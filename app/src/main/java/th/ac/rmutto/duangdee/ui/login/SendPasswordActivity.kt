package th.ac.rmutto.duangdee.ui.login

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
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
        val bt_Back = findViewById<ImageView>(R.id.bt_back);

        sendEmailButton.setOnClickListener {
            findViewById<LottieAnimationView>(R.id.lottie_loading).visibility = View.VISIBLE
            findViewById<LottieAnimationView>(R.id.lottie_loading).playAnimation()
            Handler(Looper.getMainLooper()).postDelayed({
                val emailEdt = emailEditText.text.toString()

                //Check email
                if (emailEdt.isEmpty() || emailEdt.isBlank()) {
                    emailEditText.error = "Please enter a your email."
                    findViewById<LottieAnimationView>(R.id.lottie_loading).visibility = View.GONE
                    return@postDelayed
                }else if(!emailEdt.contains("@")){
                    emailEditText.error = "Please enter a valid email."
                    findViewById<LottieAnimationView>(R.id.lottie_loading).visibility = View.GONE
                    return@postDelayed
                }else if(!emailEdt.contains(".")){
                    emailEditText.error = "Please enter a valid email."
                    findViewById<LottieAnimationView>(R.id.lottie_loading).visibility = View.GONE
                    return@postDelayed
                }else if(emailEdt.length < 10){
                    emailEditText.error = "Please enter a valid email."
                    findViewById<LottieAnimationView>(R.id.lottie_loading).visibility = View.GONE
                    return@postDelayed
                }else if(emailEdt.length > 30){
                    emailEditText.error = "Please enter a valid email."
                    findViewById<LottieAnimationView>(R.id.lottie_loading).visibility = View.GONE
                    return@postDelayed
                }

                intent = Intent(this, ConfirmOtpActivity::class.java)
                intent.putExtra("email",emailEdt)
                intent.putExtra("page_type","ResetPassword")
                startActivity(intent)
                findViewById<LottieAnimationView>(R.id.lottie_loading).visibility = View.GONE
            }, 500)
        }

        bt_Back.setOnClickListener {
            findViewById<LottieAnimationView>(R.id.lottie_loading).visibility = View.VISIBLE
            findViewById<LottieAnimationView>(R.id.lottie_loading).playAnimation()
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }, 500)
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