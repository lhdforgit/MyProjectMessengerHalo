package com.halo.widget.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class StickerCollection {
    @SerializedName("userId")
    @Expose
    var userId: String? = null

    @SerializedName("collectionId")
    @Expose
    var collectionId: String? = null

    @SerializedName("collectionName")
    @Expose
    var collectionName: String? = null

    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("fileName")
    @Expose
    var fileName: String? = null

    @SerializedName("mimeType")
    @Expose
    var mimeType: String? = null

    @SerializedName("basePath")
    @Expose
    var basePath: String? = null

    @SerializedName("downloads")
    @Expose
    var downloads: String? = null

    @SerializedName("views")
    @Expose
    var views: String? = null

    @SerializedName("createdAt")
    @Expose
    var createdAt: String? = null

    @SerializedName("updatedAt")
    @Expose
    var updatedAt: String? = null

    @SerializedName("location")
    @Expose
    var location: String? = null
}