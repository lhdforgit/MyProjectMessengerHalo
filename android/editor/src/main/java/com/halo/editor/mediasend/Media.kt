/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.editor.mediasend

import android.net.Uri
import android.os.Parcelable
import com.google.common.base.Optional
import kotlinx.android.parcel.Parcelize

/**
 * Represents a piece of media that the user has on their device.
 */
@Parcelize
class Media(
    val id: String? = "",
    val uri: Uri?,
    val mimeType: String?,
    val date: Long,
    val width: Int,
    val height: Int,
    val size: Long,
    val duration: Long = 0,
    var bucketId: Optional<String>,
    var caption: String?,
    var borderless: Boolean = false
) : Parcelable {

    constructor(
        uri: Uri?,
        mimeType: String?,
        width: Int,
        height: Int,
        size: Long
    ) : this(
        null,
        uri,
        mimeType,
        0,
        width,
        height,
        size,
        0,
        Optional.absent(),
        ""
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val media = other as Media
        return uri == media.uri
    }

    override fun hashCode(): Int {
        return uri.hashCode()
    }

    override fun toString(): String {
        return "Media(uri=$uri, " +
                "mimeType=$mimeType," +
                " date=$date," +
                " width=$width, " +
                "height=$height," +
                " size=$size," +
                " duration=$duration," +
                " bucketId=$bucketId"
    }

    companion object {
        const val ALL_MEDIA_BUCKET_ID = "com.hahalolo.android.social.ALL_MEDIA"

        @JvmStatic
        fun createImage(
            id: String?,
            uri: Uri?,
            mimeType: String?,
            date: Long,
            width: Int,
            height: Int,
            size: Long,
            bucketId: Optional<String>,
            caption: String?
        ): Media {
            return Media(
                id = id,
                uri = uri,
                mimeType = mimeType,
                date = date,
                width = width,
                height = height,
                size = size,
                duration = -1,
                bucketId = bucketId,
                caption = caption
            )
        }

        @JvmStatic
        fun createVideo(
            id: String?,
            uri: Uri?,
            mimeType: String?,
            date: Long,
            width: Int,
            height: Int,
            size: Long,
            duration: Long,
            bucketId: Optional<String>,
            caption: String?
        ): Media {
            return Media(
                id = id,
                uri = uri,
                mimeType = mimeType,
                date = date,
                width = width,
                height = height,
                size = size,
                duration = duration,
                bucketId = bucketId,
                caption = caption
            )
        }
    }
}