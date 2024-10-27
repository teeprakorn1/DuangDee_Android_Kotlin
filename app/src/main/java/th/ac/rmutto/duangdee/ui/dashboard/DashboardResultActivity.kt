package th.ac.rmutto.duangdee.ui.dashboard

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import th.ac.rmutto.duangdee.MainActivity
import th.ac.rmutto.duangdee.R
import th.ac.rmutto.duangdee.shared_preferences_encrypt.Encryption
import th.ac.rmutto.duangdee.shared_preferences_encrypt.Encryption.Companion.decrypt
import th.ac.rmutto.duangdee.ui.horoscope.palmprint.PalmprintResultActivity
import th.ac.rmutto.duangdee.ui.horoscope.zodiac.ZodiacResultActivity
import th.ac.rmutto.duangdee.ui.login.LoginActivity
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.round

class DashboardResultActivity : AppCompatActivity() {
    private lateinit var playHandID: String
    private lateinit var playHandScore: String
    private lateinit var handDetailID: String

    private lateinit var playCardID: String
    private lateinit var cardID: String

    private lateinit var zodiacID: String
    private lateinit var zodiacScore: String

    private lateinit var cardWorkScore: String
    private lateinit var cardFinanceScore: String
    private lateinit var cardLoveScore: String

    private lateinit var summaryDetail: String

    private lateinit var sumScore: String

    private lateinit var encryption: Encryption
    private var tokens: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPref = getSharedPreferences("DuangDee_Pref", Context.MODE_PRIVATE)
        val token = sharedPref.getString("token", null)
        val usersID = sharedPref.getString("usersID", null)

        encryption = Encryption(this)
        tokens = decrypt(token.toString(), encryption.getKeyFromPreferences())
        val decode = decrypt(usersID.toString(), encryption.getKeyFromPreferences())
        val pageType = intent.getStringExtra("page_type")

        if (usersID == null || tokens == null || pageType != "Dashboard") {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard_result)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.dashboard_result_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (pageType == "Dashboard") {
            getSummary(decode)
        }

