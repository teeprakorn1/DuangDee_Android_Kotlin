package th.ac.rmutto.duangdee

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import th.ac.rmutto.duangdee.ui.login.LoginActivity

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Main_Activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val email = intent.getStringExtra("email")
        val displayName = intent.getStringExtra("name")

        val signOutButton = findViewById<Button>(R.id.Signout_Btn)
        val emailTextView = findViewById<TextView>(R.id.TextviewEmail)
        auth = FirebaseAuth.getInstance()

        emailTextView.text = email + "\n" + displayName

        signOutButton.setOnClickListener {
            signOut()
        }
    }

    private fun signOut() {
        googleSignInClient.signOut().addOnCompleteListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}