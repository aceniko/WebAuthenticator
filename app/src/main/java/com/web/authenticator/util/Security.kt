package com.web.authenticator.util

import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import java.io.Serializable
import java.math.BigInteger
import java.security.*
import java.security.spec.*
import java.util.*
import javax.crypto.KeyAgreement
import javax.crypto.KeyGenerator
import javax.crypto.Mac
import javax.crypto.SecretKey
import javax.crypto.spec.DHParameterSpec
import javax.crypto.spec.SecretKeySpec

object Security {

    fun generateKeyPair(): KeyPair {
        val keyGen = KeyPairGenerator.getInstance("EC")
        keyGen.initialize(ECGenParameterSpec("secp384r1"))
        return keyGen.generateKeyPair()
    }

    // Perform Diffie-Hellman key exchange on the Android side
    fun performKeyExchange(myPrivateKey: ByteArray, otherPublicKey: ByteArray): ByteArray {

        val keySpec = X509EncodedKeySpec(otherPublicKey)
        val keyFactory = KeyFactory.getInstance("EC")
        val otherPublicKey = keyFactory.generatePublic(keySpec)
        val privateKeySpec = PKCS8EncodedKeySpec(myPrivateKey)
        val privateKey = keyFactory.generatePrivate(privateKeySpec)
        val keyAgreement = KeyAgreement.getInstance("ECDH")
        keyAgreement.init(privateKey)
        keyAgreement.doPhase(otherPublicKey, true)
        val sharedKey = keyAgreement.generateSecret()
        val sharedKeyHash = MessageDigest.getInstance("SHA384").digest(sharedKey)
        return sharedKeyHash
    }
    fun createPublicKeyFromByteArray(publicKeyBytes: ByteArray): PublicKey? {
        try {
            val kf = KeyFactory.getInstance("DH")
            val keySpec = X509EncodedKeySpec(publicKeyBytes)
            return kf.generatePublic(keySpec)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
    fun byteArrayToBase64UrlSafe(bytes: ByteArray): String {
        val base64UrlSafe = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
        return base64UrlSafe
    }

    fun base64UrlSafeToByteArray(base64UrlSafe: String): ByteArray {
        val bytes = Base64.getUrlDecoder().decode(base64UrlSafe)
        return bytes
    }

    fun storeByteArrayInKeystore(context: Context, keyAlias: String, byteArray: ByteArray) {
        // Generate the secret key
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        val keyGenSpec = KeyGenParameterSpec.Builder(keyAlias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            .build()
        keyGenerator.init(keyGenSpec)
        keyGenerator.generateKey()

        // Store the byte array in the keystore as a custom value
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        val keyEntry = keyStore.getEntry(keyAlias, null) as KeyStore.SecretKeyEntry
        val secretKey = keyEntry.secretKey
        val secretKeyBytes = secretKey.encoded
        val customKeyInfo = CustomKeyInfo(byteArray)
        keyStore.setEntry(keyAlias, KeyStore.SecretKeyEntry(secretKey), CustomKeyProtectionParameter(customKeyInfo))
    }
    fun digest(
        msg: String,
        key: ByteArray,
        alg: String = "HmacSHA256"
    ): String {
        val signingKey = SecretKeySpec(key, alg)
        val mac = Mac.getInstance(alg)
        mac.init(signingKey)

        val bytes = mac.doFinal(msg.toByteArray())
        return format(bytes)
    }

    private fun format(bytes: ByteArray): String {
        val formatter = Formatter()
        bytes.forEach { formatter.format("%02x", it) }
        return formatter.toString()
    }
    fun getSecretFromKeystore(context: Context, keyAlias: String): ByteArray? {
        try {
            // Load the Keystore
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)

            // Get the secret key entry from the Keystore
            val entry = keyStore.getEntry(keyAlias, null) as? KeyStore.SecretKeyEntry
            val secretKey: SecretKey? = entry?.secretKey

            // Return the secret key as byte array
            return secretKey?.encoded
        } catch (e: Exception) {
            // Handle any exceptions that may occur
            e.printStackTrace()
        }

        return null
    }

    class CustomKeyInfo(val byteArray: ByteArray) : java.io.Serializable

    class CustomKeyProtectionParameter(val customKeyInfo: CustomKeyInfo) : KeyStore.ProtectionParameter,
        Serializable {

        fun isEncryptionRequired(): Boolean {
            return false
        }

        fun getProtectionAlgorithm(): String {
            return "NONE"
        }

        fun getProtectionParameters(): AlgorithmParameters? {
            return null
        }

        fun getProtectionParameter(): KeyStore.ProtectionParameter {
            return this
        }
    }
}