package th.ac.rmutto.duangdee.otp

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import th.ac.rmutto.duangdee.R
import th.ac.rmutto.duangdee.ui.register.RegisMailActivity
import th.ac.rmutto.duangdee.ui.register.RegisUserActivity

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

        val confirmButton = findViewById<Button>(R.id.Confirm_Btn);
        val backButton = findViewById<Button>(R.id.Back_Btn);

        confirmButton.setOnClickListener{
            intent = Intent(this, AcceptOtpActivity::class.java)
            startActivity(intent)
        }

        backButton.setOnClickListener{
            intent = Intent(this, RegisMailActivity::class.java)
            startActivity(intent)
        }
    }
}