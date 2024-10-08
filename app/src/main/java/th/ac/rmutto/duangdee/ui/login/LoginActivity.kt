package th.ac.rmutto.duangdee.ui.login


import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
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
import th.ac.rmutto.duangdee.shared_preferences_encrypt.Encryption
import th.ac.rmutto.duangdee.shared_preferences_encrypt.Encryption.Companion.encrypt
import th.ac.rmutto.duangdee.shared_preferences_encrypt.Encryption.Companion.generateKey
import th.ac.rmutto.duangdee.ui.register.RegisMailActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var encryption: Encryption

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
//            loadingDialog()
            signIn()
        }

        buttonLogin.setOnClickListener {
//            loadingDialog()
            encryption = Encryption(this)
            val sharedPref = getSharedPreferences("DuangDee_Pref", Context.MODE_PRIVATE)
            val username = editTextUsername.text.toString()
            val password = editTextPassword.text.toString()

            if (username.isEmpty()) {
                editTextUsername.error = "Username is required"
                return@setOnClickListener
            } else if (password.isEmpty()) {
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
                    val token = obj["token"].toString()
                    val key = generateKey()
                    encryption.saveKeyToPreferences(key)
                    val encryptToken = encrypt(token, key)
                    with(sharedPref.edit()) {
                        putString("token", encryptToken)
                        apply()
                    }
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val message = obj["message"].toString()
                    Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                }
            }else{
                val obj = JSONObject(response.body!!.string())
                if (obj.has("login_status")){
                    if (obj["login_status"].toString() == "false"){
                        val message = obj["message"].toString()
                        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                    }
                }else{
                    Toast.makeText(applicationContext, "ไม่สามารถเชื่อต่อกับเซิร์ฟเวอร์ได้", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
            }
        }

        buttonRegister.setOnClickListener {
            val intent = Intent(this, RegisMailActivity::class.java)
            startActivity(intent)
        }

        forgotPassword.setOnClickListener {
            val intent = Intent(this, SendPasswordActivity::class.java)
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
    }else{
        Toast.makeText(this, "ไม่สามารถเชื่อต่อกับเซิร์ฟเวอร์ได้", Toast.LENGTH_LONG).show()
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
        encryption = Encryption(this)
        val sharedPref = getSharedPreferences("DuangDee_Pref", Context.MODE_PRIVATE)
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Sign in successful
                val firebaseUser = auth.currentUser
                if (firebaseUser != null) {
                    val uid = firebaseUser.uid
                    val displayName = firebaseUser.displayName
                    val email = firebaseUser.email

                    var url = getString(R.string.url_server) + getString(R.string.api_check_uid)
                    val okHttpClient = OkHttpClient()
                    var formBody: RequestBody = FormBody.Builder()
                        .add("Users_Google_Uid", uid)
                        .build()
                    var request: Request = Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build()

                    var response = okHttpClient.newCall(request).execute()
                    if(response.isSuccessful) {
                        var obj = JSONObject(response.body!!.string())
                        var status = obj["status"].toString()
                        if (status == "true") {
                            // google sign in clear all sessions
                            val googleSignInClients = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN)
                            googleSignInClients.signOut()

                            url = getString(R.string.url_server) + getString(R.string.api_register_uid)
                            formBody = FormBody.Builder()
                                .add("Users_Google_Uid", uid)
                                .add("Users_Email", email.toString())
                                .add("Users_DisplayName", displayName.toString())
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
                                    url = getString(R.string.url_server) + getString(R.string.api_login_uid)
                                    formBody = FormBody.Builder()
                                        .add("Users_Google_Uid", uid)
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
                                            val token = obj["token"].toString()
                                            val key = generateKey()
                                            encryption.saveKeyToPreferences(key)
                                            val encryptToken = encrypt(token, key)
                                            with(sharedPref.edit()) {
                                                putString("token", encryptToken)
                                                apply()
                                            }
                                            val intent = Intent(this, MainActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                        }else if (status == "false"){
                                            val message = obj["message"].toString()
                                            Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                                            return@addOnCompleteListener
                                        }
                                    }else{
                                        Toast.makeText(applicationContext, "ไม่สามารถเชื่อต่อกับเซิร์ฟเวอร์ได้", Toast.LENGTH_LONG).show()
                                        return@addOnCompleteListener
                                    }

                                }else if (status == "false"){
                                    val message = obj["message"].toString()
                                    Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                                    return@addOnCompleteListener
                                }
                            }else{
                                Toast.makeText(applicationContext, "ไม่สามารถเชื่อต่อกับเซิร์ฟเวอร์ได้", Toast.LENGTH_LONG).show()
                                return@addOnCompleteListener
                            }
                        }else if(status == "false"){
                            url = getString(R.string.url_server) + getString(R.string.api_login_uid)
                            formBody = FormBody.Builder()
                                .add("Users_Google_Uid", uid)
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
                                    val token = obj["token"].toString()
                                    val key = generateKey()
                                    encryption.saveKeyToPreferences(key)
                                    val encryptToken = encrypt(token, key)
                                    with(sharedPref.edit()) {
                                        putString("token", encryptToken)
                                        apply()
                                    }
                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()

                                }else if (status == "false"){
                                    val message = obj["message"].toString()
                                    Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                                    return@addOnCompleteListener
                                }
                            }else{
                                Toast.makeText(applicationContext, "ไม่สามารถเชื่อต่อกับเซิร์ฟเวอร์ได้", Toast.LENGTH_LONG).show()
                                return@addOnCompleteListener
                            }
                        }
                    }else{
                        Toast.makeText(applicationContext, "ไม่สามารถเชื่อต่อกับเซิร์ฟเวอร์ได้", Toast.LENGTH_LONG).show()
                        return@addOnCompleteListener
                    }
                }
            } else {
                // Sign in failed
                Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadingDialog(){
        val loadingDialog = Dialog(this)
        loadingDialog.setContentView(R.layout.diaglog_loading)
        loadingDialog.window!!.setLayout(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        loadingDialog.show()
    }
}