package th.ac.rmutto.duangdee.ui.horoscope.palmprint

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.airbnb.lottie.LottieAnimationView
import com.google.common.util.concurrent.ListenableFuture
import th.ac.rmutto.duangdee.MainActivity
import th.ac.rmutto.duangdee.R
import java.io.File
import java.io.FileOutputStream

class CameraActivity : AppCompatActivity() {
    private lateinit var previewView: PreviewView
    private lateinit var handOverlay: ImageView // ImageView overlay for hand image
    private var imageCapture: ImageCapture? = null // Used for taking pictures

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_camera)

        // Set up insets for system UI
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize PreviewView and ImageView for hand overlay
        previewView = findViewById(R.id.previewView)
        handOverlay = findViewById(R.id.handOverlay)

        // Set the hand overlay image
        handOverlay.setImageResource(R.drawable.img_hand_defualt) // Replace with your image name

        // Check permissions and start camera
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                10
            )
        }

        val captureButton: Button = findViewById(R.id.captureButton)
        captureButton.setOnClickListener {
            findViewById<LottieAnimationView>(R.id.lottie_loading).visibility = View.VISIBLE
            findViewById<LottieAnimationView>(R.id.lottie_loading).playAnimation()
            Handler(Looper.getMainLooper()).postDelayed({
                capturePhoto()
            }, 500)
        }
    }

    private fun allPermissionsGranted(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                bindPreview(cameraProvider)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        val preview = Preview.Builder().build()
        imageCapture = ImageCapture.Builder().build() // Initialize image capture

        // Set surfaceProvider for PreviewView
        preview.setSurfaceProvider(previewView.surfaceProvider)

        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
    }

    private fun capturePhoto() {
        // Get the ImageCapture instance
        val imageCapture = imageCapture ?: return

        // Create a file to save the image
        val photoFile = File(externalMediaDirs.firstOrNull(), "IMG_${System.currentTimeMillis()}.jpg")

        // Set up output options for the ImageCapture
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Capture the image
        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    // Create Uri from File
                    val photoUri = Uri.fromFile(photoFile)

                    // Start PalmprintCameraActivity with the saved image path
                    val intent = Intent(this@CameraActivity, PalmprintCameraActivity::class.java)
                    intent.putExtra("image_path", photoUri.toString()) // Send the Uri path as a string
                    intent.putExtra("page_type", "CameraActivity")
                    startActivity(intent)
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(this@CameraActivity, "เกิดข้อผิดพลาดในการจับภาพ", Toast.LENGTH_SHORT).show()
                    findViewById<LottieAnimationView>(R.id.lottie_loading).visibility = View.GONE
                }
            }
        )
    }

    private fun compressImage(photoFile: File, quality: Int = 80): File {
        val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
        FileOutputStream(photoFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
        }
        return photoFile
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (allPermissionsGranted()) {
            startCamera()
        }
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
