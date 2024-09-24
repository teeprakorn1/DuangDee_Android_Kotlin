package th.ac.rmutto.duangdee.ui.home

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import th.ac.rmutto.duangdee.R
import th.ac.rmutto.duangdee.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment using View Binding
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btZodiac.setOnClickListener {
            Toast.makeText(requireContext(), "Zodiac Button Clicked!", Toast.LENGTH_SHORT).show()
            showDialog()
        }
    }

    private fun showDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_zodiac_check, null)
        val dialogBuilder = AlertDialog.Builder(requireContext()).setView(dialogView)

        val dialog = dialogBuilder.create()

        val buttonOk = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.buttonok)
        buttonOk.setOnClickListener {
            Toast.makeText(requireContext(), "OK Clicked!", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.show()
    }
}
