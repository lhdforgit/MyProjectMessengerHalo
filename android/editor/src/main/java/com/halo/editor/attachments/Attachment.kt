/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.editor.attachments

import android.net.Uri

abstract class Attachment(
    val contentType: String?,
    val transferState: Int,
    val size: Long,
    val fileName: String?,
    val width: Int,
    val height: Int,
    val fastPreflightId: String?,
    var caption: String?
) {
    abstract val dataUri: Uri?
    abstract val thumbnailUri: Uri?

    override fun toString(): String {
        return "Attachment(contentType='$contentType', transferState=$transferState," +
                " size=$size, fileName=$fileName," +
                " width=$width, height=$height, fastPreflightId=$fastPreflightId, " +
                "dataUri=$dataUri, thumbnailUri=$thumbnailUri)"
    }
}