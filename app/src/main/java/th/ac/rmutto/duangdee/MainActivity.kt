package th.ac.rmutto.duangdee

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import th.ac.rmutto.duangdee.shared_preferences_encrypt.Encryption.Companion.decrypt
import okhttp3.*
import org.json.JSONObject
import th.ac.rmutto.duangdee.shared_preferences_encrypt.Encryption
import th.ac.rmutto.duangdee.ui.login.LoginActivity

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private var regisTypeId: String? = null
    private var usersID: String? = null
    private var usersUsername: String? = null
    private var usersEmail: String? = null
    private var usersGoogleUid: String? = null
    private lateinit var encryption: Encryption

    private fun verifyToken(token: String) {
        //Verify Token From Server
        val url = getString(R.string.url_server) + getString(R.string.api_verify_token)
        val okHttpClient = OkHttpClient()
        val formBody: RequestBody = FormBody.Builder()
            .build()
        val request: Request = Request.Builder()
            .url(url)
            .post(formBody)
            .addHeader("x-access-token", token) // Add your header here
            .build()
        val response = okHttpClient.newCall(request).execute()
        if (response.isSuccessful) {
            val obj = JSONObject(response.body!!.string())
            val status = obj["status"].toString()
            if (status == "true") {
                regisTypeId = obj["RegisType_ID"].toString()

                if (regisTypeId == "1"){
                    usersID = obj["Users_ID"].toString()
                    usersUsername = obj["Users_Username"].toString()
                    usersEmail = obj["Users_Email"].toString()

                }else if (regisTypeId == "2"){
                    usersID = obj["Users_ID"].toString()
                    usersGoogleUid = obj["Users_Google_Uid"].toString()
                    usersEmail = obj["Users_Email"].toString()
                }
            }else{
                intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Start Decryption
        val sharedPref = getSharedPreferences("DuangDee_Pref", Context.MODE_PRIVATE)
        encryption = Encryption(this)
        val token = sharedPref.getString("token", null)
        val decode = decrypt(token.toString(), encryption.getKeyFromPreferences())
        if (token != null) {
            verifyToken(decode)
        }else{
            intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        if (usersID == null){
            intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Main_Activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Configure sign-in to request the user's ID, email address, and basic profile.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = GoogleSignIn.getClient(this, gso)


        val signOutButton = findViewById<Button>(R.id.Signout_Btn)
        val emailTextView = findViewById<TextView>(R.id.TextviewEmail)

        emailTextView.text = usersEmail+"\nID:"+usersID

        signOutButton.setOnClickListener {
            signOut()
        }
    }

    private fun signOut() {
        googleSignInClient.signOut().addOnCompleteListener {
            // Handle sign-out success or failure
            if (it.isSuccessful) {
                auth.signOut()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // Optionally, handle sign-out failure
                Toast.makeText(this, "Failed to sign out", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
