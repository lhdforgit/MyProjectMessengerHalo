package com.halo.data.entities.response

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class ResponsePaging<T> : Parcelable {
    @SerializedName("docs")
    @Expose
    var docs: MutableList<T>? = null

    @SerializedName("totalDocs")
    @Expose
    var totalDocs: Int? = null

    @SerializedName("hasNextPage")
    @Expose
    var hasNextPage: Boolean? = null

    @SerializedName("limit")
    @Expose
    var limit: Int? = null

   private val isHaveData: Boolean
        get() = docs?.isNotEmpty() ?: false

    val elements: MutableList<T>?
        get() = if (isHaveData) docs else mutableListOf()

    val isHasNextPage
        get() = hasNextPage ?: false

    override fun toString(): String {
        return "ResponsePaging(docs=$docs, totalDocs=$totalDocs, hasNextPage=$hasNextPage, limit=$limit)"
    }


}