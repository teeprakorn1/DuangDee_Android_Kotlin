package th.ac.rmutto.duangdee

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import th.ac.rmutto.duangdee.databinding.ActivityLoadingBinding
import th.ac.rmutto.duangdee.ui.horoscope.tarot.TarotActivity
import th.ac.rmutto.duangdee.ui.horoscope.zodiac.ZodiacResultActivity
import th.ac.rmutto.duangdee.ui.login.LoginActivity

class LoadingActivity : AppCompatActivity() {
    private val setTime: Long = 3000 // ระยะเวลาในการแสดงหน้าจอโหลด
    private lateinit var binding: ActivityLoadingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // เริ่มต้น View Binding
        binding = ActivityLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // เริ่มการ Animation ของจุด
        startDotAnimation()

        // รับข้อมูลจาก Intent เพื่อกำหนด Activity เป้าหมาย
        val targetActivity = intent.getStringExtra("target_activity")
        val pageType = intent.getStringExtra("page_type")

        // รอเวลา setTime ก่อนจะไปยัง Activity เป้าหมาย
        Handler(Looper.getMainLooper()).postDelayed({
            // ตรวจสอบว่ามี targetActivity ที่ต้องไปหรือไม่
            navigateToTargetActivity(targetActivity, pageType)
        }, setTime)
    }

    private fun startDotAnimation() {
        val dots = listOf(binding.dot1, binding.dot2, binding.dot3)

        for ((index, dot) in dots.withIndex()) {
            // ใช้ PropertyValuesHolder เพื่อปรับขนาดตามแกน X และ Y
            ObjectAnimator.ofPropertyValuesHolder(
                dot,
                PropertyValuesHolder.ofFloat("scaleX", 1.5f, 0.5f, 1.5f), // ขยายและหดกลับในแนว X
                PropertyValuesHolder.ofFloat("scaleY", 1.5f, 0.5f, 1.5f)  // ขยายและหดกลับในแนว Y
            ).apply {
                duration = 500
                startDelay = (index * 150).toLong() // เว้นระยะการขยับของแต่ละจุด
                repeatCount = ObjectAnimator.INFINITE
                interpolator = AccelerateDecelerateInterpolator()
                start()
            }
        }
    }

    private fun navigateToTargetActivity(targetActivity: String?, pageType: String?) {
        // ตรวจสอบ targetActivity และไปยังหน้าเป้าหมายที่ถูกต้อง
        val intent = when (targetActivity) {
            "TarotActivity" -> Intent(this, TarotActivity::class.java).apply {
                putExtra("page_type", pageType)
            }
            "ZodiacResultActivity" -> Intent(this, ZodiacResultActivity::class.java).apply {
                putExtra("page_type", pageType)
            }
            "LoginActivity" -> Intent(this, LoginActivity::class.java)
            else -> null
        }

        intent?.let {
            startActivity(it)
            finish() // ปิด LoadingActivity
        }
    }
    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
}
