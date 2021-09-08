/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.room.table

import android.text.TextUtils
import android.webkit.URLUtil
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.halo.data.room.type.AttachmentType
import com.halo.data.room.type.DocumentType

@Entity
class AttachmentTable(
    @PrimaryKey
    @ColumnInfo(name = "atmId")
    val id: String,
    @ColumnInfo(name = "msgId")
    var msgId: String? = null,
    @ColumnInfo(name = "roomId")
    var roomId: String? = null
) {

    @ColumnInfo(name = "atmLocalUri")
    var localUri: String? = null

    @ColumnInfo(name = "atmType")
    var type: String? = null

    @ColumnInfo(name = "atmUrl")
    var url: String? = null

    @ColumnInfo(name = "atmSentAt")
    var sentAt: Long? = null

    @ColumnInfo(name = "atmFileMimeType")
    var fileMimeType: String? = null

    @ColumnInfo(name = "atmFileName")
    var fileName: String? = null

    @ColumnInfo(name = "atmFileType")
    var fileType: String? = null

    @ColumnInfo(name = "atmFileSize")
    var fileSize: Int = 0

    @ColumnInfo(name = "atmFileUrl")
    var fileUrl: String? = null

    @ColumnInfo(name = "atmThumbnail")
    var thumbnail: String? = null

    @ColumnInfo(name = "atmWidth")
    var width: Int = 0

    @ColumnInfo(name = "atmHeight")
    var height: Int = 0

    @ColumnInfo(name = "atmSave")
    var atmSave: String? = null

    fun isUploaded(): Boolean {
        return !fileUrl.isNullOrEmpty() && URLUtil.isValidUrl(fileUrl)
    }

    fun getAttachmentUrl(): String {
        if (isUploaded()) {
            return fileUrl ?: ""
        } else {
            return localUri ?: ""
        }
    }

    fun getStickerUrl(): String {
        return fileUrl?.takeIf { it.isNotEmpty() && URLUtil.isValidUrl(fileUrl) } ?: (url ?: "")
    }

    fun isDoccument(): Boolean {
        return TextUtils.equals(type, AttachmentType.DOCUMENT)
    }

    fun isVideo(): Boolean {
        return (fileMimeType?.contains("video") == true) || (fileUrl?.contains(".mp4") == true)
    }

    override fun toString(): String {
        return "AttachmentTable(id='$id', msgId='$msgId', fileMimeType=$fileMimeType, fileName=$fileName, fileType=$fileType, fileSize=$fileSize, fileUrl=$fileUrl, thumbnail=$thumbnail, width=$width, height=$height, localUri=$localUri, type=$type, url=$url, sentAt=$sentAt)"
    }

    fun getFileSizeText(): String {
        if (fileSize < 1024) return "$fileSize Byte"
        if (fileSize < 1024 * 1024) return (fileSize / 1024).toString() + " KB"
        return if (fileSize < 1024 * 1024 * 1024) (fileSize / (1024 * 1024)).toString() + " MB" else (fileSize / (1024 * 1024 * 1024)).toString() + " GB"
    }

    fun getDocumentType(): String {
        fileMimeType?.takeIf { it.isNotEmpty() }?.let { type ->
            when {
                type.contains("/pdf") -> return DocumentType.PDF
                type.contains("/doc") -> return DocumentType.DOC
                type.contains("/xlsx") || type.contains(".sheet") -> return DocumentType.EXCEl
                else -> DocumentType.OTHER
            }
        }
        return DocumentType.OTHER
    }

    fun isGiphy(): Boolean {
        return url?.contains("giphy.com") ?: false
    }
}