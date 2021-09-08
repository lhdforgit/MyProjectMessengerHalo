/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.editor.attachments

import android.net.Uri

class UriAttachment(
    override val dataUri: Uri,
    override val thumbnailUri: Uri?,
    contentType: String?,
    transferState: Int,
    size: Long,
    width: Int,
    height: Int,
    fileName: String?,
    fastPreflightId: String?,
    caption: String?
) : Attachment(
    contentType,
    transferState,
    size,
    fileName,
    width,
    height,
    fastPreflightId,
    caption
) {

    override fun equals(other: Any?): Boolean {
        return other != null && other is UriAttachment && other.dataUri == dataUri
    }

    override fun hashCode(): Int {
        return dataUri.hashCode()
    }
}