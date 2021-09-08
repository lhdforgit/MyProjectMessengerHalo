/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.room.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.halo.data.room.StringUtils


@Entity
data class UserTable constructor(
        @PrimaryKey
        @ColumnInfo(name = "userId")
        var userId: String,
        @ColumnInfo(name = "providerId")
        var providerId: String? = null,
        @ColumnInfo(name = "userName")
        var userName: String? = null
) {

    @ColumnInfo(name = "userAvatar")
    var avatar: String? = null

    @ColumnInfo(name = "userCover")
    var cover: String? = null

    @ColumnInfo(name = "userGender")
    var gender: String? = null

    @ColumnInfo(name = "userStatus")
    var status: String? = null

    @ColumnInfo(name = "userNameSearch")
    var nameSearch: String? = StringUtils.deAccent(userName)
}