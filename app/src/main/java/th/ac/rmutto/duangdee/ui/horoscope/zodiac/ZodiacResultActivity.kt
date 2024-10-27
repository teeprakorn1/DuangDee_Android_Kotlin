package th.ac.rmutto.duangdee.ui.horoscope.zodiac

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import th.ac.rmutto.duangdee.MainActivity
import th.ac.rmutto.duangdee.R
import th.ac.rmutto.duangdee.shared_preferences_encrypt.Encryption
import th.ac.rmutto.duangdee.shared_preferences_encrypt.Encryption.Companion.decrypt
import th.ac.rmutto.duangdee.ui.login.LoginActivity
import th.ac.rmutto.duangdee.ui.profile.ProfileFragment

class ZodiacResultActivity : AppCompatActivity() {

    private var zodiacName : String? = null
    private var zodiacDetail : String? = null
    private var zodiacWorkTopic : String? = null
    private var zodiacFinanceTopic : String? = null
    private var zodiacLoveTopic : String? = null
    private var zodiacImageFile : String? = null

    private lateinit var encryption: Encryption
    private var tokens: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPref = getSharedPreferences("DuangDee_Pref", Context.MODE_PRIVATE)
        val token = sharedPref.getString("token", null)

        encryption = Encryption(this)
        tokens = decrypt(token.toString(), encryption.getKeyFromPreferences())

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()
        setContentView(R.layout.activity_zodiac_result)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val zodiacID = intent.getStringExtra("zodiacID")
        val pageType = intent.getStringExtra("page_type")
        if (zodiacID == null || pageType == null){
            intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        val backBtn = findViewById<Button>(R.id.backBtn)
        val showZodiacBtn = findViewById<Button>(R.id.showZodiacBtn)
        if(pageType == "Total"){
            showZodiacBtn.text = "หน้าหลัก"
        }

        showZodiac(zodiacID.toString())

        backBtn.setOnClickListener {
            if (pageType == "Home") {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }else if(pageType == "Total"){
                val intent = Intent(this, ZodiacTotalActivity::class.java)
                startActivity(intent)
                finish()
            }else if (pageType == "Horoscope"){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        showZodiacBtn.setOnClickListener{
            if(pageType == "Total"){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                val intent = Intent(this, ZodiacTotalActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

    }

    private fun showZodiac(zodiacID : String){
        var url = getString(R.string.url_server) + getString(R.string.port_3000) + getString(R.string.api_get_zodiac) + zodiacID
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
                zodiacName = obj["Zodiac_Name"].toString()
                zodiacDetail = obj.optString("Zodiac_Detail", "N/A")
                zodiacWorkTopic = obj.optString("Zodiac_WorkTopic", "N/A")
                zodiacFinanceTopic = obj.optString("Zodiac_FinanceTopic", "N/A")
                zodiacLoveTopic = obj.optString("Zodiac_LoveTopic", "N/A")
                zodiacImageFile = obj.optString("Zodiac_ImageFile", "N/A")
                setZodiac()

                if (zodiacImageFile != "null") {
                    val imageProfile = findViewById<ImageView>(R.id.imageZodiac)
                    url = getString(R.string.url_server) + getString(R.string.port_3000) + zodiacImageFile.toString()
                    // Load image using Glide
                    Glide.with(this)
                        .load(url)
                        .into(imageProfile)
                }
            } else {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        } else {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setZodiac(){
        findViewById<TextView>(R.id.txt_ZodiacName).text = zodiacName
        findViewById<TextView>(R.id.txt_detail).text = zodiacDetail
        findViewById<TextView>(R.id.txt_WorkTopic).text = "การงาน: $zodiacWorkTopic"
        findViewById<TextView>(R.id.txt_FinanceTopic).text = "การเงิน: $zodiacFinanceTopic"
        findViewById<TextView>(R.id.txt_LoveTopic).text = "ความรัก: $zodiacLoveTopic"
    }
}