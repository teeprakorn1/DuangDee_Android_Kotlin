package th.ac.rmutto.duangdee.ui.register

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import th.ac.rmutto.duangdee.R

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

        nextButton.setOnClickListener{
            val intent = Intent(this, RegisUserActivity::class.java)
            startActivity(intent)
        }
    }
}