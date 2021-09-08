/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.data.room.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
class ParameterTable(
    @PrimaryKey
    @ColumnInfo(name = "msgId")
    var id: String
){
    @ColumnInfo(name = "parUser")
    var user: String? = null
    @ColumnInfo(name = "parName")
    var name: String? = null
    @ColumnInfo(name = "parNames")
    var names: String? = null
    @ColumnInfo(name = "parObject")
    var `object`: String? = null
    @ColumnInfo(name = "parCount")
    var count:String?= null
    @ColumnInfo(name = "parNickName")
    var nickName:String?= null
    @ColumnInfo(name = "parRole")
    var role:String?= null
    @ColumnInfo(name ="parTime")
    var time: String? = null
    @ColumnInfo(name ="parMembers")
    var members: String? = null
    @ColumnInfo(name ="parColor")
    var color: String? = null
    @ColumnInfo(name ="parUsers")
    var users: String? = null
    @ColumnInfo(name ="parNameConversation")
    var nameConversation: String? = null

    override fun toString(): String {
        return "ParameterTable(id='$id', user=$user, name=$name, names=$names, `object`=$`object`, count=$count, nickName=$nickName)"
    }
}