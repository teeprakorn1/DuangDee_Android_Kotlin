package th.ac.rmutto.duangdee

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import th.ac.rmutto.duangdee.databinding.ActivityMainBinding
import th.ac.rmutto.duangdee.shared_preferences_encrypt.Encryption
import th.ac.rmutto.duangdee.shared_preferences_encrypt.Encryption.Companion.decrypt
import th.ac.rmutto.duangdee.shared_preferences_encrypt.Encryption.Companion.encrypt
import th.ac.rmutto.duangdee.shared_preferences_encrypt.Encryption.Companion.generateKey
import th.ac.rmutto.duangdee.ui.horoscope.HoroscopeFragment
import th.ac.rmutto.duangdee.ui.login.LoginActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var regisTypeId: String? = null
    private var usersID: String? = null
    private var usersEmail: String? = null
    private var usersUsernameOrUid: String? = null
    private lateinit var encryption: Encryption
    private lateinit var navController: NavController

    private fun verifyToken(token: String) {
        // Verify Token From Server
        val url = getString(R.string.url_server) + getString(R.string.port_3000) + getString(R.string.api_verify_token)
        val okHttpClient = OkHttpClient()
        val formBody: RequestBody = FormBody.Builder().build()
        val request: Request = Request.Builder()
            .url(url)
            .post(formBody)
            .addHeader("x-access-token", token)
            .build()

        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val obj = JSONObject(response.body!!.string())
                val status = obj["status"].toString()
                if (status == "true") {
                    regisTypeId = obj["RegisType_ID"].toString()
                    usersID = obj["Users_ID"].toString()
                    usersEmail = obj["Users_Email"].toString()

                    if (regisTypeId == "1") {
                        usersUsernameOrUid = obj["Users_Username"].toString()
                    } else if (regisTypeId == "2") {
                        usersUsernameOrUid = obj["Users_Google_Uid"].toString()
                    }

                    // Save data in SharedPreferences
                    val sharedPref = getSharedPreferences("DuangDee_Pref", Context.MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putString("regisTypeId", encryptionValue(regisTypeId!!))
                        putString("usersID", encryptionValue(usersID!!))
                        putString("usersEmail", encryptionValue(usersEmail!!))
                        putString("usersUsernameOrUid", encryptionValue(usersUsernameOrUid!!))
                        apply() // or commit()
                    }

                    // Call setupNavigation only when token verification is successful
                    setupNavigation()
                } else {
                    redirectToLogin()
                }
            } else {
                redirectToLogin()
            }
        } catch (e: Exception) {
            Log.e("VerifyTokenError", "Error verifying token", e)
            redirectToLogin()
        }
    }

    private fun encryptionValue(value: String): String {
        val key = encryption.getKeyFromPreferences() ?: generateKey().also { encryption.saveKeyToPreferences(it) }
        return encrypt(value, key)
    }

    private fun redirectToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish() // Call finish() to close the current activity
    }

    // Navigation setup function, called only after token verification
    private fun setupNavigation() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.app_name)
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))

        val navView: BottomNavigationView = binding.bottomNavigation
        navController = findNavController(R.id.nav_host_fragment_activity_main)
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        fab.setOnClickListener {
            navController.navigate(R.id.navigation_horoscope)
        }

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    navController.navigate(R.id.navigation_home)
                    true
                }
                R.id.navigation_dashboard -> {
                    navController.navigate(R.id.navigation_dashboard)
                    true
                }
                R.id.navigation_history -> {
                    navController.navigate(R.id.navigation_history)
                    true
                }
                R.id.navigation_profile -> {
                    navController.navigate(R.id.navigation_profile)
                    true
                }
                else -> false
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //For an synchronous task
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Start Decryption
        val sharedPref = getSharedPreferences("DuangDee_Pref", Context.MODE_PRIVATE)
        encryption = Encryption(this)
        val token = sharedPref.getString("token", null)

        val decode = token?.let { decrypt(it, encryption.getKeyFromPreferences()) }
        if (decode != null) {
            verifyToken(decode)
        } else {
            redirectToLogin()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        if (navController.currentDestination?.id != R.id.navigation_home) {
            navController.navigate(R.id.navigation_home)
        } else {
            super.onBackPressed()
        }
    }
}
