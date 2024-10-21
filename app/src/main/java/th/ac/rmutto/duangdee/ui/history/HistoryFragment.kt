package th.ac.rmutto.duangdee.ui.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.tabs.TabLayout
import th.ac.rmutto.duangdee.R
import th.ac.rmutto.duangdee.shared_preferences_encrypt.Encryption

class HistoryFragment : Fragment() {

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
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        // หาตำแหน่งของ TabLayout และ FrameLayout
        val tabLayout: TabLayout = view.findViewById(R.id.tabLayout)
        val frameLayout: FrameLayout = view.findViewById(R.id.frameLayout)

        // ตั้งค่าแท็บ
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val fragment: Fragment? = when (tab.position) {
                    0 -> HistoryTarotFragment()
                    1 -> HistoryPalmFragment()
                    else -> null
                }
                if (fragment != null) {
                    replaceFragment(fragment)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                // ไม่ต้องทำอะไร
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                // ไม่ต้องทำอะไร
            }
        })

        // ตั้งค่าให้เลือกแท็บแรกโดยอัตโนมัติ
        tabLayout.getTabAt(0)?.select()

        // แสดง Fragment เริ่มต้น
        replaceFragment(HistoryTarotFragment()) // แสดงแท็บแรกเมื่อเริ่มต้น

        return view
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = childFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment)
        transaction.addToBackStack(null) // เพิ่มลงใน back stack
        transaction.commit()
    }
}
