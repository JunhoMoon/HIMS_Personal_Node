package com.hims.personal_node.encryption

import android.security.keystore.KeyProperties
import java.security.PrivateKey
import java.security.PublicKey
import java.util.Base64
import javax.crypto.Cipher

@ExperimentalStdlibApi
object EncryptionRSA_Test{
    val cipher = Cipher.getInstance("RSA")

    private const val CIPHER_ALGORITHM =
        "${KeyProperties.KEY_ALGORITHM_RSA}/" +
                "${KeyProperties.BLOCK_MODE_ECB}/" +
                KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1

    fun encryption(value:String, publicKey:PublicKey):String{
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        val byteValue: ByteArray? = cipher.doFinal(value.encodeToByteArray())
        return Base64.getEncoder().encodeToString(byteValue)
    }

    fun decryption(value:String, privateKey: PrivateKey):String{
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        val byteValue: ByteArray? = Base64.getDecoder().decode(value.encodeToByteArray())
        return String(cipher.doFinal(byteValue), Charsets.UTF_8)
    }

    fun encrypt2(plainText: String, publicKey: PublicKey): String {
        val cipher = Cipher.getInstance(CIPHER_ALGORITHM).apply {
            init(Cipher.ENCRYPT_MODE, publicKey)
        }
        val bytes = plainText.toByteArray(Charsets.UTF_8)
        val encryptedBytes = cipher.doFinal(bytes)
        val base64EncryptedBytes = android.util.Base64.encode(encryptedBytes, android.util.Base64.DEFAULT)

        return String(base64EncryptedBytes)
    }
}
