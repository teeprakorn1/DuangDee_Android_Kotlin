package th.ac.rmutto.duangdee.ui.otp

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import th.ac.rmutto.duangdee.R
import th.ac.rmutto.duangdee.ui.login.LoginActivity
import th.ac.rmutto.duangdee.ui.login.SendPasswordActivity
import th.ac.rmutto.duangdee.ui.register.RegisMailActivity

class ConfirmOtpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()
        setContentView(R.layout.activity_confirm_otp)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ConfirmOtp_Activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        var password: String? = null; var username: String? = null
        val pageType = intent.getStringExtra("page_type")
        val email = intent.getStringExtra("email")

        if (pageType == null || email == null){
            intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        val emailTextView = findViewById<TextView>(R.id.EmailTxtView);
        val confirmButton = findViewById<Button>(R.id.Confirm_Btn);
        val backButton = findViewById<Button>(R.id.Back_Btn);

        emailTextView.text = email

        confirmButton.setOnClickListener{
            if (pageType == "Register"){
                username = intent.getStringExtra("username")
                password = intent.getStringExtra("password")

                if (email != null){
                    val url = getString(R.string.url_server) + getString(R.string.api_request_register)
                    val okHttpClient = OkHttpClient()
                    val formBody: RequestBody = FormBody.Builder()
                        .add("Users_Email", email)
                        .add("Value", "0")
                        .build()
                    val request: Request = Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build()

                    val response = okHttpClient.newCall(request).execute()
                    if(response.isSuccessful) {
                        val obj = JSONObject(response.body!!.string())
                        val status = obj["status"].toString()
                        if (status == "true") {
                            intent = Intent(this, AcceptOtpActivity::class.java)
                            intent.putExtra("page_type",pageType)
                            intent.putExtra("username",username)
                            intent.putExtra("password",password)
                            intent.putExtra("email",email)
                            startActivity(intent)
                            finish()
                        }else if(status == "false"){
                            val message = obj["message"].toString()
                            Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                            return@setOnClickListener
                        }
                    }else {
                        Toast.makeText(applicationContext, "เกิดข้อผิดพลาดในการเชื่อมต่อ", Toast.LENGTH_LONG).show()
                        return@setOnClickListener
                    }
                }
            }else if (pageType == "ResetPassword"){
                if (email != null){
                    var url = getString(R.string.url_server) + getString(R.string.api_check_email)

                    val okHttpClient = OkHttpClient()
                    var formBody: RequestBody = FormBody.Builder()
                        .add("Users_Email",email)
                        .build()
                    var request: Request = Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build()

                    var response = okHttpClient.newCall(request).execute()
                    if(response.isSuccessful) {
                        var obj = JSONObject(response.body!!.string())
                        var status = obj["status"].toString()
                        if (status == "false") {
                            url = getString(R.string.url_server) + getString(R.string.api_request_password)
                            formBody = FormBody.Builder()
                                .add("Users_Email", email)
                                .add("Value","0")
                                .build()
                            request = Request.Builder()
                                .url(url)
                                .post(formBody)
                                .build()

                            response = okHttpClient.newCall(request).execute()
                            if(response.isSuccessful) {
                                obj = JSONObject(response.body!!.string())
                                status = obj["status"].toString()
                                if (status == "true") {
                                    val intent = Intent(this, AcceptOtpActivity::class.java)
                                    intent.putExtra("page_type",pageType)
                                    intent.putExtra("email",email)
                                    startActivity(intent)
                                    finish()
                                }else if (status == "false") {
                                    val message = obj["message"].toString()
                                    Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                                    return@setOnClickListener
                                }
                            }else{
                                Toast.makeText(applicationContext, "เกิดข้อผิดพลาดในการเชื่อมต่อ", Toast.LENGTH_LONG).show()
                                return@setOnClickListener
                            }
                        }else if(status == "true"){
                            val message = obj["message"].toString()
                            Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                            return@setOnClickListener
                        }
                    }else{
                        Toast.makeText(applicationContext, "เกิดข้อผิดพลาดในการเชื่อมต่อ", Toast.LENGTH_LONG).show()
                        return@setOnClickListener
                    }
                }
            }else{
                intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        backButton.setOnClickListener{
            if (pageType == "Register"){
                intent = Intent(this, RegisMailActivity::class.java)
                startActivity(intent)
                finish()
            }else if (pageType == "ResetPassword"){
                intent = Intent(this, SendPasswordActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}