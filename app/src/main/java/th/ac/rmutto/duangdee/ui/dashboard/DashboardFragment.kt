package th.ac.rmutto.duangdee.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import th.ac.rmutto.duangdee.R
import th.ac.rmutto.duangdee.databinding.FragmentDashboardBinding
import th.ac.rmutto.duangdee.shared_preferences_encrypt.Encryption
import th.ac.rmutto.duangdee.ui.horoscope.tarot.TarotActivity

class DashboardFragment : Fragment() {

    private lateinit var encryption: Encryption

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        encryption = Encryption(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val btSummary = view.findViewById<Button>(R.id.bt_summary)

        btSummary.setOnClickListener{
            val intent = Intent(activity, DashboardResultActivity::class.java)
            intent.putExtra("page_type","Dashboard")
            startActivity(intent)
        }

        return view
    }
}