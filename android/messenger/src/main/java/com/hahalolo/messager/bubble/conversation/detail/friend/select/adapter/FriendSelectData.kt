/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.bubble.conversation.detail.friend.select.adapter

import android.os.Parcelable
import android.text.TextUtils
import com.halo.data.entities.contact.Contact
import com.halo.data.entities.user.User
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FriendSelectData(
    var userId: String? = "",
    var fullName: String? = "",
    var avatar: String? = ""

) : Parcelable {
    fun areItemsTheSame(t1: FriendSelectData?): Boolean {
        return TextUtils.equals(userId, t1?.userId)
    }

    fun areContentsTheSame(t1: FriendSelectData?): Boolean {
        return TextUtils.equals(userId, t1?.userId) && TextUtils.equals(fullName, t1?.fullName)
    }

    companion object {

        fun transformFriendSelectData(data: Contact?): FriendSelectData {
            val result = FriendSelectData()
            data?.run {
                result.userId = userId
                result.avatar = avatar
                result.fullName = "$firstName $lastName"
            }
            return result
        }

        fun transformFriendSelectData(data: User?): FriendSelectData {
            val result = FriendSelectData()
            data?.run {
                result.userId = userId
                result.avatar = avatar
                result.fullName = userName()
            }
            return result
        }
    }
}