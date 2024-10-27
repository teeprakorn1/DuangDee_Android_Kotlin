package th.ac.rmutto.duangdee.ui.history

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import th.ac.rmutto.duangdee.MainActivity
import th.ac.rmutto.duangdee.R
import th.ac.rmutto.duangdee.shared_preferences_encrypt.Encryption
import th.ac.rmutto.duangdee.shared_preferences_encrypt.Encryption.Companion.decrypt
import th.ac.rmutto.duangdee.ui.contact.ContactActivity
import th.ac.rmutto.duangdee.ui.login.LoginActivity
import th.ac.rmutto.duangdee.ui.profile.EditProfileActivity

class HistoryPalmFragment : Fragment() {
    private lateinit var encryption: Encryption
    private var tokens: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Encryption
        encryption = Encryption(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_history_palm, container, false)

        // Start Decryption SharedPreferences
        val sharedPref = requireActivity().getSharedPreferences("DuangDee_Pref", Context.MODE_PRIVATE)
        val usersID = sharedPref.getString("usersID", null)
        val token = sharedPref.getString("token", null)

        // Attempt to decrypt the token
        tokens = token?.let { decrypt(it, encryption.getKeyFromPreferences()) }
        val decode = usersID?.let { decrypt(it, encryption.getKeyFromPreferences()) }
        if (decode != null) {
            getPlayHand(decode.toString(), view)
        }
        return view
    }

    private fun getPlayHand(usersID : String, view: View){
        val url = getString(R.string.url_server) + getString(R.string.port_3000) + getString(R.string.api_get_playhand_top1) + usersID
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder()
            .url(url)
            .get()
            .addHeader("x-access-token", tokens.toString())
            .build()
        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val obj = JSONObject(response.body!!.string())
                val status = obj["status"].toString()
                if (status == "true") {
                    val playHandImageFile = obj.optString("PlayHand_ImageFile", "N/A").toString()
                    val playHandScore = obj.optString("PlayHand_Score", "N/A").toString()
                    val handDetailID = obj.optString("HandDetail_ID", "N/A").toString()
                    getHandDetail(handDetailID, playHandScore, playHandImageFile, view)
                }else{
                    dialogWarning()
                }
            }else{
                dialogWarning()
            }
        } catch (e: Exception) {
            startActivity(Intent(activity, MainActivity::class.java))
            activity?.finish()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getHandDetail(handDetailID : String, playHandScore : String, imagePath : String, view: View) {
        var url = getString(R.string.url_server) + getString(R.string.port_3000) + getString(R.string.api_get_handdetail) + handDetailID
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder()
            .url(url)
            .get()
            .addHeader("x-access-token", tokens.toString())
            .build()
        val response = okHttpClient.newCall(request).execute()
        if (response.isSuccessful) {
            val obj = JSONObject(response.body!!.string())
            val status = obj["status"].toString()
            if (status == "true") {
                view.findViewById<TextView>(R.id.txt_HandName).text = obj["HandDetail_Name"].toString()
                view.findViewById<TextView>(R.id.txt_detail1).text = "การทำนาย: " + obj["HandDetail_Detail"].toString()
                view.findViewById<TextView>(R.id.txt_percent).text = "ความโชคดีของคุณได้ $playHandScore%"

                if (imagePath != "null") {
                    val imageZodiac = view.findViewById<ImageView>(R.id.imageZodiac)
                    url = getString(R.string.url_server)+ getString(R.string.port_3000) + imagePath
                    // Load image using Glide
                    Glide.with(this)
                        .load(url)
                        .into(imageZodiac)
                }
            } else {
                startActivity(Intent(activity, MainActivity::class.java))
                activity?.finish()
            }
        } else {
            startActivity(Intent(activity, MainActivity::class.java))
            activity?.finish()
        }
    }

    private fun dialogWarning() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_nosummary, null)
        val dialogBuilder = AlertDialog.Builder(requireContext()).setView(dialogView)
        val dialog = dialogBuilder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val txtMessage = dialogView.findViewById<TextView>(R.id.textviewshowmassege1)
        val btYes = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btYes)
        txtMessage.text = "คุณจำเป็นต้องเล่นการดูดวงรายมือก่อน ถึงจะสามารถดูประวัติการเล่นของคุณได้"
        btYes.setOnClickListener {
            startActivity(Intent(activity, MainActivity::class.java))
            activity?.finish()
        }
        dialog.show()
    }
}