package th.ac.rmutto.duangdee.ui.horoscope.tarot

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import th.ac.rmutto.duangdee.MainActivity
import th.ac.rmutto.duangdee.R
import th.ac.rmutto.duangdee.ui.login.LoginActivity
import th.ac.rmutto.duangdee.ui.register.RegisMailActivity

class TarotActivity : AppCompatActivity() {
    private val context = this
    private var valueCard: Int = 0

    private var cardName : String? = null
    private var cardWorkTopic : String? = null
    private var cardFinanceTopic : String? = null
    private var cardLoveTopic : String? = null
    private var cardImageFile : String? = null

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
        val btBack = findViewById<ImageView>(R.id.bt_back)

        // Initialize all tarot card image views
        val tarotCards = listOf(
            findViewById<ImageView>(R.id.imagetarot1),
            findViewById<ImageView>(R.id.imagetarot2),
            findViewById<ImageView>(R.id.imagetarot3),
            findViewById<ImageView>(R.id.imagetarot4),
            findViewById<ImageView>(R.id.imagetarot5),
            findViewById<ImageView>(R.id.imagetarot6),
            findViewById<ImageView>(R.id.imagetarot7),
            findViewById<ImageView>(R.id.imagetarot8),
            findViewById<ImageView>(R.id.imagetarot9),
            findViewById<ImageView>(R.id.imagetarot10),
            findViewById<ImageView>(R.id.imagetarot11),
            findViewById<ImageView>(R.id.imagetarot12),
            findViewById<ImageView>(R.id.imagetarot13),
            findViewById<ImageView>(R.id.imagetarot14),
            findViewById<ImageView>(R.id.imagetarot15),
            findViewById<ImageView>(R.id.imagetarot16)
        )

        // Set click listeners for all tarot cards
        tarotCards.forEach { tarotCard ->
            tarotCard.setOnClickListener {
                clickItem(tarotCard)
            }
        }

        btBack.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        btPredict.setOnClickListener {
            if (valueCard == 1) {
                dialogPredict()
            } else {
                dialogWarning()
            }
        }
    }

    private fun setCard(view: ImageView) {
        view.setBackgroundResource(if (valueCard == 0) {
            valueCard = 1
            R.drawable.img_duangdee_card_blank
        } else {
            valueCard = 0
            R.drawable.img_duangdee_card
        })
    }

    private fun clickItem(view: ImageView) {
        if (valueCard == 0) {
            setCard(view)
        } else {
            clearCard()
            valueCard = 0
        }
    }

    private fun clearCard(){
        // Reset all cards to the regular card state
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
    }

    @SuppressLint("SetTextI18n")
    private fun dialogPredict() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_tarot_result, null)
        val dialogBuilder = AlertDialog.Builder(this).setView(dialogView)
        val dialog = dialogBuilder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val txtCardName = dialogView.findViewById<com.google.android.material.textview.MaterialTextView>(R.id.txt_CardName)
        val txtWork = dialogView.findViewById<com.google.android.material.textview.MaterialTextView>(R.id.txtWork)
        val txtFinance = dialogView.findViewById<com.google.android.material.textview.MaterialTextView>(R.id.txtFinance)
        val txtLove = dialogView.findViewById<com.google.android.material.textview.MaterialTextView>(R.id.txtLove)

        val btAccept = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btAccept)
        val btClose = dialogView.findViewById<com.google.android.material.imageview.ShapeableImageView>(R.id.btClose)

        val randomNumbers = randomCard(getCount().toInt())
        showCard(randomNumbers.toString())
        clearCard()
        valueCard = 0

        txtCardName.text = cardName
        txtWork.text = "การงาน: $cardWorkTopic"
        txtFinance.text = "การเงิน: $cardFinanceTopic"
        txtLove.text = "ความรัก: $cardLoveTopic"

        if (cardImageFile != "null") {
            val imageCard = dialogView.findViewById<ImageView>(R.id.imageCard) // แก้จาก findViewById เป็น dialogView.findViewById
            val url = getString(R.string.url_server)+ getString(R.string.port_3000) + cardImageFile.toString()

            Glide.with(this)
                .load(url)
                .into(imageCard)
        }

        btAccept.setOnClickListener {
            dialogConfirmTarot()
        }
        btClose.setOnClickListener {
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

        btOK.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun dialogConfirmTarot() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_confirm_tarot, null)
        val dialogBuilder = AlertDialog.Builder(this).setView(dialogView)
        val dialog = dialogBuilder.create()

        val yesBtn = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.yesBtn)
        val noBtn = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.noBtn)

        yesBtn.setOnClickListener {
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        noBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun randomCard(number: Int): Int {
        return (1..number).random()
    }

    private fun showCard(cardID : String){
        val url = getString(R.string.url_server)+ getString(R.string.port_3000) + getString(R.string.api_get_card) + cardID
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()
        val response = okHttpClient.newCall(request).execute()
        if (response.isSuccessful) {
            val obj = JSONObject(response.body!!.string())
            val status = obj["status"].toString()
            if (status == "true") {
                cardName = obj["Card_Name"].toString()
                cardWorkTopic = obj.optString("Card_WorkTopic", "N/A")
                cardFinanceTopic = obj.optString("Card_FinanceTopic", "N/A")
                cardLoveTopic = obj.optString("Card_LoveTopic", "N/A")
                cardImageFile = obj.optString("Card_ImageFile", "N/A")
            } else {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        } else {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getCount() : String {
        val url = getString(R.string.url_server)+ getString(R.string.port_3000) + getString(R.string.api_get_count_card)
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()
        val response = okHttpClient.newCall(request).execute()
        if (response.isSuccessful) {
            val obj = JSONObject(response.body!!.string())
            val status = obj["status"].toString()
            if (status == "true") {
                return obj["Count"].toString()
            } else {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        } else {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        return "0"
    }
}