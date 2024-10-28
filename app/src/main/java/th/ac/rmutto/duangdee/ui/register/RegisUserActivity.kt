package th.ac.rmutto.duangdee.ui.register

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.airbnb.lottie.LottieAnimationView
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import th.ac.rmutto.duangdee.R
import th.ac.rmutto.duangdee.ui.login.LoginActivity
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
            findViewById<LottieAnimationView>(R.id.lottie_loading).visibility = View.VISIBLE
            findViewById<LottieAnimationView>(R.id.lottie_loading).playAnimation()
            Handler(Looper.getMainLooper()).postDelayed({
                val usernameEdt = editTextUsername.text.toString()
                val passwordEdt = editTextPassword.text.toString()
                val confirmPasswordEdt = editTextConfirmPassword.text.toString()

                if(usernameEdt.isEmpty()) {
                    editTextUsername.error = "Please enter username"
                    findViewById<LottieAnimationView>(R.id.lottie_loading).visibility = View.GONE
                    return@postDelayed
                }else if (usernameEdt.length < 8){
                    editTextUsername.error = "Username must be at least 8 characters"
                    findViewById<LottieAnimationView>(R.id.lottie_loading).visibility = View.GONE
                    return@postDelayed
                }else if (passwordEdt.isEmpty()) {
                    editTextPassword.error = "Please enter password"
                    findViewById<LottieAnimationView>(R.id.lottie_loading).visibility = View.GONE
                    return@postDelayed
                }else if (passwordEdt.length < 8){
                    editTextPassword.error = "Password must be at least 8 characters"
                    findViewById<LottieAnimationView>(R.id.lottie_loading).visibility = View.GONE
                    return@postDelayed
                }else if (confirmPasswordEdt.isEmpty()) {
                    editTextConfirmPassword.error = "Please enter confirm password"
                    findViewById<LottieAnimationView>(R.id.lottie_loading).visibility = View.GONE
                    return@postDelayed
                }else if (passwordEdt != confirmPasswordEdt){
                    editTextConfirmPassword.error = "Password not match"
                    findViewById<LottieAnimationView>(R.id.lottie_loading).visibility = View.GONE
                    return@postDelayed
                }

                val url = getString(R.string.url_server) + getString(R.string.port_3000) + getString(R.string.api_check_username)
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
                        findViewById<LottieAnimationView>(R.id.lottie_loading).visibility = View.GONE
                        return@postDelayed
                    }
                }else{
                    findViewById<LottieAnimationView>(R.id.lottie_loading).visibility = View.GONE
                    return@postDelayed
                }
            },500)
        }

        bt_Back.setOnClickListener {
            findViewById<LottieAnimationView>(R.id.lottie_loading).visibility = View.VISIBLE
            findViewById<LottieAnimationView>(R.id.lottie_loading).playAnimation()
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, RegisMailActivity::class.java)
                startActivity(intent)
            },500)
        }
    }
    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
}