/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.halo.data.room.entity.MemberEntity
import com.halo.data.room.table.MemberTable
import com.halo.data.room.type.MemberStatusType

@Dao
abstract class MemberDao {

    /*INSERT*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(word: MemberTable)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertMembers(list: MutableList<MemberTable>)

    @Query("SELECT * FROM MemberTable M WHERE M.channelId =:channelId AND M.userId IN (:userIds)")
    abstract fun getMemberList(channelId: String, userIds: MutableList<String>): LiveData<MutableList<MemberEntity>>

//    @Query("SELECT * FROM MemberTable M, UserTable U WHERE M.channelId=:channelId " +
//            "AND M.userId !=:ownerId " +
//            "AND M.status ='${MemberStatusType.ACTIVE}' " +
//            "AND (M.nickName LIKE :query " +
//                "OR M.nickNameSearch LIKE :query " +
//                "OR (M.userId = U.userId " +
//                    "AND (U.userName LIKE :query " +
//                        "OR U.userNameSearch LIKE :query)" +
//                    ")" +
//            ")")
    @Query("SELECT * FROM MemberTable M, UserTable U WHERE M.channelId=:channelId AND M.userId !=:ownerId AND M.status ='${MemberStatusType.ACTIVE}' AND M.userId = U.userId AND (U.userName LIKE :query OR U.userNameSearch LIKE :query OR M.nickName LIKE :query OR M.nickNameSearch LIKE :query) "
    )
    abstract fun mentionQuery(channelId: String, ownerId: String, query: String): LiveData<MutableList<MemberEntity>>

}