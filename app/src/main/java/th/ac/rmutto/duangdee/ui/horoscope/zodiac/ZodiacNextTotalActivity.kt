package th.ac.rmutto.duangdee.ui.horoscope.zodiac

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import th.ac.rmutto.duangdee.R

class ZodiacNextTotalActivity : AppCompatActivity() {
    private var recyclerView: RecyclerView? = null
    private var data = ArrayList<Data>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_zodiac_next_total)

        // Initialize the RecyclerView
        recyclerView = findViewById(R.id.recyclerView) // Ensure you set the correct ID

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val imageBack = findViewById<ImageView>(R.id.image_back)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.app_name)
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))

        // Show the data list
        showDataList()

        imageBack.setOnClickListener {
            val intent = Intent(this, ZodiacTotalActivity::class.java)
            startActivity(intent)
        }
    }

    // Show a data list
    private fun showDataList() {
        val url: String = getString(R.string.url_server) + getString(R.string.api_get_zodiac)

        // Use coroutines for background tasks
        CoroutineScope(Dispatchers.IO).launch {
            val okHttpClient = OkHttpClient()
            val request: Request = Request.Builder().url(url).get().build()
            try {
                val response = okHttpClient.newCall(request).execute()

                if (response.isSuccessful) {
                    val res = JSONArray(response.body!!.string())
                    if (res.length() > 0) {
                        for (i in 6 until 12) {
                            val item: JSONObject = res.getJSONObject(i)
                            data.add(
                                Data(
                                    item.getString("Zodiac_ID"),
                                    item.getString("Zodiac_Name"),
                                    item.getString("Zodiac_Detail"),
                                    item.getString("Zodiac_ImageFile")
                                )
                            )
                        }

                        // Update UI on the main thread
                        withContext(Dispatchers.Main) {
                            recyclerView!!.adapter = DataAdapter(data)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@ZodiacNextTotalActivity, "ไม่สามารถแสดงข้อมูลได้", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@ZodiacNextTotalActivity, "Error: ${response.code}", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ZodiacNextTotalActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    internal class Data(
        var zodiacID: String,
        var zodiacName: String,
        var zodiacDetail: String,
        var zodiacImageFile: String
    )

    internal inner class DataAdapter(private val list: List<Data>) :
        RecyclerView.Adapter<DataAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View = LayoutInflater.from(parent.context).inflate(
                R.layout.menu_zodiac,
                parent, false
            )
            return ViewHolder(view)
        }

        internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var data: Data? = null
            var linearLayoutZodiac: LinearLayout = itemView.findViewById(R.id.LinearLayout_Zodiac)
            var zodiacImageFile: ImageView = itemView.findViewById(R.id.imageZodiac)
            var zodiacName: TextView = itemView.findViewById(R.id.textViewZodiacName)
            var zodiacDetail: TextView = itemView.findViewById(R.id.textViewDateZodiac)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val data = list[position]
            holder.data = data
            val url = getString(R.string.url_server) + data.zodiacImageFile

            // Load image using Picasso
            Picasso.get().load(url).into(holder.zodiacImageFile)
            holder.zodiacName.text = data.zodiacName
            holder.zodiacDetail.text = data.zodiacDetail

            // Set click listener for the image
            holder.linearLayoutZodiac.setOnClickListener {
                val intent = Intent(this@ZodiacNextTotalActivity, ZodiacResultActivity::class.java)
                intent.putExtra("zodiacID", data.zodiacID)
                intent.putExtra("page_type", "Total")
                startActivity(intent)
            }
        }
    }
}