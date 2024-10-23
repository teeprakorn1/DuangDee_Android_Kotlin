package th.ac.rmutto.duangdee.ui.login

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
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

class ResetPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()
        setContentView(R.layout.activity_reset_password)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ResetPassword_Activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val email = intent.getStringExtra("email")

        if (email == null){
            intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        val confirmButton = findViewById<Button>(R.id.Confirm_Password_Btn);
        val bt_Back = findViewById<ImageView>(R.id.bt_back);
        val editTextPassword = findViewById<EditText>(R.id.editText_Password);
        val editTextConfirmPassword = findViewById<EditText>(R.id.editText_confirmPassword);


        confirmButton.setOnClickListener {
            val passwordEdt = editTextPassword.text.toString()
            val confirmPasswordEdt = editTextConfirmPassword.text.toString()

            if(passwordEdt.isEmpty() || confirmPasswordEdt.isEmpty()) {
                editTextPassword.error = "Please enter password and confirm password"
                return@setOnClickListener
            }else if (passwordEdt.length < 8 || confirmPasswordEdt.length < 8){
                editTextPassword.error = "Password must be at least 8 characters"
                return@setOnClickListener
            }else if (passwordEdt != confirmPasswordEdt){
                editTextConfirmPassword.error = "Password not match"
                return@setOnClickListener
            }

            if (email != null){
                val url = getString(R.string.url_server)+ getString(R.string.port_3000) + getString(R.string.api_reset_password)
                val okHttpClient = OkHttpClient()
                val formBody: RequestBody = FormBody.Builder()
                    .add("Users_Email", email)
                    .add("Users_Password", passwordEdt)
                    .build()
                val request: Request = Request.Builder()
                    .url(url)
                    .post(formBody)
                    .build()

                val response = okHttpClient.newCall(request).execute()
                if(response.isSuccessful) {
                    val  obj = JSONObject(response.body!!.string())
                    val status = obj["status"].toString()
                    if (status == "true") {
                        intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else{
                        val message = obj["message"].toString()
                        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                        return@setOnClickListener
                    }
                }else{
                    Toast.makeText(applicationContext, "เกิดข้อผิดพลาดในการเชื่อมต่อ", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
            }
        }

        bt_Back.setOnClickListener {
            val intent = Intent(this, SendPasswordActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}