package th.ac.rmutto.duangdee.ui.history

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import th.ac.rmutto.duangdee.MainActivity
import th.ac.rmutto.duangdee.R
import th.ac.rmutto.duangdee.shared_preferences_encrypt.Encryption
import th.ac.rmutto.duangdee.shared_preferences_encrypt.Encryption.Companion.decrypt
import th.ac.rmutto.duangdee.ui.horoscope.zodiac.ZodiacResultActivity
import th.ac.rmutto.duangdee.ui.login.LoginActivity

class HistoryTarotFragment : Fragment() {
    private var cardName : String? = null
    private var cardWorkTopic : String? = null
    private var cardFinanceTopic : String? = null
    private var cardLoveTopic : String? = null
    private var cardImageFile : String? = null

    private lateinit var encryption: Encryption
    private var recyclerView: RecyclerView? = null
    private var data = ArrayList<Data>()

    private var tokens: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Encryption
        encryption = Encryption(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_history_tarot, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)

        // Set LayoutManager for RecyclerView
        recyclerView?.layoutManager = LinearLayoutManager(requireContext())

        // Start Decryption SharedPreferences
        val sharedPref = requireActivity().getSharedPreferences("DuangDee_Pref", Context.MODE_PRIVATE)
        val usersID = sharedPref.getString("usersID", null)
        val token = sharedPref.getString("token", null)

        // Attempt to decrypt the token
        tokens = token?.let { decrypt(it, encryption.getKeyFromPreferences()) }
        val decode = usersID?.let { decrypt(it, encryption.getKeyFromPreferences()) }
        if (decode != null) {
            showDataList(decode)
        } else {
            startActivity(Intent(activity, LoginActivity::class.java))
            activity?.finish()
        }

        return view
    }

    // Show a data list
    private fun showDataList(usersID: String) {
        val url: String = getString(R.string.url_server) + getString(R.string.port_3000) + getString(R.string.api_get_playcard_top7) + usersID

        // Use coroutines for background tasks
        CoroutineScope(Dispatchers.IO).launch {
            val okHttpClient = OkHttpClient()
            val request: Request = Request.Builder().url(url).get().addHeader("x-access-token", tokens.toString()).build()
            try {
                val response = okHttpClient.newCall(request).execute()

                if (response.isSuccessful) {
                    val res = JSONArray(response.body!!.string())
                    if (res.length() > 0) {
                        for (i in 0 until res.length()) {
                            val item: JSONObject = res.getJSONObject(i)
                            data.add(
                                Data(
                                    item.getString("PlayCard_RegisDate"),
                                    item.getString("Card_ID")
                                )
                            )
                        }

                        // Update UI on the main thread
                        withContext(Dispatchers.Main) {
                            recyclerView?.adapter = DataAdapter(data)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "No data", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Error: ${response.code}", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun getCardData(cardID: String): String {
        val url = getString(R.string.url_server) + getString(R.string.port_3000) + getString(R.string.api_get_card) + cardID
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
                    return obj.optString("Card_Name", "N/A")
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
        return "N/A"
    }

    internal class Data(
        var playCardRegisDate: String,
        var cardID: String
    )

    internal inner class DataAdapter(private val list: List<Data>) :
        RecyclerView.Adapter<DataAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View = LayoutInflater.from(parent.context).inflate(
                R.layout.view_historylist,
                parent, false
            )
            return ViewHolder(view)
        }

        internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var data: Data? = null
            var linearLayoutHistoryList: LinearLayout = itemView.findViewById(R.id.LinearLayout_HistoryList)
            var textTarot: TextView = itemView.findViewById(R.id.textTarot)
            var txtDate: TextView = itemView.findViewById(R.id.txtDate)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val data = list[position]
            holder.data = data

            holder.textTarot.text = getCardData(data.cardID)
            holder.txtDate.text = data.playCardRegisDate

            // Set click listener for the image
            holder.linearLayoutHistoryList.setOnClickListener {
                dialogPredict(data.cardID)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun dialogPredict(cardID: String) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_tarot_result, null)
        val dialogBuilder = AlertDialog.Builder(requireContext()).setView(dialogView)
        val dialog = dialogBuilder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val txtCardName = dialogView.findViewById<com.google.android.material.textview.MaterialTextView>(R.id.txt_CardName)
        val txtWork = dialogView.findViewById<com.google.android.material.textview.MaterialTextView>(R.id.txtWork)
        val txtFinance = dialogView.findViewById<com.google.android.material.textview.MaterialTextView>(R.id.txtFinance)
        val txtLove = dialogView.findViewById<com.google.android.material.textview.MaterialTextView>(R.id.txtLove)

        val btAccept = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btAccept)
        val btClose = dialogView.findViewById<com.google.android.material.imageview.ShapeableImageView>(R.id.btClose)

        btAccept.visibility = View.GONE
        showCard(cardID)

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

        btClose.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showCard(cardID : String){
        val url = getString(R.string.url_server)+ getString(R.string.port_3000) + getString(R.string.api_get_card) + cardID
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
                cardName = obj["Card_Name"].toString()
                cardWorkTopic = obj.optString("Card_WorkTopic", "N/A")
                cardFinanceTopic = obj.optString("Card_FinanceTopic", "N/A")
                cardLoveTopic = obj.optString("Card_LoveTopic", "N/A")
                cardImageFile = obj.optString("Card_ImageFile", "N/A")
            } else {
                startActivity(Intent(activity, MainActivity::class.java))
                activity?.finish()
            }
        } else {
            startActivity(Intent(activity, MainActivity::class.java))
            activity?.finish()
        }
    }
}
