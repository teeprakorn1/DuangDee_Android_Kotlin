package th.ac.rmutto.duangdee.otp

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import th.ac.rmutto.duangdee.MainActivity
import th.ac.rmutto.duangdee.R

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

        val confirmButton = findViewById<Button>(R.id.confirmotp_btn);

        confirmButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}