package com.halo.data.room.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Relation
import com.halo.data.room.table.MemberTable
import com.halo.data.room.table.MentionTable

class MentionEntity (
    @Embedded
    private var mentionTable: MentionTable,

    @Relation(parentColumn = "userInRoomId", entityColumn = "userInRoomId", entity = MemberTable::class)
    var memberEntity: MemberEntity? = null,
    ){


}