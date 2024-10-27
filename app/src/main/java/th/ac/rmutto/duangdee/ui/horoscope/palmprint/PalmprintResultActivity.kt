package th.ac.rmutto.duangdee.ui.horoscope.palmprint

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

class PalmprintResultActivity : AppCompatActivity() {
    private var handDetailID: String? = null
    private var playHandScore: String? = null
    private var imagePath: String? = null

    private lateinit var encryption: Encryption
    private var tokens: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPref = getSharedPreferences("DuangDee_Pref", Context.MODE_PRIVATE)
        val token = sharedPref.getString("token", null)

        encryption = Encryption(this)
        tokens = decrypt(token.toString(), encryption.getKeyFromPreferences())
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_palmprint_result)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val pageType = intent.getStringExtra("page_type")
        if (pageType == null){
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        if (pageType == "PalmprintCamera"){
            handDetailID = intent.getStringExtra("HandDetail_ID")
            playHandScore = intent.getStringExtra("PlayHand_Score")
            imagePath = intent.getStringExtra("ImagePath")
        }

        if (pageType == "PalmprintCamera"){
            getHandDetail(handDetailID.toString())
        }

        val backBtn = findViewById<Button>(R.id.backBtn)
        val verifyBtn = findViewById<Button>(R.id.VerifyBtn)

        backBtn.setOnClickListener {
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        verifyBtn.setOnClickListener {
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getHandDetail(handDetailID : String) {
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
                findViewById<TextView>(R.id.txt_HandName).text = obj["HandDetail_Name"].toString()
                findViewById<TextView>(R.id.txt_detail1).text = "การทำนาย: " + obj["HandDetail_Detail"].toString()
                findViewById<TextView>(R.id.txt_percent).text = "ความโชคดีของคุณได้ $playHandScore%"

                if (imagePath != "null") {
                    val imageZodiac = findViewById<ImageView>(R.id.imageZodiac)
                    url = getString(R.string.url_server)+ getString(R.string.port_3000) + imagePath.toString()
                    // Load image using Glide
                    Glide.with(this)
                        .load(url)
                        .into(imageZodiac)
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
}