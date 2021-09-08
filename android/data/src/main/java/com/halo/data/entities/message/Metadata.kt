package com.halo.data.entities.message

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Metadata {
    @SerializedName("actor")
    @Expose
    val actor: Actor? = null
    @SerializedName("verb")
    @Expose
    val verb: Verb? = null
    @SerializedName("object")
    @Expose
    val `object`: Object? = null

    class Actor {
        @SerializedName("_id")
        @Expose
        val id: String? = null

        @SerializedName("displayName")
        @Expose
        val displayName: String? = null
    }

    class Verb {
        @SerializedName("type")
        @Expose
        val type: String? = null
    }

    class Object {
        @SerializedName("_id")
        @Expose
        val id: String? = null
        @SerializedName("type")
        @Expose
        val type: String? = null
    }
}