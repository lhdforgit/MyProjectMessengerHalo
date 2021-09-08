/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.data.entities.media

import android.os.Parcelable
import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import com.halo.common.utils.ThumbImageUtils
import com.halo.common.utils.ktx.formatK
import kotlinx.android.parcel.Parcelize

/**
 * @author ndn
 * Created by ndn
 * Created on 7/2/18
 */
@Parcelize
data class MediaEntity(
    @JvmField
    @SerializedName("type")
    var type: String? = null,
    @JvmField
    @SerializedName("path")
    var path: String? = null,
    @SerializedName("when")
    var `when`: String? = null,
    @SerializedName("onid")
    var onid: String? = null,
    @SerializedName("onty")
    var onty: String? = null,
    @SerializedName("id")
    @JvmField
    var id: String? = null,
    @SerializedName("what")
    @JvmField
    var what: String? = null,
    @SerializedName("user")
    var user: String? = null,
    @SerializedName("album")
    var album: String? = null,
    @SerializedName("scope")
    var scope: String? = null,
    @SerializedName("ofn")
    var ofn: String? = null,
    @SerializedName("text")
    var text: String? = null,
    @SerializedName("size")
    var size: String? = null,
    @SerializedName("n_comms")
    var nComms: Int = 0,
    @SerializedName("onna")
    var onna: String? = null,
    @SerializedName("onav")
    var onav: String? = null,
    @SerializedName("liked")
    var liked: Int = 0,
    @SerializedName("n_likes")
    var nLikes: Int = 0,
    @SerializedName("n_share")
    var nShare: Int = 0,
    @SerializedName("thumb")
    var thumb: String? = null,
    @SerializedName("avatar")
    var avatar: String? = null,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("width")
    var width: String? = null,
    @SerializedName("height")
    var height: String? = null,
    @SerializedName("meta")
    var meta: MetaEntity? = null
) : Parcelable {

    fun getType(): String? {
        return type?.run {
            if (!TextUtils.isEmpty(path)) {
                if (ThumbImageUtils.isVideo(path)) {
                    return TypeMedia.VID
                } else if (ThumbImageUtils.isImage(path)) {
                    return TypeMedia.IMG
                }
            }
            return ""
        }
    }

    val isUploadAlbum: Boolean
        get() = TextUtils.equals(album, "root")

    val isVideo: Boolean
        get() {
            return getType() == TypeMedia.VID
        }

    fun getPath(): String {
        // Fix bug path link.
        return ThumbImageUtils.replaceDomain(path) ?: ""
    }

    fun getId(): String? {
        return if (TextUtils.isEmpty(id)) what else id
    }

    fun getnCommsStr(): String {
        return nComms.toLong().formatK()
    }

    fun getnLikesStr(): String {
        return nLikes.toLong().formatK()
    }

    fun isLiked(): Boolean {
        return liked != 0
    }
}