package th.ac.rmutto.duangdee.ui.profile

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import th.ac.rmutto.duangdee.R
import th.ac.rmutto.duangdee.shared_preferences_encrypt.Encryption
import th.ac.rmutto.duangdee.shared_preferences_encrypt.Encryption.Companion.decrypt
import th.ac.rmutto.duangdee.ui.login.LoginActivity
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ProfileFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var encryption: Encryption

    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private var imageUri: Uri? = null

    private var usersDisplayName: String? = null
    private var usersFirstName: String? = null
    private var usersLastName: String? = null
    private var usersPhone: String? = null
    private var usersDateOfBirth: String? = null
    private var usersGender: String? = null
    private var imageName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Encryption
        encryption = Encryption(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val imageProfile = view.findViewById<ImageView>(R.id.imageProfile)

        val textNameUser = view.findViewById<TextView>(R.id.ResultDisplayname)
        val textFirstName = view.findViewById<TextView>(R.id.ResultFirstname)
        val textLastName = view.findViewById<TextView>(R.id.ResultLastname)
        val textPhone = view.findViewById<TextView>(R.id.ResultPhone)
        val textBrithDay = view.findViewById<TextView>(R.id.ResultBrithDay)
        val textGender = view.findViewById<TextView>(R.id.ResultGender)

        val btnLogout = view.findViewById<Button>(R.id.btnLogout)
        val btnEdit = view.findViewById<Button>(R.id.btnEdit)

        // Start Decryption SharedPreferences
        val sharedPref = requireActivity().getSharedPreferences("DuangDee_Pref", Context.MODE_PRIVATE)
        val usersID = sharedPref.getString("usersID", null)

        // Attempt to decrypt the token
        val decode = usersID?.let { decrypt(it, encryption.getKeyFromPreferences()) }
        if (decode != null) {
            updateProfile(decode, view)
            textNameUser.text = usersDisplayName
            textFirstName.text = usersFirstName
            textLastName.text = usersLastName
            textPhone.text = usersPhone
            textGender.text = usersGender
            textBrithDay.text = dateFormat(usersDateOfBirth.toString())
        } else {
            startActivity(Intent(activity, LoginActivity::class.java))
            activity?.finish()
        }

        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                imageUri = data?.data
                imageUri?.let {
                    updateImageProfile(decode.toString())
                    if (decode != null) {
                        updateProfile(decode, view)
                    }
                }
            }
        }

        imageProfile.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .createIntent { intent ->
                    imagePickerLauncher.launch(intent)
                }
        }

        btnLogout.setOnClickListener {
            dialogLogout()
        }

        btnEdit.setOnClickListener {
            val intent = Intent(requireActivity(), EditProfileActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun updateProfile(userID: String, view: View){
        var url = getString(R.string.url_server) + getString(R.string.api_get_profile) + userID
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()
        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val obj = JSONObject(response.body!!.string())
                val status = obj["status"].toString()
                if (status == "true") {
                    usersDisplayName = obj["Users_DisplayName"].toString()
                    usersFirstName = obj.optString("Users_FirstName", "N/A")
                    usersLastName = obj.optString("Users_LastName", "N/A")
                    usersPhone = obj.optString("Users_Phone", "N/A")
                    usersDateOfBirth = obj.optString("Users_BirthDate", "N/A")
                    usersGender = obj.optString("UsersGender_Name", "N/A")
                    imageName = obj.optString("Users_ImageFile", "N/A")

                    if (imageName != "null") {
                        val imageProfile = view.findViewById<ImageView>(R.id.imageProfile)
                        url = getString(R.string.url_server) + imageName.toString()
                        // Load image using Glide
                        Glide.with(this)
                            .load(url)
                            .into(imageProfile)
                    }
                } else {
                    startActivity(Intent(activity, LoginActivity::class.java))
                    activity?.finish()
                }
            } else {
                startActivity(Intent(activity, LoginActivity::class.java))
                activity?.finish()
            }
        } catch (e: Exception) {
            Log.e("VerifyTokenError", "Error verifying token", e)
            startActivity(Intent(activity, LoginActivity::class.java))
            activity?.finish()
        }
    }

    private fun deleteImageProfile(imagePath: String, userID : String) {
        val url = getString(R.string.url_server) + getString(R.string.api_delete_profile_image) + userID
        val okHttpClient = OkHttpClient()

        val formBody: RequestBody = FormBody.Builder()
            .add("imagePath",imagePath)
            .build()
        val request: Request = Request.Builder()
            .url(url)
            .delete(formBody)
            .build()
        val response = okHttpClient.newCall(request).execute()
        if(response.isSuccessful){
            val obj = JSONObject(response.body!!.string())
            val status = obj["status"].toString()
            if (status == "false") {
                val message = obj["message"].toString()
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateImageProfile(userID: String) {
        val url = getString(R.string.url_server) + getString(R.string.api_update_profile_image) + userID
        val okHttpClient = OkHttpClient()

        val imageUri = imageUri ?: run {
            Toast.makeText(requireContext(), "ไม่ได้เลือกรูปภาพ", Toast.LENGTH_SHORT).show()
            return
        }

        val file = File(imageUri.path!!)
        val requestFile = file.asRequestBody("image/jpeg".toMediaType())
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("Profile_Image", file.name, requestFile)
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .put(requestBody)
            .build()
        val response = okHttpClient.newCall(request).execute()
        if (response.isSuccessful) {
            val obj = JSONObject(response.body!!.string())
            val status = obj["status"].toString()
            if (status == "true") {
                Toast.makeText(requireContext(), "อัปโหดรูปภาพสำเร็จ", Toast.LENGTH_SHORT).show()
                deleteImageProfile(imageName.toString(), userID)
            }else{
                val message = obj["message"].toString()
                Toast.makeText(requireContext(), message , Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(requireContext(), "อัปโหดรูปภาพไม่สำเร็จ", Toast.LENGTH_SHORT).show()
        }
    }

    private fun signOutOAuth() {
        auth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        googleSignInClient.signOut().addOnCompleteListener {
            // Handle sign-out success or failure
            if (it.isSuccessful) {
                auth.signOut()
                startActivity(Intent(activity, LoginActivity::class.java))
                activity?.finish()
            } else {
                // Optionally, handle sign-out failure
                Toast.makeText(requireContext(), "Sign-out failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun dateFormat(value : String): String{
        if (value == "N/A" || value == "null"){
            return value
        }else{
        val instant = Instant.parse(value)
        val localDate = instant.atZone(ZoneId.of("Asia/Bangkok")).toLocalDate()
        val formattedDate = localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        return formattedDate;
        }
    }

    private fun dialogLogout() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_logout, null)
        val sharedPref = requireActivity().getSharedPreferences("DuangDee_Pref", Context.MODE_PRIVATE)
        val dialogBuilder = AlertDialog.Builder(requireContext()).setView(dialogView)
        val dialog = dialogBuilder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val yesBtn = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.yesBtn)
        val noBtn = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.noBtn)

        yesBtn.setOnClickListener {
            val regisTypeId = sharedPref.getString("regisTypeId", null)
            val decodeTypeID = regisTypeId?.let { decrypt(it, encryption.getKeyFromPreferences()) }
            with(sharedPref.edit()) {
                clear()
                apply()
            }
            signOutOAuth()
            if (decodeTypeID == "1"){
                startActivity(Intent(activity, LoginActivity::class.java))
                activity?.finish()
            }else if(decodeTypeID == "2"){
                signOutOAuth()
            }
            dialog.dismiss()
        }

        noBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}
