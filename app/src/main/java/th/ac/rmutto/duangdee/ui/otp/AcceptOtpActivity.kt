package th.ac.rmutto.duangdee.ui.otp

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
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
import th.ac.rmutto.duangdee.R
import th.ac.rmutto.duangdee.ui.login.LoginActivity
import th.ac.rmutto.duangdee.ui.login.ResetPasswordActivity

class AcceptOtpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()
        setContentView(R.layout.activity_accept_otp)
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

        val resendButton = findViewById<TextView>(R.id.resend_otp_textview);
        val confirmButton = findViewById<Button>(R.id.confirmotp_btn);
        val editTextOTP1 = findViewById<EditText>(R.id.editTextOTP1);
        val editTextOTP2 = findViewById<EditText>(R.id.editTextOTP2);
        val editTextOTP3 = findViewById<EditText>(R.id.editTextOTP3);
        val editTextOTP4 = findViewById<EditText>(R.id.editTextOTP4);
        val editTextOTP5 = findViewById<EditText>(R.id.editTextOTP5);
        val editTextOTP6 = findViewById<EditText>(R.id.editTextOTP6);

        editTextOTP1.addTextChangedListener(CustomTextWatcher(editTextOTP1, editTextOTP2))
        editTextOTP2.addTextChangedListener(CustomTextWatcher(editTextOTP2, editTextOTP3))
        editTextOTP3.addTextChangedListener(CustomTextWatcher(editTextOTP3, editTextOTP4))
        editTextOTP4.addTextChangedListener(CustomTextWatcher(editTextOTP4, editTextOTP5))
        editTextOTP5.addTextChangedListener(CustomTextWatcher(editTextOTP5, editTextOTP6))
        editTextOTP6.addTextChangedListener(CustomTextWatcher(editTextOTP6, null))

        confirmButton.setOnClickListener {
            val otp = editTextOTP1.text.toString() + editTextOTP2.text.toString() +
                    editTextOTP3.text.toString() + editTextOTP4.text.toString() +
                    editTextOTP5.text.toString() + editTextOTP6.text.toString()

            if (otp.length != 6){
                Toast.makeText(applicationContext, "กรุณากรอกรหัส OTP ให้ครบถ้วน", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (pageType == "Register"){
                username = intent.getStringExtra("username")
                password = intent.getStringExtra("password")

                if (email != null){
                    var url = getString(R.string.url_server) + getString(R.string.api_verify_otp)
                    val okHttpClient = OkHttpClient()
                    var formBody: RequestBody = FormBody.Builder()
                        .add("Users_Email", email)
                        .add("OTP", otp)
                        .add("Value", "0")
                        .build()
                    var request: Request = Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build()

                    var response = okHttpClient.newCall(request).execute()
                    if(response.isSuccessful) {
                        var  obj = JSONObject(response.body!!.string())
                        var status = obj["status"].toString()
                        if (status == "true") {
                            url = getString(R.string.url_server) + getString(R.string.api_register)
                            formBody = FormBody.Builder()
                                .add("Users_Email", email)
                                .add("Users_Username", username.toString())
                                .add("Users_Password", password.toString())
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
                                    val message = obj["message"].toString()
                                    Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                                    val intent = Intent(this, LoginActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }else if (status == "false") {
                                    val message = obj["message"].toString()
                                    Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                                    clearEdtOTP();
                                    return@setOnClickListener
                                }
                            }else{
                                Toast.makeText(applicationContext, "เกิดข้อผิดพลาดในการเชื่อมต่อ", Toast.LENGTH_LONG).show()
                                return@setOnClickListener
                            }
                        } else if (status == "false") {
                            val message = obj["message"].toString()
                            Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                            clearEdtOTP();
                            return@setOnClickListener
                        }
                    }else{
                        Toast.makeText(applicationContext, "เกิดข้อผิดพลาดในการเชื่อมต่อ", Toast.LENGTH_LONG).show()
                        return@setOnClickListener
                    }
                }
            }else if (pageType == "ResetPassword"){
                if (email != null){
                    val url = getString(R.string.url_server) + getString(R.string.api_verify_otp)
                    val okHttpClient = OkHttpClient()
                    val formBody: RequestBody = FormBody.Builder()
                        .add("Users_Email", email)
                        .add("OTP", otp)
                        .add("Value", "1")
                        .build()
                    var request: Request = Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build()

                    val response = okHttpClient.newCall(request).execute()
                    if(response.isSuccessful) {
                        val  obj = JSONObject(response.body!!.string())
                        val status = obj["status"].toString()
                        if (status == "true") {
                            intent = Intent(this, ResetPasswordActivity::class.java)
                            intent.putExtra("email", email)
                            startActivity(intent)
                            finish()
                        }else{
                            val message = obj["message"].toString()
                            Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                            clearEdtOTP();
                            return@setOnClickListener
                        }
                    }else{
                        Toast.makeText(applicationContext, "เกิดข้อผิดพลาดในการเชื่อมต่อ", Toast.LENGTH_LONG).show()
                        return@setOnClickListener
                    }
                }
            }else{
                Toast.makeText(applicationContext, "เกิดข้อผิดพลาด", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
        }

        resendButton.setOnClickListener {
            if (pageType == "Register"){
                if (email != null){
                    val url = getString(R.string.url_server) + getString(R.string.api_request_register)
                    val okHttpClient = OkHttpClient()
                    val formBody: RequestBody = FormBody.Builder()
                        .add("Users_Email", email)
                        .add("Value", "1")
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
                            val message = obj["message"].toString()
                            Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                            clearEdtOTP()
                            return@setOnClickListener
                        }else if (status == "false") {
                            val message = obj["message"].toString()
                            Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                            clearEdtOTP()
                            return@setOnClickListener
                        }
                    }else{
                        Toast.makeText(applicationContext, "เกิดข้อผิดพลาดในการเชื่อมต่อ", Toast.LENGTH_LONG).show()
                        return@setOnClickListener
                    }
                }
            }else if (pageType == "ResetPassword"){
                if (email != null){
                    val url = getString(R.string.url_server) + getString(R.string.api_request_password)
                    val okHttpClient = OkHttpClient()
                    val formBody: RequestBody = FormBody.Builder()
                        .add("Users_Email", email)
                        .add("Value", "1")
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
                            val message = obj["message"].toString()
                            Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                            clearEdtOTP()
                            return@setOnClickListener
                        }else if (status == "false") {
                            val message = obj["message"].toString()
                            Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                            clearEdtOTP()
                            return@setOnClickListener
                        }
                    }else{
                        Toast.makeText(applicationContext, "เกิดข้อผิดพลาดในการเชื่อมต่อ", Toast.LENGTH_LONG).show()
                        return@setOnClickListener
                    }
                }
            }else{
                Toast.makeText(applicationContext, "เกิดข้อผิดพลาด", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
        }
    }

    private class CustomTextWatcher(
        private val currentEditText: EditText,
        private val nextEditText: EditText?
    ) :
        TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            // ไม่ต้องทำอะไร
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            // ถ้ากรอกครบ 1 ตัวอักษร ให้เลื่อนโฟกัสไปยัง EditText ถัดไป
            if (currentEditText.text.toString().length == 1 && nextEditText != null) {
                nextEditText.requestFocus()
            }
        }

        override fun afterTextChanged(s: Editable) {
            // ไม่ต้องทำอะไร
        }
    }

   private fun clearEdtOTP(){
       findViewById<EditText>(R.id.editTextOTP1).text.clear()
       findViewById<EditText>(R.id.editTextOTP2).text.clear()
       findViewById<EditText>(R.id.editTextOTP3).text.clear()
       findViewById<EditText>(R.id.editTextOTP4).text.clear()
       findViewById<EditText>(R.id.editTextOTP5).text.clear()
       findViewById<EditText>(R.id.editTextOTP6).text.clear()
    }
}