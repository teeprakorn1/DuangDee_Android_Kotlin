package th.ac.rmutto.duangdee.ui.horoscope.palmprint

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import th.ac.rmutto.duangdee.MainActivity
import th.ac.rmutto.duangdee.R
import th.ac.rmutto.duangdee.shared_preferences_encrypt.Encryption
import th.ac.rmutto.duangdee.shared_preferences_encrypt.Encryption.Companion.decrypt
import java.io.File

class PalmprintCameraActivity : AppCompatActivity() {
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private var imageUri: Uri? = null

    private lateinit var encryption: Encryption
    private var tokens: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPref = getSharedPreferences("DuangDee_Pref", Context.MODE_PRIVATE)
        val usersID = sharedPref.getString("usersID", null)
        val token = sharedPref.getString("token", null)

        encryption = Encryption(this)
        tokens = decrypt(token.toString(), encryption.getKeyFromPreferences())
        val decode = decrypt(usersID.toString(), encryption.getKeyFromPreferences())

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_palmprint_camera)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val pageType = intent.getStringExtra("page_type")
        if (pageType == null){
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        when (pageType) {
            "Horoscope", "Home", "CameraActivity" -> {
                if (pageType == "CameraActivity") {
                    imageUri = intent.getStringExtra("image_path").toString().toUri()
                    updateImageProfile(decode)
                }
            }
            else -> {
                // ส่งกลับไปยัง MainActivity หากไม่ตรงกับค่าที่คาดไว้
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }


        val cameraBtn = findViewById<Button>(R.id.CameraBtn)
        val btClose = findViewById<ImageButton>(R.id.bt_close)
        val checkBox = findViewById<CheckBox>(R.id.checkBox)

        btClose.setOnClickListener {
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        cameraBtn.setOnClickListener {
            if (!checkBox.isChecked){
                Toast.makeText(this, "กรุณายอมรับข้อกำหนดและเงื่อนไข", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else {
                intent = Intent(this, CameraActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun updateImageProfile(usersID: String) {
        val url = getString(R.string.url_server) + getString(R.string.port_5000) + getString(R.string.api_palmprint_ai)
        val okHttpClient = OkHttpClient()

        val imageUri = imageUri ?: run {
            Toast.makeText(this, "ยังไม่ได้เลือกรูปภาพ", Toast.LENGTH_SHORT).show()
            return
        }

        // แปลง Uri เป็นเส้นทางไฟล์จริง
        val realPath = getRealPathFromURI(imageUri) ?: run {
            Toast.makeText(this, "ไม่สามารถแปลงรูปภาพได้", Toast.LENGTH_SHORT).show()
            return
        }

        val file = File(realPath)
        val requestFile = file.asRequestBody("image/jpeg".toMediaType())
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("palmprint", file.name, requestFile)
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        val response = okHttpClient.newCall(request).execute()
        if (response.isSuccessful) {
            val obj = JSONObject(response.body!!.string())
            val status = obj["status"].toString()
            if (status == "true") {
                val result = obj["result"].toString()
                val imagePath = obj["image_path"].toString()
                playHand(usersID, result.toFloat(), imagePath)

            }else{
                val message = obj["message"].toString()
                Toast.makeText(this, message , Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this, "อัปโหดรูปภาพไม่สำเร็จ", Toast.LENGTH_SHORT).show()
        }
    }
    private fun playHand(usersID : String, summaryScore : Float, imagePath : String){
        var handDetailID = -1
        if (summaryScore > getHandDetail(10)){
            handDetailID = 10
        }else if (summaryScore > getHandDetail(9)){
            handDetailID = 9
        }else if (summaryScore > getHandDetail(8)){
            handDetailID = 8
        }else if (summaryScore > getHandDetail(7)){
            handDetailID = 7
        }else if (summaryScore > getHandDetail(6)){
            handDetailID = 6
        }else if (summaryScore > getHandDetail(5)){
            handDetailID = 5
        }else if (summaryScore > getHandDetail(4)){
            handDetailID = 7
        }else if (summaryScore > getHandDetail(3)){
            handDetailID = 3
        }else if (summaryScore > getHandDetail(2)){
            handDetailID = 2
        }else if (summaryScore > getHandDetail(1)){
            handDetailID = 1
        }

        if (handDetailID != -1){
            val url = getString(R.string.url_server) + getString(R.string.port_3000) + getString(R.string.api_add_playhand)
            val okHttpClient = OkHttpClient()
            val formBody: RequestBody = FormBody.Builder()
                .add("Users_ID", usersID)
                .add("HandDetail_ID", handDetailID.toString())
                .add("PlayHand_Score", summaryScore.toString())
                .add("PlayHand_ImageFile", imagePath)
                .build()
            val request: Request = Request.Builder()
                .url(url)
                .post(formBody)
                .addHeader("x-access-token", tokens.toString())
                .build()
            val response = okHttpClient.newCall(request).execute()
            if(response.isSuccessful) {
                val obj = JSONObject(response.body!!.string())
                val status = obj["status"].toString()
                if (status == "true") {
                    val intent = Intent(this, PalmprintResultActivity::class.java)
                    intent.putExtra("HandDetail_ID", handDetailID.toString())
                    intent.putExtra("PlayHand_Score", summaryScore.toString())
                    intent.putExtra("ImagePath", imagePath)
                    intent.putExtra("page_type", "PalmprintCamera")
                    startActivity(intent)
                } else if(status == "false"){
                    val message = obj["message"].toString()
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    return
                }
            }else{
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun getHandDetail(handDetailID : Int) : Float{
        val url = getString(R.string.url_server) + getString(R.string.port_3000) + getString(R.string.api_get_handdetail) + handDetailID
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder()
            .url(url)
            .get()
            .addHeader("x-access-token", tokens.toString())
            .build()

        val response = okHttpClient.newCall(request).execute()
        if (response.isSuccessful) {
            val obj = JSONObject(response.body!!.string())
            val status = obj["status"].toString()
            if (status == "true") {
                return obj["HandDetail_MinPercent"].toString().toFloat()
            } else {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        } else {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        return -1f
    }


    // ฟังก์ชันสำหรับแปลง Uri เป็นเส้นทางไฟล์จริง
    private fun getRealPathFromURI(contentUri: Uri): String? {
        try {
            val inputStream = contentResolver.openInputStream(contentUri) ?: return null
            val file = File(cacheDir, "temp_image.jpg")
            val outputStream = file.outputStream()

            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            return file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}
