package th.ac.rmutto.duangdee.ui.profile

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import th.ac.rmutto.duangdee.R
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
//import com.bumptech.glide.Glide
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import th.ac.rmutto.duangdee.MainActivity
import th.ac.rmutto.duangdee.shared_preferences_encrypt.Encryption
import th.ac.rmutto.duangdee.shared_preferences_encrypt.Encryption.Companion.decrypt
import th.ac.rmutto.duangdee.ui.login.LoginActivity
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class EditProfileActivity : AppCompatActivity() {

    private var genderData: String? = null
    private var formattedDate: String? = null
    private lateinit var encryption: Encryption
    private var tokens: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()
        setContentView(R.layout.activity_edit_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.EditProfile_Activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Method to hide the keyboard when clicking outside the EditText
        findViewById<ConstraintLayout>(R.id.EditProfile_Activity).setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
            currentFocus?.clearFocus()
        }

        // Start Decryption SharedPreferences
        val sharedPref = getSharedPreferences("DuangDee_Pref", Context.MODE_PRIVATE)
        val usersID = sharedPref.getString("usersID", null)
        val token = sharedPref.getString("token", null)

        encryption = Encryption(this)
        val decode = decrypt(usersID.toString(), encryption.getKeyFromPreferences())
        tokens = decrypt(token.toString(), encryption.getKeyFromPreferences())

        val spinner = findViewById<Spinner>(R.id.spinner)
        val optionGender = arrayOf("โปรดเลือก", "ผู้ชาย", "ผู้หญิง", "อื่นๆ")
        val adapter = ArrayAdapter(this, R.layout.custom_spinner, optionGender)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        getProfile(decode)

        val imageButton = findViewById<ImageButton>(R.id.imageButton)
        val saveBtn = findViewById<Button>(R.id.Save_Btn)
        val backBtn = findViewById<Button>(R.id.Back_Btn)

        saveBtn.setOnClickListener {
            updateProfile(decode)
        }

        backBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        imageButton.setOnClickListener {
            showDatePickerDialog()
        }
    }
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val yearDate = calendar.get(Calendar.YEAR)
        var monthDate = calendar.get(Calendar.MONTH)
        val dayDate = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            ContextThemeWrapper(this, R.style.WhiteDatePickerDialog), // บังคับใช้ธีม
            { _, year, month, day ->
                monthDate = month + 1

                findViewById<EditText>(R.id.editTextRegisDay).setText(day.toString())
                findViewById<EditText>(R.id.editTextRegisMonth).setText(monthDate.toString())
                findViewById<EditText>(R.id.editTextRegisYear).setText(year.toString())

                // เรียกฟังก์ชัน dateFormat ที่คุณมีเพื่อจัดรูปแบบวันที่
                val selectedDate = Calendar.getInstance().apply {
                    set(year, month, day)
                }
                formattedDate = dateFormat(selectedDate)
            },
            yearDate, monthDate, dayDate
        )

        datePickerDialog.show()
    }

    private fun getProfile(userID: String){
        val editTextRegisDisplayName = findViewById<EditText>(R.id.editTextRegisName)
        val editTextFirstName = findViewById<EditText>(R.id.editText_FirstName)
        val editTextRegisLastName = findViewById<EditText>(R.id.editTextRegisSurname)
        val editTextPhone = findViewById<EditText>(R.id.editText_Phone)
        val spinnerGender = findViewById<Spinner>(R.id.spinner)

        val editTextRegisDay = findViewById<EditText>(R.id.editTextRegisDay)
        val editTextRegisMonth = findViewById<EditText>(R.id.editTextRegisMonth)
        val editTextRegisYear = findViewById<EditText>(R.id.editTextRegisYear)

        val url = getString(R.string.url_server) + getString(R.string.port_3000) + getString(R.string.api_get_profile) + userID
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder()
            .url(url)
            .get()
            .addHeader("x-access-token", tokens.toString())
            .build()
        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val obj = JSONObject(response.body!!.string())
                val status = obj["status"].toString()
                if (status == "true") {
                    editTextRegisDisplayName.setText(obj["Users_DisplayName"].toString())
                    editTextFirstName.setText(obj.optString("Users_FirstName", "N/A"))
                    editTextRegisLastName.setText(obj.optString("Users_LastName", "N/A"))
                    editTextPhone.setText(obj.optString("Users_Phone", "N/A"))

                    formattedDate = dateTransfer(obj.optString("Users_BirthDate", "N/A"))
                    genderData = obj.optString("UsersGender_ID", "N/A")

                    if (genderData == "1"){
                        spinnerGender.setSelection(1)
                    }else if (genderData == "2"){
                        spinnerGender.setSelection(2)
                    }else if (genderData == "3"){
                        spinnerGender.setSelection(3)
                    }

                    if (formattedDate != "null") {
                        val (year, month, day) = formattedDate!!.split("-").map { it.toInt() }
                        editTextRegisDay.setText(day.toString())
                        editTextRegisMonth.setText((month).toString())
                        editTextRegisYear.setText(year.toString())
                    }

                    if (editTextFirstName.text.toString() == "N/A" || editTextFirstName.text.toString() == "null") {
                        editTextFirstName.setText("")
                    }
                    if (editTextRegisLastName.text.toString() == "N/A" || editTextRegisLastName.text.toString() == "null") {
                        editTextRegisLastName.setText("")
                    }
                    if (editTextPhone.text.toString() == "N/A" || editTextPhone.text.toString() == "null") {
                        editTextPhone.setText("")
                    }


                } else {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
            } else {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        } catch (e: Exception) {
            Log.e("VerifyTokenError", "Error verifying token", e)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updateProfile(userID: String){
        val editTextRegisDisplayName = findViewById<EditText>(R.id.editTextRegisName)
        val editTextFirstName = findViewById<EditText>(R.id.editText_FirstName)
        val editTextRegisLastName = findViewById<EditText>(R.id.editTextRegisSurname)
        val editTextPhone = findViewById<EditText>(R.id.editText_Phone)
        val spinnerGender = findViewById<Spinner>(R.id.spinner)

        if (formattedDate == "null") {
            Toast.makeText(this, "โปรดเลือกวันเกิด", Toast.LENGTH_SHORT).show()
            return
        }else if(checkTimeStamp(formattedDate.toString())){
            Toast.makeText(this, "โปรดเลือกวันเกิดตามความเป็นจริง", Toast.LENGTH_SHORT).show()
            return
        }

        if (spinnerGender.selectedItemPosition == 0){
            Toast.makeText(this, "โปรดเลือกเพศ", Toast.LENGTH_SHORT).show()
            return
        }

        if (editTextRegisDisplayName.text.isEmpty()){
            editTextRegisDisplayName.error = "กรุณากรอกชื่อ"
            return
        } else if (editTextRegisDisplayName.text.length < 4){
            editTextRegisDisplayName.error = "กรุณากรอกชื่อมากกว่า 4 ตัวอักษร"
            return
        } else if (editTextRegisDisplayName.text.length > 32){
            editTextRegisDisplayName.error = "กรุณากรอกชื่อไม่เกิน 32 ตัวอักษร"
            return
        }

        if (editTextFirstName.text.isEmpty()){
            editTextFirstName.error = "กรุณากรอกชื่อ"
            return
        }else if (editTextFirstName.text.length < 2){
            editTextFirstName.error = "กรุณากรอกชื่อมากกว่า 2 ตัวอักษร"
            return
        }else if (editTextFirstName.text.length > 32){
            editTextFirstName.error = "กรุณากรอกชื่อไม่เกิน 32 ตัวอักษร"
            return
        }

        if (editTextRegisLastName.text.isEmpty()){
            editTextRegisLastName.error = "กรุณากรอกนามสกุล"
            return
        }else if (editTextRegisLastName.text.length < 2){
            editTextRegisLastName.error = "กรุณากรอกนามสกุลมากกว่า 2 ตัวอักษร"
            return
        }else if (editTextRegisLastName.text.length > 32){
            editTextRegisLastName.error = "กรุณากรอกนามสกุลไม่เกิน 32 ตัวอักษร"
            return
        }

        if (editTextPhone.text.isEmpty()){
            editTextPhone.error = "กรุณากรอกเบอร์โทร"
            return
        }else if (editTextPhone.text.length != 10){
            editTextPhone.error = "กรุณากรอกเบอร์โทร 10 หลัก"
            return
        }

        val url = getString(R.string.url_server) + getString(R.string.port_3000) + getString(R.string.api_update_profile) + userID
        val okHttpClient = OkHttpClient()
        val formBody: RequestBody = FormBody.Builder()
            .add("Users_DisplayName",editTextRegisDisplayName.text.toString())
            .add("Users_FirstName",editTextFirstName.text.toString())
            .add("Users_LastName",editTextRegisLastName.text.toString())
            .add("Users_Phone",editTextPhone.text.toString())
            .add("Users_BirthDate",formattedDate.toString())
            .add("UsersGender_ID",spinnerGender.selectedItemPosition.toString())
            .build()
        val request: Request = Request.Builder()
            .url(url)
            .put(formBody)
            .addHeader("x-access-token", tokens.toString())
            .build()
        val response = okHttpClient.newCall(request).execute()
        if(response.isSuccessful) {
            val obj = JSONObject(response.body!!.string())
            val status = obj["status"].toString()
            if (status == "true") {
                val message = obj["message"].toString()
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else if(status == "false"){
                val message = obj["message"].toString()
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                return
            }
        }else{
            Toast.makeText(this, "ไม่สามารถเชื่อต่อกับเซิร์ฟเวอร์ได้", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkTimeStamp(value: String) : Boolean{
        val timestamp: Long = System.currentTimeMillis()

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date: Date? = dateFormat.parse(value)

        if (date != null && date.time > timestamp) {
            return true
        } else{
            return false
        }
    }

    private fun dateFormat(calendar: Calendar): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun dateTransfer(value : String): String{
        if (value == "N/A" || value == "null"){
            return value
        }

        val instant = Instant.parse(value)
        val localDate = instant.atZone(ZoneId.of("Asia/Bangkok")).toLocalDate()
        val formattedDate = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        return formattedDate;
    }
    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
}