        val btBack = findViewById<Button>(R.id.bt_back)
        btBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getSummary(usersID: String){
        val url = getString(R.string.url_server) + getString(R.string.port_3000) + getString(R.string.api_get_summary_inday_top1) + usersID
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
                setDisplayData(usersID)
            }else if(status == "false"){
                getPlayHandInWeek(usersID)
            } else {
                getPlayHandInWeek(usersID)
            }
        } else {
            getPlayHandInWeek(usersID)
        }
    }

    private fun getPlayHandInWeek(usersID: String){
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
                playHandID = obj["PlayHand_ID"].toString()
                playHandScore = obj["PlayHand_Score"].toString()
                handDetailID = obj["HandDetail_ID"].toString()
                getPlayCardInDay(usersID)
            }else if(status == "false"){
                dialogWarning()
            } else {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        } else {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getPlayCardInDay(usersID: String){
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
                playCardID = obj["PlayCard_ID"].toString()
                cardID = obj["Card_ID"].toString()
                getCardDetail(usersID, cardID)
            }else if(status == "false"){
                dialogWarning()
            } else {
                dialogWarning()
            }
        } else {
            dialogWarning()
        }
    }

    private fun getCardDetail(usersID: String, cardID: String){
        val url = getString(R.string.url_server) + getString(R.string.port_3000) + getString(R.string.api_get_card) + cardID
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
                cardWorkScore = obj["Card_WorkScore"].toString()
                cardFinanceScore = obj["Card_FinanceScore"].toString()
                cardLoveScore = obj["Card_LoveScore"].toString()
                val usersDateOfBirth = getUserBirthday(usersID)
                checkZodiacDay(usersDateOfBirth, usersID)

            }else if(status == "false"){
                dialogWarning()
            } else {
                dialogWarning()
            }
        } else {
            dialogWarning()
        }
    }

    private fun getUserBirthday(usersID : String) : String {
        val url = getString(R.string.url_server) + getString(R.string.port_3000) + getString(R.string.api_get_profile) + usersID
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
                    dialogWarning()
                }
            } else {
                dialogWarning()
            }
        } catch (e: Exception) {
            dialogWarning()
        }
        return "N/A"
    }

    private fun checkZodiacDay(usersDateOfBirth: String , usersID: String){
        if (usersDateOfBirth == "N/A" || usersDateOfBirth == "null"){
            dialogWarning()
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
                    zodiacID = obj["Zodiac_ID"].toString()
                    getZodiacByID(zodiacID, usersID)
                }else{
                    dialogWarning()
                }
            }else{
                dialogWarning()
            }
        }
    }

    private fun getZodiacByID(zodiacIDs: String , usersID: String){
        val url = getString(R.string.url_server) + getString(R.string.port_3000) + getString(R.string.api_get_zodiac) + zodiacIDs
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
                zodiacScore = obj["Zodiac_Score"].toString()
                sumScore = processSummary(zodiacScore.toDouble(),playHandScore.toDouble(),
                    cardWorkScore.toDouble(),cardFinanceScore.toDouble(),cardLoveScore.toDouble()).toString()
                val summaryDetailID = getSummaryDetailID(sumScore.toFloat())
                addSummary(sumScore, usersID, zodiacID, playCardID, playHandID, summaryDetailID.toString())
            }else if(status == "false"){
                dialogWarning()
            } else {
                dialogWarning()
            }
        } else {
            dialogWarning()
        }
    }

    private fun processSummary(zodiacScore : Double,playHandScore : Double,
        cardWorkScore : Double,cardFinanceScore : Double,cardLoveScore : Double) : Double {
        val average = (zodiacScore + playHandScore + cardWorkScore +
                cardFinanceScore + cardLoveScore) / 5
        return average
    }

    private fun addSummary(summaryScore : String, usersID : String,
        zodiacID : String, playCardID : String, playHandID : String, summaryDetailID : String){
        val url = getString(R.string.url_server) + getString(R.string.port_3000) + getString(R.string.api_add_summary)
        val okHttpClient = OkHttpClient()
        val formBody: RequestBody = FormBody.Builder()
            .add("Summary_TotalScore", summaryScore)
            .add("Users_ID", usersID)
            .add("Zodiac_ID", zodiacID)
            .add("PlayCard_ID", playCardID)
            .add("PlayHand_ID", playHandID)
            .add("SummaryDetail_ID", summaryDetailID)
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
                setDisplayData(usersID)
            }else{
                dialogWarning()
            }
        }else{
            dialogWarning()
        }
    }

    private fun getSummaryDetailID( summaryScore : Float) : Int {
        var summaryDetailID = -1
        if (summaryScore > getSummaryDetail(10)){
            summaryDetailID = 10
        }else if (summaryScore > getSummaryDetail(9)){
            summaryDetailID = 9
        }else if (summaryScore > getSummaryDetail(8)){
            summaryDetailID = 8
        }else if (summaryScore > getSummaryDetail(7)){
            summaryDetailID = 7
        }else if (summaryScore > getSummaryDetail(6)){
            summaryDetailID = 6
        }else if (summaryScore > getSummaryDetail(5)){
            summaryDetailID = 5
        }else if (summaryScore > getSummaryDetail(4)){
            summaryDetailID = 7
        }else if (summaryScore > getSummaryDetail(3)){
            summaryDetailID = 3
        }else if (summaryScore > getSummaryDetail(2)){
            summaryDetailID = 2
        }else if (summaryScore > getSummaryDetail(1)){
            summaryDetailID = 1
        }
        return summaryDetailID
    }

    private fun getSummaryDetail(summaryDetailID : Int) : Float{
        val url = getString(R.string.url_server) + getString(R.string.port_3000) + getString(R.string.api_get_summarydetail) + summaryDetailID
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
                return obj["SummaryDetail_MinPercent"].toString().toFloat()
            } else {
                dialogWarning()
            }
        } else {
            dialogWarning()
        }
        return -1f
    }
    private fun getSummaryData(usersID: String){
        val url = getString(R.string.url_server) + getString(R.string.port_3000) + getString(R.string.api_get_summary) + usersID
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
                sumScore = obj["Summary_TotalScore"].toString()
                zodiacScore = obj["Zodiac_Score"].toString()
                playHandScore = obj["PlayHand_Score"].toString()
                cardWorkScore = obj["Card_WorkScore"].toString()
                cardFinanceScore = obj["Card_FinanceScore"].toString()
                cardLoveScore = obj["Card_LoveScore"].toString()
                summaryDetail = obj["SummaryDetail_Detail"].toString()
            } else {
                dialogWarning()
            }
        } else {
            dialogWarning()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setTextData(){
        setColorData(sumScore.toDouble(),findViewById(R.id.txt_samary_percent),findViewById(R.id.txt_samary_class))
        setColorData(zodiacScore.toDouble(),findViewById(R.id.txt_Zodiac_percent),findViewById(R.id.txt_Zodiac_Class))
        setColorData(cardWorkScore.toDouble(),findViewById(R.id.txt_work_percent),findViewById(R.id.txt_work_Class))
        setColorData(cardFinanceScore.toDouble(),findViewById(R.id.txt_Money_percent),findViewById(R.id.txt_Money_Class))
        setColorData(cardLoveScore.toDouble(),findViewById(R.id.txt_love_percent),findViewById(R.id.txt_love_Class))
        setColorData(playHandScore.toDouble(),findViewById(R.id.txt_Palm_percent),findViewById(R.id.txt_Palm_Class))
        findViewById<TextView>(R.id.txt_detail).text = "คุณมีดวงที่: $summaryDetail"
    }

    private fun setDisplayData(usersID: String){
        getSummaryData(usersID)
        setTextData()
    }

    @SuppressLint("SetTextI18n")
    private fun setColorData(value : Double, textView1: TextView ,textView2: TextView){
        if (percentIf(round(value)) == "ดีมาก"){
            textView1.text = round(value).toString()+"%"
            textView1.setTextColor(getColor(R.color.very_green))
            textView2.text = "ดีมาก"
            textView2.setTextColor(getColor(R.color.very_green))
        }else if (percentIf(round(value)) == "ดี"){
            textView1.text = round(value).toString()+"%"
            textView1.setTextColor(getColor(R.color.green))
            textView2.text = "ดี"
            textView2.setTextColor(getColor(R.color.green))
        }else if (percentIf(round(value)) == "ปานกลาง"){
            textView1.text = round(value).toString()+"%"
            textView1.setTextColor(getColor(R.color.orange))
            textView2.text = "ปานกลาง"
            textView2.setTextColor(getColor(R.color.orange))
        }else if (percentIf(round(value)) == "แย่"){
            textView1.text = round(value).toString()+"%"
            textView1.setTextColor(getColor(R.color.orange_red))
            textView2.text = "แย่"
            textView2.setTextColor(getColor(R.color.orange_red))
        }else if (percentIf(round(value)) == "แย่มาก"){
            textView1.text = round(value).toString()+"%"
            textView1.setTextColor(getColor(R.color.red))
            textView2.text = "แย่มาก"
            textView2.setTextColor(getColor(R.color.red))
        }
    }

    private fun percentIf(value : Double): String{
        if (value > 80){
            return "ดีมาก"
        }else if (value > 60){
            return "ดี"
        }else if (value > 40){
            return "ปานกลาง"
        }else if(value > 20){
            return "แย่"
        }else if(value > 0){
            return "แย่มาก"
        }else{
            return "ไม่มีข้อมูล"
        }
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

    private fun dialogWarning() {
        val dialogView = LayoutInflater.from(this ).inflate(R.layout.dialog_nosummary, null)
        val dialogBuilder = AlertDialog.Builder(this ).setView(dialogView)
        val dialog = dialogBuilder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val txtMessage = dialogView.findViewById<TextView>(R.id.textviewshowmassege1)
        val btYes = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btYes)
        txtMessage.text = "คุณจำเป็นต้องเล่นการดูดวงรายมือก่อน ถึงจะสามารถดูประวัติการเล่นของคุณได้"
        btYes.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        dialog.show()
    }
}