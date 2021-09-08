package com.halo.widget.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class StickerModel {
    @SerializedName("collectionId")
    @Expose
    var collectionId: String? = null

    @SerializedName("stickerId")
    @Expose
    var stickerId: String? = null

    @SerializedName("basePath")
    @Expose
    var basePath: String? = null

    @SerializedName("createdAt")
    @Expose
    var createdAt: String? = null

    @SerializedName("deletedAt")
    @Expose
    var deletedAt: Any? = null

    @SerializedName("dims")
    @Expose
    var dims: Any? = null

    @SerializedName("fileName")
    @Expose
    var fileName: String? = null

    @SerializedName("mimeType")
    @Expose
    var mimeType: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("params")
    @Expose
    var params: Any? = null

    @SerializedName("preview")
    @Expose
    var preview: Any? = null

    @SerializedName("send")
    @Expose
    var send: String? = null

    @SerializedName("size")
    @Expose
    var size: Int? = null

    @SerializedName("tags")
    @Expose
    var tags: String? = null

    @SerializedName("updatedAt")
    @Expose
    var updatedAt: String? = null

    @SerializedName("location")
    @Expose
    var location: String? = null
}