/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.common.utils

import android.annotation.SuppressLint
import com.google.common.io.BaseEncoding
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec


/**
 * @author admin
 * Created by admin
 * Created on 6/1/20.
 * Package com.halo.common.utils
 */
object RSA {
    const val PUBLIC_KEY =
        "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAoOezrpQgpF1wxHeFgqroXcFVvT6plxVx0a4zsa5umxfXcg7EaKwknhi7YU3ylW4/TyDCWrrJMGHjwD7jjs5dgiUldGudWpRyigUS1uRexeauTJR9f3t9fZ+eKiaUQMQQdH39bBX9chRFGH7NC9uNMxtspHTozHnILb56UXQ7Ozovsz6CD4OW4D2KlZuuf1SbTFroXVw4ceXNpQ9epgNvIu3NIqnpkoUNhulPkQwYVmi8WZiW4laLzlMWnvBRxNvqNaNOBfBXfkPO3QLG8IqasU5DavdTRNSSkqYsFidiB+yqX58Ucur3+Y40uSRwYpAEcr57bak94S1kSVyBXYSrAOFqNmcp4KgoNO/mVTtRwNd4176Vh4ecFQ4LAUSaUugXyX+7eIhAaqA4I/3vjrW8jwbLiKDVI2L/POGGtXfS2fEphTaH7ju1BVJIqOwwM1gyctez6/1uiLk2TcYscludv+7Wjk9dcnFIQfa5TIBinWSMN1wlD4t/imJHul+9FUOCMZtFNdBlCigFKvJCPe/rYAv1e8+iytOJbPptIRZbo/aqNlfZWbOnE9kpvFipykmsxtPNlM3oF+nHPvkV6LAKB1QgmHKkgjXQiGovNYkdZRkX44aXO4d4aKuchMPRWvneoTsdfPSO82ac5b5CywElrCoqOVVrIBfY2eVP1JQbnpUCAwEAAQ=="

    @JvmStatic
    fun getKeyAES(): SecretKey? {
        return kotlin.runCatching {
            val generator = KeyGenerator.getInstance("AES")
            generator.init(128)
            return generator.generateKey()
        }.getOrElse {
            it.printStackTrace()
            null
        }
    }

    private fun getPublicKey(key: String): PublicKey? {
        return kotlin.runCatching {
            val keySpec = X509EncodedKeySpec(BaseEncoding.base64().decode(key))
            val keyFactory: KeyFactory = KeyFactory.getInstance("RSA")
            return keyFactory.generatePublic(keySpec)
        }.getOrElse {
            it.printStackTrace()
            null
        }
    }

    @SuppressLint("GetInstance")
    @JvmStatic
    fun encryptHook(data: String, key: String): Pair<String, String> {
        val secKey = getKeyAES()

        //2. Encrypt plain text using AES
        val aesCipher = Cipher.getInstance("AES")
        aesCipher.init(Cipher.ENCRYPT_MODE, secKey)
        val byteCipherText = aesCipher.doFinal(data.toByteArray())

        //3. Encrypt the key using RSA public key
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.PUBLIC_KEY, getPublicKey(key))
        val encryptedKey = cipher.doFinal(secKey!!.encoded)

        return Pair(
            BaseEncoding.base64().encode(byteCipherText),
            BaseEncoding.base64().encode(encryptedKey)
        )
    }

    @SuppressLint("GetInstance")
    fun encryptStripe(key: String?, data: String?): String? {
        return kotlin.runCatching {
            val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            cipher.init(Cipher.PUBLIC_KEY, getPublicKey(key ?: ""))
            val byteCipherText = cipher.doFinal(data?.toByteArray())
            return BaseEncoding.base64().encode(byteCipherText)
        }.getOrElse {
            it.printStackTrace()
            null
        }
    }

    @SuppressLint("GetInstance")
    fun decryptPublicKey(cipherText: String?, secretKey: String?): String? {
        return kotlin.runCatching {
            val skeySpec = SecretKeySpec(secretKey?.toByteArray(), "AES")
            val cipher = Cipher.getInstance("AES")
            cipher.init(Cipher.DECRYPT_MODE, skeySpec)
            val decryptedBytes = cipher.doFinal(BaseEncoding.base64().decode(cipherText ?: ""))
            return String(decryptedBytes)
        }.getOrElse {
            it.printStackTrace()
            null
        }
    }
}