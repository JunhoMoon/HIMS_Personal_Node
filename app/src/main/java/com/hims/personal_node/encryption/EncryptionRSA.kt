package com.hims.personal_node.encryption

import java.security.PrivateKey
import java.security.PublicKey
import java.util.Base64
import javax.crypto.Cipher

@ExperimentalStdlibApi
object EncryptionRSA{
    val cipher = Cipher.getInstance("RSA")
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
}
