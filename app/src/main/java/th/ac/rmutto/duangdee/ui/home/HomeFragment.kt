package th.ac.rmutto.duangdee.ui.home

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import th.ac.rmutto.duangdee.R
import th.ac.rmutto.duangdee.shared_preferences_encrypt.Encryption
import th.ac.rmutto.duangdee.shared_preferences_encrypt.Encryption.Companion.decrypt
import th.ac.rmutto.duangdee.ui.login.LoginActivity
import th.ac.rmutto.duangdee.ui.profile.EditProfileActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var encryption: Encryption

    private lateinit var usersDateOfBirth: String
    private lateinit var userID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        encryption = Encryption(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val btZodiac = view.findViewById<ImageButton>(R.id.bt_zodiac)

        btZodiac.setOnClickListener {
            userID = getUserId()
            usersDateOfBirth = getUserBirthday(userID)
            if (usersDateOfBirth == "N/A" || usersDateOfBirth == "null"){
                showDialog()
            }
        }

        return view
    }

    private fun getUserId() : String {
        val sharedPref = requireActivity().getSharedPreferences("DuangDee_Pref", Context.MODE_PRIVATE)
        val usersID = sharedPref.getString("usersID", null)

        val decode = usersID?.let { decrypt(it, encryption.getKeyFromPreferences()) }
        if (decode != null) {
            return decode
        } else {
            startActivity(Intent(activity, LoginActivity::class.java))
            activity?.finish()
        }
        return "N/A"
    }
    private fun getUserBirthday(value : String) : String {
        val url = getString(R.string.url_server) + getString(R.string.api_get_profile) + value
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()
        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val obj = JSONObject(response.body!!.string())
                val status = obj["status"].toString()
                if (status == "true") {
                    return obj.optString("Users_BirthDate", "N/A")
                } else {
                    startActivity(Intent(activity, LoginActivity::class.java))
                    activity?.finish()
                }
            } else {
                startActivity(Intent(activity, LoginActivity::class.java))
                activity?.finish()
            }
        } catch (e: Exception) {
            Log.e("VerifyTokenError", "Error verifying token", e)
            startActivity(Intent(activity, LoginActivity::class.java))
            activity?.finish()
        }
        return "N/A"
    }

    private fun checkTimeIf(value: String) : Int{

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date: Date? = dateFormat.parse(value)

        if (date != null && date.time > 12) {
            return 1
        } else{
            return 0
        }
    }

    private fun showDialog() {
        // Inflate the dialog's custom layout
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_zodiac_check, null)

        // Build an AlertDialog using the custom layout
        val dialogBuilder = AlertDialog.Builder(requireContext()).setView(dialogView)

        // Create and show the dialog
        val dialog = dialogBuilder.create()

        // Find the OK button in the dialog layout and set an onClickListener
        val buttonOk = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.buttonok)
        buttonOk.setOnClickListener {
            startActivity(Intent(activity, EditProfileActivity::class.java))
            activity?.finish()
            dialog.dismiss()
        }

        dialog.show()
    }
}
