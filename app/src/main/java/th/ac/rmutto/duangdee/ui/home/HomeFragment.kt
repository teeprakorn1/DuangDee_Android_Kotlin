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
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import th.ac.rmutto.duangdee.R
import th.ac.rmutto.duangdee.shared_preferences_encrypt.Encryption
import th.ac.rmutto.duangdee.shared_preferences_encrypt.Encryption.Companion.decrypt
import th.ac.rmutto.duangdee.ui.horoscope.tarot.TarotActivity
import th.ac.rmutto.duangdee.ui.horoscope.zodiac.ZodiacResultActivity
import th.ac.rmutto.duangdee.ui.login.LoginActivity
import th.ac.rmutto.duangdee.ui.profile.EditProfileActivity
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
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
        val btTarot = view.findViewById<ImageButton>(R.id.bt_tarot)

        btZodiac.setOnClickListener {
            userID = getUserId()
            usersDateOfBirth = getUserBirthday(userID)
            if (usersDateOfBirth == "N/A" || usersDateOfBirth == "null"){
                showDialog()
            }else{
                val birthDayFormat = dateFormat(usersDateOfBirth)
                val url = getString(R.string.url_server) + getString(R.string.api_check_zodiac)
                val okHttpClient = OkHttpClient()
                val formBody: RequestBody = FormBody.Builder()
                    .add("Users_BirthDate", birthDayFormat)
                    .build()
                val request: Request = Request.Builder()
                    .url(url)
                    .post(formBody)
                    .build()
                val response = okHttpClient.newCall(request).execute()
                if(response.isSuccessful){
                    val obj = JSONObject(response.body!!.string())
                    val status = obj["status"].toString()
                    if (status == "true") {
                        val zodiacID = obj["Zodiac_ID"].toString()
                        val intent = Intent(activity, ZodiacResultActivity::class.java)
                        intent.putExtra("zodiacID", zodiacID)
                        intent.putExtra("page_type","Home")
                        startActivity(intent)
                        activity?.finish()
                    }else{
                        val message = obj["message"].toString()
                        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                    }
                }else{
                    Toast.makeText(requireContext(), "ไม่สามารถเชื่อต่อกับเซิร์ฟเวอร์ได้", Toast.LENGTH_LONG).show()
                }
            }
        }

        btTarot.setOnClickListener {
            val intent = Intent(activity, TarotActivity::class.java)
            intent.putExtra("page_type","Home")
            startActivity(intent)
            activity?.finish()
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

    private fun dateFormat(value : String): String{
        if (value == "N/A" || value == "null"){
            return value
        }else{
            val instant = Instant.parse(value)
            val localDate = instant.atZone(ZoneId.of("Asia/Bangkok")).toLocalDate()
            val formattedDate = localDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
            return formattedDate;
        }
    }

    private fun showDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_zodiac_check, null)
        val dialogBuilder = AlertDialog.Builder(requireContext()).setView(dialogView)
        val dialog = dialogBuilder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Find the OK button in the dialog layout and set an onClickListener
        val editBtn = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.editBtn)
        val backBtn = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.backBtn)

        editBtn.setOnClickListener {
            startActivity(Intent(activity, EditProfileActivity::class.java))
            activity?.finish()
            dialog.dismiss()
        }

        backBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}
