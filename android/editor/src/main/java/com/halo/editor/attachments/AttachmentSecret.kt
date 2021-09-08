/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.editor.attachments

import com.google.gson.Gson

/**
 * Encapsulates the key material used to encrypt attachments on disk.
 *
 * There are two logical pieces of material, a deprecated set of keys used to encrypt
 * legacy attachments, and a key that is used to encrypt attachments going forward.
 */
class AttachmentSecret(
    var classicCipherKey: ByteArray?,
    var classicMacKey: ByteArray?,
    var modernKey: ByteArray?
) {

    fun serialize(): String {
        return try {
            Gson().toJson(this)
        } catch (e: Exception) {
            throw AssertionError(e)
        }
    }

    companion object {
        @JvmStatic
        fun fromString(value: String): AttachmentSecret {
            return try {
                Gson().fromJson(value, AttachmentSecret::class.java)
            } catch (e: Exception) {
                throw AssertionError(e)
            }
        }
    }
}