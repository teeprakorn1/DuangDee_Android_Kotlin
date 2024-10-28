package th.ac.rmutto.duangdee.ui.home

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import th.ac.rmutto.duangdee.MainActivity
import th.ac.rmutto.duangdee.R
import th.ac.rmutto.duangdee.shared_preferences_encrypt.Encryption
import th.ac.rmutto.duangdee.shared_preferences_encrypt.Encryption.Companion.decrypt
import th.ac.rmutto.duangdee.ui.horoscope.palmprint.PalmprintCameraActivity
import th.ac.rmutto.duangdee.ui.horoscope.tarot.TarotActivity
import th.ac.rmutto.duangdee.ui.horoscope.zodiac.ZodiacResultActivity
import th.ac.rmutto.duangdee.ui.login.LoginActivity
import th.ac.rmutto.duangdee.ui.profile.EditProfileActivity
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class HomeFragment : Fragment() {

    private lateinit var encryption: Encryption

    private lateinit var usersDateOfBirth: String
    private lateinit var userID: String
    private var tokens: String? = null
    private lateinit var lottie: LottieAnimationView

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
        val btHand = view.findViewById<ImageView>(R.id.bt_hand)
        lottie = view.findViewById<LottieAnimationView>(R.id.lottie_loading)

        btZodiac.setOnClickListener {
            lottie.visibility = View.VISIBLE
            lottie.playAnimation()
            userID = getUserId()
            usersDateOfBirth = getUserBirthday(userID)
            if (usersDateOfBirth == "N/A" || usersDateOfBirth == "null"){
                lottie.visibility = View.GONE
                showDialog()
            }else{
                val birthDayFormat = dateFormat(usersDateOfBirth)
                val url = getString(R.string.url_server) + getString(R.string.port_3000) + getString(R.string.api_check_zodiac)
                val okHttpClient = OkHttpClient()
                val formBody: RequestBody = FormBody.Builder()
                    .add("Users_BirthDate", birthDayFormat)
                    .build()
                val request: Request = Request.Builder()
                    .url(url)
                    .post(formBody)
                    .addHeader("x-access-token", tokens.toString())
                    .build()
                val response = okHttpClient.newCall(request).execute()
                if(response.isSuccessful){
                    val obj = JSONObject(response.body!!.string())
                    val status = obj["status"].toString()
                    if (status == "true") {
                        val zodiacID = obj["Zodiac_ID"].toString()
                        Handler(Looper.getMainLooper()).postDelayed({
                            val intent = Intent(activity, ZodiacResultActivity::class.java)
                            intent.putExtra("zodiacID", zodiacID)
                            intent.putExtra("page_type","Home")
                            startActivity(intent)
                            activity?.finish()
                        }, 500) // เวลาในหน่วยมิลลิวินาที (3 วินาที)
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
            lottie.visibility = View.VISIBLE
            lottie.playAnimation()
            Handler(Looper.getMainLooper()).postDelayed({
                if(getPlayCardInDay(getUserId()) == 1){
                    val intent = Intent(activity, TarotActivity::class.java)
                    intent.putExtra("page_type","Home")
                    startActivity(intent)
                    activity?.finish()
                }else{
                    dialogWarning("คุณมีการรับคำทำนายของการเล่นไพ่วันนี้ไปแล้ว สามารถเล่นใหม่ได้อีกในวันพรุ่งนี้",view)
                }
            },500)
        }

        btHand.setOnClickListener{
            lottie.visibility = View.VISIBLE
            lottie.playAnimation()
            Handler(Looper.getMainLooper()).postDelayed({
                if(getPlayHandInWeek(getUserId()) == 1){
                    val intent = Intent(activity, PalmprintCameraActivity::class.java)
                    intent.putExtra("page_type","Home")
                    startActivity(intent)
                    activity?.finish()
                }else{
                    dialogWarning("คุณมีการทำนายผลรายมือไปแล้ว แล้วมาทำนายใหม่ อีก 7 วัน",view)
                }
            },500)
        }

        return view
    }

    private fun getUserId() : String {
        val sharedPref = requireActivity().getSharedPreferences("DuangDee_Pref", Context.MODE_PRIVATE)
        val usersID = sharedPref.getString("usersID", null)
        val token = sharedPref.getString("token", null)

        tokens = token?.let { decrypt(it, encryption.getKeyFromPreferences()) }
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
        val url = getString(R.string.url_server) + getString(R.string.port_3000) + getString(R.string.api_get_profile) + value
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

    private fun getPlayHandInWeek(usersID: String): Int{
        val url = getString(R.string.url_server) + getString(R.string.port_3000) + getString(R.string.api_get_playhand_inweek_top1) + usersID
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
                return 0
            }else if(status == "false"){
                return 1
            } else {
                startActivity(Intent(activity, MainActivity::class.java))
                activity?.finish()
            }
        } else {
            startActivity(Intent(activity, MainActivity::class.java))
            activity?.finish()
        }
        return 0
    }

    private fun getPlayCardInDay(usersID: String): Int{
        val url = getString(R.string.url_server) + getString(R.string.port_3000) + getString(R.string.api_get_playcard_inday_top1) + usersID
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
                return 0
            }else if(status == "false"){
                return 1
            } else {
                startActivity(Intent(activity, MainActivity::class.java))
                activity?.finish()
            }
        } else {
            startActivity(Intent(activity, MainActivity::class.java))
            activity?.finish()
        }
        return 0
    }

    private fun dialogWarning(text : String = "", view : View) {
        val dialogView = LayoutInflater.from(requireContext() ).inflate(R.layout.dialog_nosummary, null)
        val dialogBuilder = AlertDialog.Builder(requireContext() ).setView(dialogView)
        val dialog = dialogBuilder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val txtMessage = dialogView.findViewById<TextView>(R.id.textviewshowmassege1)
        val btYes = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btYes)
        txtMessage.text = text
        btYes.setOnClickListener {
            view.findViewById<LottieAnimationView>(R.id.lottie_loading).visibility = View.GONE
            dialog.dismiss()
        }
        dialog.show()
    }
}
