/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.editor.slide

import android.content.Context
import android.content.res.Resources
import android.net.Uri
import androidx.annotation.DrawableRes
import com.google.common.base.Optional
import com.halo.editor.attachments.Attachment
import com.halo.editor.attachments.UriAttachment
import com.halo.editor.mediasend.Media
import com.halo.editor.util.MediaUtil
import com.halo.editor.util.Util
import java.security.SecureRandom

abstract class Slide(
    protected val context: Context,
    protected val attachment: Attachment
) {
    val contentType: String?
        get() = attachment.contentType

    val uri: Uri?
        get() = attachment.dataUri

    open val thumbnailUri: Uri?
        get() = attachment.thumbnailUri

    val fileName: Optional<String>
        get() = Optional.fromNullable(attachment.fileName)

    val fileSize: Long
        get() = attachment.size

    val fastPreflightId: String?
        get() = attachment.fastPreflightId

    open fun hasImage(): Boolean {
        return false
    }

    fun hasSticker(): Boolean {
        return false
    }

    open fun hasVideo(): Boolean {
        return false
    }

    fun hasAudio(): Boolean {
        return false
    }

    fun hasDocument(): Boolean {
        return false
    }

    fun hasLocation(): Boolean {
        return false
    }

    open fun hasViewOnce(): Boolean {
        return false
    }

    open val contentDescription: String
        get() = ""

    fun asAttachment(): Attachment {
        return attachment
    }

    fun asMedia(): Media {
        return Media(
            uri = uri,
            mimeType = contentType,
            width = attachment.width,
            height = attachment.height,
            size = attachment.size
        )
    }

    val transferState: Int
        get() = attachment.transferState

    var caption: String? = null
        set(value) {
            field = value
            attachment.caption = value
        }
        get() = attachment.caption

    @DrawableRes
    open fun getPlaceholderRes(theme: Resources.Theme?): Int {
        throw AssertionError("getPlaceholderRes() called for non-drawable slide")
    }

    open fun hasPlayOverlay(): Boolean {
        return false
    }

    fun getFileType(context: Context): Optional<String> {
        val fileName = fileName
        return if (fileName.isPresent) {
            Optional.of(
                getFileType(
                    fileName
                )
            )
        } else Optional.fromNullable(
            MediaUtil.getExtension(
                context,
                uri
            )
        )
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is Slide) return false
        return Util.equals(
            contentType,
            other.contentType
        ) && hasAudio() == other.hasAudio()
                && hasImage() == other.hasImage()
                && hasVideo() == other.hasVideo()
                && transferState == other.transferState &&
                Util.equals(uri, other.uri) &&
                Util.equals(thumbnailUri, other.thumbnailUri)
    }

    override fun hashCode(): Int {
        return Util.hashCode(
            contentType, hasAudio(), hasImage(),
            hasVideo(), uri, thumbnailUri, transferState
        )
    }

    override fun toString(): String {
        return "Slide(context=$context, attachment=$attachment)"
    }

    companion object {
        @JvmStatic
        protected fun constructAttachmentFromUri(
            context: Context,
            uri: Uri,
            defaultMime: String,
            size: Long,
            width: Int,
            height: Int,
            hasThumbnail: Boolean,
            fileName: String?,
            caption: String
        ): Attachment {
            val resolvedType = Optional.fromNullable(
                MediaUtil.getMimeType(context, uri)
            ).or(defaultMime)
            val fastPreflightId = SecureRandom().nextLong().toString()
            return UriAttachment(
                uri,
                if (hasThumbnail) uri else null,
                resolvedType,
                1,
                size,
                width,
                height,
                fileName,
                fastPreflightId,
                caption
            )
        }

        private fun getFileType(fileName: Optional<String>): String {
            if (!fileName.isPresent) return ""
            val parts = fileName.get().split("\\.").toTypedArray()
            if (parts.size < 2) {
                return ""
            }
            val suffix = parts[parts.size - 1]
            return if (suffix.length <= 3) {
                suffix
            } else ""
        }
    }
}