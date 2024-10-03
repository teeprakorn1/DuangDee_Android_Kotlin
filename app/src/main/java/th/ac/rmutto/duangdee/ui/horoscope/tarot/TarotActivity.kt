package th.ac.rmutto.duangdee.ui.horoscope.tarot

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import th.ac.rmutto.duangdee.R

//ขนาดไพ่ 968*1615 120dp 200dp
class TarotActivity : AppCompatActivity() {
    val context = this

    private var valueCard: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tarot)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.app_name)
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))

        val btPredict = findViewById<Button>(R.id.bt_predict)

        val tarot1 = findViewById<ImageView>(R.id.imagetarot1)
        val tarot2 = findViewById<ImageView>(R.id.imagetarot2)
        val tarot3 = findViewById<ImageView>(R.id.imagetarot3)
        val tarot4 = findViewById<ImageView>(R.id.imagetarot4)
        val tarot5 = findViewById<ImageView>(R.id.imagetarot5)
        val tarot6 = findViewById<ImageView>(R.id.imagetarot6)
        val tarot7 = findViewById<ImageView>(R.id.imagetarot7)
        val tarot8 = findViewById<ImageView>(R.id.imagetarot8)
        val tarot9 = findViewById<ImageView>(R.id.imagetarot9)
        val tarot10 = findViewById<ImageView>(R.id.imagetarot10)
        val tarot11 = findViewById<ImageView>(R.id.imagetarot11)
        val tarot12 = findViewById<ImageView>(R.id.imagetarot12)
        val tarot13 = findViewById<ImageView>(R.id.imagetarot13)
        val tarot14 = findViewById<ImageView>(R.id.imagetarot14)
        val tarot15 = findViewById<ImageView>(R.id.imagetarot15)
        val tarot16 = findViewById<ImageView>(R.id.imagetarot16)

        tarot1.setOnClickListener {
            clickItem(tarot1)
        }

        tarot2.setOnClickListener {
            clickItem(tarot2)
        }

        tarot3.setOnClickListener {
            clickItem(tarot3)
        }

        tarot4.setOnClickListener {
            clickItem(tarot4)
        }

        tarot5.setOnClickListener {
            clickItem(tarot5)
        }

        tarot6.setOnClickListener {
            clickItem(tarot6)
        }

        tarot7.setOnClickListener {
            clickItem(tarot7)
        }

        tarot8.setOnClickListener {
            clickItem(tarot8)
        }

        tarot9.setOnClickListener {
            clickItem(tarot9)
        }

        tarot10.setOnClickListener {
            clickItem(tarot10)
        }

        tarot11.setOnClickListener {
            clickItem(tarot11)
        }

        tarot12.setOnClickListener {
            clickItem(tarot12)
        }

        tarot13.setOnClickListener {
            clickItem(tarot13)
        }

        tarot14.setOnClickListener {
            clickItem(tarot14)
        }

        tarot15.setOnClickListener {
            clickItem(tarot15)
        }

        tarot16.setOnClickListener {
            clickItem(tarot16)
        }

        btPredict.setOnClickListener {
            if (valueCard == 1) {
                dialogPredict()
            }else{
                dialogWarning()
            }

        }

    }

    private fun setCard(view: ImageView) {
        if (valueCard == 0) {
            view.setBackgroundResource(R.drawable.img_duangdee_card_blank)
            valueCard = 1
        } else if (valueCard == 1) {
            view.setBackgroundResource(R.drawable.img_duangdee_card)
            valueCard = 0
        }
    }

    private fun clickItem(view: ImageView) {
        if (valueCard == 0) {
            setCard(view)
        } else if (valueCard == 1) {
            findViewById<ImageView>(R.id.imagetarot1).setBackgroundResource(R.drawable.img_duangdee_card)
            findViewById<ImageView>(R.id.imagetarot2).setBackgroundResource(R.drawable.img_duangdee_card)
            findViewById<ImageView>(R.id.imagetarot3).setBackgroundResource(R.drawable.img_duangdee_card)
            findViewById<ImageView>(R.id.imagetarot4).setBackgroundResource(R.drawable.img_duangdee_card)
            findViewById<ImageView>(R.id.imagetarot5).setBackgroundResource(R.drawable.img_duangdee_card)
            findViewById<ImageView>(R.id.imagetarot6).setBackgroundResource(R.drawable.img_duangdee_card)
            findViewById<ImageView>(R.id.imagetarot7).setBackgroundResource(R.drawable.img_duangdee_card)
            findViewById<ImageView>(R.id.imagetarot8).setBackgroundResource(R.drawable.img_duangdee_card)
            findViewById<ImageView>(R.id.imagetarot9).setBackgroundResource(R.drawable.img_duangdee_card)
            findViewById<ImageView>(R.id.imagetarot10).setBackgroundResource(R.drawable.img_duangdee_card)
            findViewById<ImageView>(R.id.imagetarot11).setBackgroundResource(R.drawable.img_duangdee_card)
            findViewById<ImageView>(R.id.imagetarot12).setBackgroundResource(R.drawable.img_duangdee_card)
            findViewById<ImageView>(R.id.imagetarot13).setBackgroundResource(R.drawable.img_duangdee_card)
            findViewById<ImageView>(R.id.imagetarot14).setBackgroundResource(R.drawable.img_duangdee_card)
            findViewById<ImageView>(R.id.imagetarot15).setBackgroundResource(R.drawable.img_duangdee_card)
            findViewById<ImageView>(R.id.imagetarot16).setBackgroundResource(R.drawable.img_duangdee_card)
            valueCard = 0
        }
    }

    private fun dialogPredict() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_tarot_result, null)
        val dialogBuilder = AlertDialog.Builder(this).setView(dialogView)
        val dialog = dialogBuilder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val btAccept = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btAccept)
        val btClose = dialogView.findViewById<com.google.android.material.imageview.ShapeableImageView>(R.id.btClose)

        val txtWork = dialogView.findViewById<com.google.android.material.textview.MaterialTextView>(R.id.txtWork)
        //txtWork.text = "การงาน: "
        val txtFinance = dialogView.findViewById<com.google.android.material.textview.MaterialTextView>(R.id.txtFinance)
        //txtFinance.text = "การเงิน: "
        val txtLove = dialogView.findViewById<com.google.android.material.textview.MaterialTextView>(R.id.txtLove)
        //txtLove.text = "ความรัก: "

        btAccept.setOnClickListener{
            dialog.dismiss()
        }
        btClose.setOnClickListener{
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun dialogWarning() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_warning, null)
        val dialogBuilder = AlertDialog.Builder(this).setView(dialogView)
        val dialog = dialogBuilder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val btOK = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btOK)

        btOK.setOnClickListener{
            dialog.dismiss()
        }
        dialog.show()
    }

}
