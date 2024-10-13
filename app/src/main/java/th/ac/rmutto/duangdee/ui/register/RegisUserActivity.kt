package th.ac.rmutto.duangdee.ui.register

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.widget.Button
import android.widget.EditText
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
import th.ac.rmutto.duangdee.ui.otp.ConfirmOtpActivity

class RegisUserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()
        setContentView(R.layout.activity_regis_user)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.RegisUser_Activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val email = intent.getStringExtra("email")

        val nextButton = findViewById<Button>(R.id.Next_Btn);
        val bt_Back = findViewById<Button>(R.id.bt_back);
        val editTextUsername = findViewById<EditText>(R.id.editTextRegisUsername);
        val editTextPassword = findViewById<EditText>(R.id.editTextRegisPassword);
        val editTextConfirmPassword = findViewById<EditText>(R.id.editTextRegisConfirmPassword);

        if (email == null){
            intent = Intent(this, RegisMailActivity::class.java)
            startActivity(intent)
        }

        nextButton.setOnClickListener{
            val usernameEdt = editTextUsername.text.toString()
            val passwordEdt = editTextPassword.text.toString()
            val confirmPasswordEdt = editTextConfirmPassword.text.toString()

            if(usernameEdt.isEmpty() || passwordEdt.isEmpty() || confirmPasswordEdt.isEmpty()) {
                editTextUsername.error = "Please enter username and password"
                return@setOnClickListener
            }else if (usernameEdt.length < 8){
                editTextUsername.error = "Username must be at least 8 characters"
                return@setOnClickListener
            }else if (passwordEdt.length < 8 || confirmPasswordEdt.length < 8){
                editTextPassword.error = "Password must be at least 8 characters"
                return@setOnClickListener
            }else if (passwordEdt != confirmPasswordEdt){
                editTextConfirmPassword.error = "Password not match"
                return@setOnClickListener
            }

            val url = getString(R.string.url_server) + getString(R.string.api_check_username)
            val okHttpClient = OkHttpClient()
            val formBody: RequestBody = FormBody.Builder()
                .add("Users_Username", usernameEdt)
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
                    intent = Intent(this, ConfirmOtpActivity::class.java)
                    intent.putExtra("page_type","Register")
                    intent.putExtra("username",usernameEdt)
                    intent.putExtra("password",passwordEdt)
                    intent.putExtra("email",email)
                    startActivity(intent)
                    finish()
                }else if(status == "false"){
                    val message = obj["message"].toString()
                    Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                }
            }
        }

        bt_Back.setOnClickListener {
            val intent = Intent(this, RegisMailActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}