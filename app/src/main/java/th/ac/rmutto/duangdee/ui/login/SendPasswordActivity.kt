package th.ac.rmutto.duangdee.ui.login

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
import th.ac.rmutto.duangdee.ui.otp.AcceptOtpActivity
import th.ac.rmutto.duangdee.ui.otp.ConfirmOtpActivity
import th.ac.rmutto.duangdee.ui.register.RegisUserActivity

class SendPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()
        setContentView(R.layout.activity_send_password)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.SendPassword_Activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val sendEmailButton = findViewById<Button>(R.id.SendEmail_btn);
        val emailEditText = findViewById<EditText>(R.id.editText_EmailAddress);

        sendEmailButton.setOnClickListener {
            val emailEdt = emailEditText.text.toString()

            //Check email
            if (emailEdt.isEmpty() || emailEdt.isBlank()) {
                emailEditText.error = "Please enter a your email."
                return@setOnClickListener
            }else if(!emailEdt.contains("@")){
                emailEditText.error = "Please enter a valid email."
                return@setOnClickListener
            }else if(!emailEdt.contains(".")){
                emailEditText.error = "Please enter a valid email."
                return@setOnClickListener
            }else if(emailEdt.length < 10){
                emailEditText.error = "Please enter a valid email."
                return@setOnClickListener
            }else if(emailEdt.length > 30){
                emailEditText.error = "Please enter a valid email."
                return@setOnClickListener
            }

            intent = Intent(this, ConfirmOtpActivity::class.java)
            intent.putExtra("email",emailEdt)
            intent.putExtra("page_type","ResetPassword")
            startActivity(intent)
            finish()

        }
    }
}