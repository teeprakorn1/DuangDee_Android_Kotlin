package th.ac.rmutto.duangdee.ui.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import th.ac.rmutto.duangdee.R
import th.ac.rmutto.duangdee.ui.horoscope.zodiac.ZodiacTotalActivity.Data

class HistoryTarotFragment : Fragment() {
    private var recyclerView: RecyclerView? = null
    private var data = ArrayList<Data>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_history_tarot, container, false);

        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView);

        return view;
    }
}