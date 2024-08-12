package th.ac.rmutto.duangdee.ui.login


import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import th.ac.rmutto.duangdee.MainActivity
import th.ac.rmutto.duangdee.R
import th.ac.rmutto.duangdee.ui.register.RegisMailActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Login_Activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        supportActionBar?.hide()
        //For an synchronous task
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        auth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val editTextUsername = findViewById<EditText>(R.id.editTextUsername)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val buttonLogin = findViewById<Button>(R.id.ButtonLogin)
        val buttonRegister = findViewById<TextView>(R.id.TextRegister_Btn)
        val buttonLoginGoogle = findViewById<Button>(R.id.ButtonLoginGoogle)
        val forgotPassword = findViewById<TextView>(R.id.TextForgotPassword_Btn)

        buttonLoginGoogle.setOnClickListener {
            signIn()
        }


        buttonLogin.setOnClickListener {
            val username = editTextUsername.text.toString()
            val password = editTextPassword.text.toString()

            if (username.isEmpty()) {
                editTextUsername.error = "Username is required"
                return@setOnClickListener
            }
            else if (password.isEmpty()) {
                editTextPassword.error = "Password is required"
                return@setOnClickListener
            }

            val url = getString(R.string.url_server) + getString(R.string.api_login)
            val okHttpClient = OkHttpClient()
            val formBody: RequestBody = FormBody.Builder()
                .add("Users_Username",username)
                .add("Users_Password",password)
                .build()
            val request: Request = Request.Builder()
                .url(url)
                .post(formBody)
                .build()

            val response = okHttpClient.newCall(request).execute()

            if(response.isSuccessful){
                val obj = JSONObject(response.body!!.string())
                val status = obj["status"].toString()

                if (status == "true") {
                    //redirect to main page
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val message = obj["message"].toString()
                    Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                }

            }else{
                Toast.makeText(applicationContext, "ไม่สามารถเชื่อต่อกับเซิร์ฟเวอร์ได้", Toast.LENGTH_LONG).show()
            }
        }

        buttonRegister.setOnClickListener {
            val intent = Intent(this, RegisMailActivity::class.java)
            startActivity(intent)
        }

        forgotPassword.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        }
    }

    private fun signIn(){
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)

    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result -> if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if(task.isSuccessful){
            val account : GoogleSignInAccount? = task.result
            if (account != null){
                updateUI(account)
            }
        }else{
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken,null)
        auth.signInWithCredential(credential).addOnCompleteListener{
            if(it.isSuccessful){
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("email", account.email)
                intent.putExtra("name", account.displayName)
                startActivity(intent)
            }else{
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }
}