package com.example.data.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class SecurityManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPrefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "prms_secure_session_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun getOrGenerateDatabasePassphrase(): ByteArray {
        val storedPassHex = encryptedPrefs.getString(KEY_DB_PASSPHRASE, null)
        if (storedPassHex != null) {
            return hexToByteArray(storedPassHex)
        }
        val randomBytes = ByteArray(32)
        SecureRandom().nextBytes(randomBytes)
        val newHex = byteArrayToHex(randomBytes)
        encryptedPrefs.edit().putString(KEY_DB_PASSPHRASE, newHex).apply()
        return randomBytes
    }

    fun generateSalt(): String {
        val salt = ByteArray(16)
        SecureRandom().nextBytes(salt)
        return byteArrayToHex(salt)
    }

    fun hashPassword(password: String, saltHex: String): String {
        return try {
            val saltBytes = hexToByteArray(saltHex)
            val spec = PBEKeySpec(password.toCharArray(), saltBytes, 10000, 256)
            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val hash = factory.generateSecret(spec).encoded
            byteArrayToHex(hash)
        } catch (e: Exception) {
            // Fallback SHA-256 for maximum compatibility
            val md = java.security.MessageDigest.getInstance("SHA-256")
            md.update(hexToByteArray(saltHex))
            val digest = md.digest(password.toByteArray(Charsets.UTF_8))
            byteArrayToHex(digest)
        }
    }

    fun verifyPassword(password: String, saltHex: String, storedHash: String): Boolean {
        val computed = hashPassword(password, saltHex)
        return computed.equals(storedHash, ignoreCase = true)
    }

    fun saveActiveSession(userId: Long, username: String, role: String) {
        encryptedPrefs.edit()
            .putLong(KEY_ACTIVE_USER_ID, userId)
            .putString(KEY_ACTIVE_USERNAME, username)
            .putString(KEY_ACTIVE_ROLE, role)
            .putLong(KEY_SESSION_TIMESTAMP, System.currentTimeMillis())
            .apply()
    }

    fun clearSession() {
        encryptedPrefs.edit()
            .remove(KEY_ACTIVE_USER_ID)
            .remove(KEY_ACTIVE_USERNAME)
            .remove(KEY_ACTIVE_ROLE)
            .remove(KEY_SESSION_TIMESTAMP)
            .apply()
    }

    fun getActiveUsername(): String? = encryptedPrefs.getString(KEY_ACTIVE_USERNAME, null)
    fun getActiveUserRole(): String? = encryptedPrefs.getString(KEY_ACTIVE_ROLE, null)
    fun getActiveUserId(): Long = encryptedPrefs.getLong(KEY_ACTIVE_USER_ID, -1L)

    private fun byteArrayToHex(bytes: ByteArray): String {
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun hexToByteArray(hex: String): ByteArray {
        val len = hex.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(hex[i], 16) shl 4) + Character.digit(hex[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }

    companion object {
        private const val KEY_DB_PASSPHRASE = "secure_sqlcipher_key"
        private const val KEY_ACTIVE_USER_ID = "active_user_id"
        private const val KEY_ACTIVE_USERNAME = "active_username"
        private const val KEY_ACTIVE_ROLE = "active_user_role"
        private const val KEY_SESSION_TIMESTAMP = "session_timestamp"
    }
}
