package th.ac.rmutto.duangdee.shared_preferences_encrypt

import android.content.Context
import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

class Encryption(private val context: Context) {
    companion object {
        private const val ALGORITHM = "AES"
        private const val KEY_SIZE = 256
        private const val PREFS_NAME = "encryption_prefs"
        private const val KEY_PREF_NAME = "encryption_key"

        fun generateKey(): SecretKey {
            val keyGenerator = KeyGenerator.getInstance(ALGORITHM)
            keyGenerator.init(KEY_SIZE)
            return keyGenerator.generateKey()
        }

        fun encrypt(value: String, secretKey: SecretKey?): String {
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            val encryptedValue = cipher.doFinal(value.toByteArray())
            return Base64.encodeToString(encryptedValue, Base64.DEFAULT)
        }

        fun decrypt(encryptedValue: String, secretKey: SecretKey?): String {
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, secretKey)
            val decodedValue = Base64.decode(encryptedValue, Base64.DEFAULT)
            val decryptedValue = cipher.doFinal(decodedValue)
            return String(decryptedValue)
        }
    }

    fun saveKeyToPreferences(key: SecretKey) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val keyString = Base64.encodeToString(key.encoded, Base64.DEFAULT)
        editor.putString(KEY_PREF_NAME, keyString)
        editor.apply()
    }

    fun getKeyFromPreferences(): SecretKey? {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val keyString = sharedPreferences.getString(KEY_PREF_NAME, null) ?: return null
        val keyBytes = Base64.decode(keyString, Base64.DEFAULT)
        return SecretKeySpec(keyBytes, ALGORITHM)
    }
}

