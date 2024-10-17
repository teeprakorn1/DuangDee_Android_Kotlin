package th.ac.rmutto.duangdee.ui.register

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
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
import th.ac.rmutto.duangdee.PrivacyPolicyActivity
import th.ac.rmutto.duangdee.R
import th.ac.rmutto.duangdee.ui.login.LoginActivity

class RegisMailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()
        setContentView(R.layout.activity_regis_mail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.RegisEmail_Activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val nextButton = findViewById<Button>(R.id.Next_Btn);
        val bt_Back = findViewById<Button>(R.id.bt_back);
        val privacyPolicy = findViewById<TextView>(R.id.txt_PrivacyPolicy);
        val editTextRegisEmail = findViewById<EditText>(R.id.editTextRegisEmail)
        val checkBoxPolicy = findViewById<CheckBox>(R.id.checkBoxPolicy)
        val checkBoxAge = findViewById<CheckBox>(R.id.checkBoxAge)

        nextButton.setOnClickListener{
            //Check email
            val emailEdt = editTextRegisEmail.text.toString()
            if (emailEdt.isEmpty() || emailEdt.isBlank()) {
                editTextRegisEmail.error = "Please enter a your email."
                return@setOnClickListener
            }else if(!emailEdt.contains("@")){
                editTextRegisEmail.error = "Please enter a valid email."
                return@setOnClickListener
            }else if(!emailEdt.contains(".")){
                editTextRegisEmail.error = "Please enter a valid email."
                return@setOnClickListener
            }else if(emailEdt.length < 10){
                editTextRegisEmail.error = "Please enter a valid email."
                return@setOnClickListener
            }else if(emailEdt.length > 30){
                editTextRegisEmail.error = "Please enter a val" +
                        "                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 id email."
                return@setOnClickListener
            }else if(checkBoxAge.isChecked == false){
                Toast.makeText(applicationContext, "Please check age.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }else if(checkBoxPolicy.isChecked == false) {
                Toast.makeText(applicationContext, "Please check", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val url = getString(R.string.url_server) + getString(R.string.api_check_email)

            val okHttpClient = OkHttpClient()
            val formBody: RequestBody = FormBody.Builder()
                .add("Users_Email",emailEdt)
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
                    val intent = Intent(this, RegisUserActivity::class.java)
                    intent.putExtra("email",emailEdt)
                    startActivity(intent)
                    finish()
                }else if(status == "false"){
                    val message = obj["message"].toString()
                    Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
            }
        }
        privacyPolicy.setOnClickListener {
            val intent = Intent(this, PrivacyPolicyActivity::class.java)
            startActivity(intent)
        }

        bt_Back.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}