package th.ac.rmutto.duangdee.ui.horoscope

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import th.ac.rmutto.duangdee.R
import th.ac.rmutto.duangdee.shared_preferences_encrypt.Encryption
import th.ac.rmutto.duangdee.shared_preferences_encrypt.Encryption.Companion.decrypt
import th.ac.rmutto.duangdee.ui.login.LoginActivity

class HoroscopeFragment : Fragment() {
    private lateinit var encryption: Encryption
    private lateinit var userID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        encryption = Encryption(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_horoscope, container, false)

        // Start Decryption SharedPreferences
        val sharedPref = requireActivity().getSharedPreferences("DuangDee_Pref", Context.MODE_PRIVATE)
        val usersID = sharedPref.getString("usersID", null)

        val decode = usersID?.let { decrypt(it, encryption.getKeyFromPreferences()) }
        if (decode != null) {
            userID = decode
        } else {
            startActivity(Intent(activity, LoginActivity::class.java))
            activity?.finish()
        }

        val imgBtZodiac = view.findViewById<ImageButton>(R.id.imgBtZodiac)

        imgBtZodiac.setOnClickListener {
            Toast.makeText(requireContext(), "Zodiac Button Clicked!", Toast.LENGTH_SHORT).show()
            showDialog()
        }

        return view
    }

    private fun showDialog() {
        // Inflate the dialog's custom layout
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_zodiac_check, null)

        // Build an AlertDialog using the custom layout
        val dialogBuilder = AlertDialog.Builder(requireContext()).setView(dialogView)

        // Create and show the dialog
        val dialog = dialogBuilder.create()

        // Find the OK button in the dialog layout and set an onClickListener
        val buttonOk = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.buttonok)
        buttonOk.setOnClickListener {
            Toast.makeText(requireContext(), "OK Clicked!", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.show()
    }
}