package com.halo.data.entities.contact

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ContactDetail {
    @SerializedName("profileId")
    @Expose
    var profileId: String? = null

    @SerializedName("userId")
    @Expose
    val userId: String? = null

    @SerializedName("provider")
    @Expose
    val provider: String? = null

    @SerializedName("providerId")
    @Expose
    val providerId: String? = null

    @SerializedName("firstName")
    @Expose
    val firstName: String? = null

    @SerializedName("lastName")
    @Expose
    val lastName: String? = null

    @SerializedName("fullName")
    @Expose
    val fullName: String? = null

    @SerializedName("avatar")
    @Expose
    val avatar: String? = null

    @SerializedName("cover")
    @Expose
    val cover: String? = null

    @SerializedName("birthday")
    @Expose
    val birthday: String? = null

    @SerializedName("gender")
    @Expose
    val gender: String? = null

    @SerializedName("address")
    @Expose

    val address: String? = null

    @SerializedName("city")
    @Expose
    val city: Any? = null

    @SerializedName("presence")
    @Expose
    private val presence: Any? = null

    override fun toString(): String {
        return "ContactDetail(profileId=$profileId, userId=$userId, provider=$provider, providerId=$providerId, firstName=$firstName, lastName=$lastName, fullName=$fullName, avatar=$avatar, cover=$cover, birthday=$birthday, gender=$gender, address=$address, city=$city, presence=$presence)"
    }

}