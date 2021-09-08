package com.halo.data.entities.attachment

import android.text.TextUtils
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.halo.data.room.type.AttachmentType

class Attachment {
    @SerializedName("url")
    @Expose
    var url: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("mimetype")
    @Expose
    var mimetype: String? = null

    @SerializedName("size")
    @Expose
    var size = 0

    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("fileUrl")
    @Expose
    var fileUrl: String? = null

    @SerializedName("originalname")
    @Expose
    var originalname: String? = null

    @SerializedName("width")
    @Expose
    var width: Any? = null

    @SerializedName("height")
    @Expose
    var height: Any? = null

    @SerializedName("encoding")
    @Expose
    var encoding: Any? = null

    @SerializedName("metadata")
    @Expose
    var metadata: Any? = null

    @SerializedName("createdAt")
    @Expose
    private val createdAt: Long? = null

    @SerializedName("messageId")
    @Expose
    val messageId: String? = null

    override fun toString(): String {
        return "Attachment{" +
                "fileUrl='" + fileUrl + '\'' +
                ", originalname='" + originalname + '\'' +
                ", size=" + size +
                ", width=" + width +
                ", height=" + height +
                ", mimetype='" + mimetype + '\'' +
                ", encoding=" + encoding +
                ", metadata=" + metadata +
                ", type='" + type + '\'' +
                '}'
    }

    fun isVideo(): Boolean {
        return TextUtils.equals(type, AttachmentType.VIDEO)
    }

    fun joinCode(): String? {
        return url?.takeIf { it.isNotEmpty() }?.runCatching {
            this.substring(this.lastIndexOf("/i/") + 3)
        }?.getOrNull()
    }
}