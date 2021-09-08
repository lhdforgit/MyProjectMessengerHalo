package com.halo.widget.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GifModel {
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("originalUrl")
    @Expose
    var originalUrl: String? = null

    @SerializedName("provider")
    @Expose
    var provider: String? = null

    @SerializedName("gif")
    @Expose
    var gif: Gif? = null

    @SerializedName("mp4")
    @Expose
    var mp4: Mp4? = null

    inner class Gif {
        @SerializedName("preview")
        @Expose
        var preview: String? = null

        @SerializedName("url")
        @Expose
        var url: String? = null

        @SerializedName("dims")
        @Expose
        var dims: MutableList<Int>? = null

        @SerializedName("size")
        @Expose
        var size: Int = 0 
    }

    inner class Mp4 {
        @SerializedName("preview")
        @Expose
        var preview: String? = null

        @SerializedName("url")
        @Expose
        var url: String? = null

        @SerializedName("dims")
        @Expose
        var dims: MutableList<Int>? = null

        @SerializedName("size")
        @Expose
        var size: Int = 0 
    }
}