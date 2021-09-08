/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.halo.data.room.entity.ChannelDetailEntity
import com.halo.data.room.entity.ChannelEntity
import com.halo.data.room.table.ChannelTable
import com.halo.data.room.type.SaveType

@Dao
abstract class ChannelDao {

    /*INSERT*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(word: ChannelTable)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertChannels(list: MutableList<ChannelTable>)

    @Query("SELECT * FROM ChannelTable R WHERE  R.channelId =:channelId")
    abstract suspend fun getChannelEntity(channelId:String ): ChannelEntity?

    @Query("SELECT * FROM ChannelTable R WHERE  R.channelId =:channelId")
    abstract fun getChannelDetailEntity(channelId: String):LiveData<ChannelDetailEntity>

    open fun insertChannels(workspaceId: String, list: MutableList<ChannelTable>) {
        updateDataCacheType(workspaceId, list.map { it.id }.toMutableList())
        insertChannels(list)
    }

    @Query("UPDATE ChannelTable SET saveType = '${SaveType.CACHE}' WHERE workspaceId =:workspaceId AND saveType = '${SaveType.OLD}' AND channelId NOT IN (:ids)")
    abstract fun updateDataCacheType(workspaceId: String, ids: MutableList<String>)
    /*INSERT END*/


    @Transaction
    @Query("SELECT * FROM ChannelTable R WHERE R.workspaceId =:workspaceId AND R.saveType != '${SaveType.CACHE}' ORDER BY time DESC ")
    abstract fun channelList(workspaceId: String): LiveData<MutableList<ChannelEntity>>


    @Query("UPDATE ChannelTable SET saveType = '${SaveType.OLD}' WHERE channelId IN (SELECT M.channelId FROM ChannelTable M ORDER BY M.time DESC LIMIT :limit )")
    abstract fun updateDataOldType(limit: Int)

    @Query("UPDATE ChannelTable SET saveType = '${SaveType.CACHE}' WHERE channelId NOT IN (SELECT M.channelId FROM ChannelTable M ORDER BY M.time DESC LIMIT :limit )")
    abstract fun updateDataCacheType(limit: Int)

    //clear cache before load data from server
    @Transaction
    open fun clearCache(workspaceId: String, limit: Int) {
        updateDataOldType(limit)
        updateDataCacheType(limit)
    }



    /*UPDATE*/
//
//    //load more cache to show when network is disconnected
//    @Transaction
//    @Query(
//        "UPDATE ChannelTable SET roomSave = '${SaveType.OLD}' " +
//                "WHERE roomId  IN (" +
//                "SELECT R.roomId FROM ChannelTable R, LastMsgTable L " +
//                "WHERE R.roomId = L.roomId " +
//                "AND R.roomId IN (" +
//                "SELECT UI.roomId FROM MemberTable UI " +
//                "WHERE UI.userId=:ownerId " +
//                "AND UI.status ='${MemberStatusType.ACTIVE}'" +
//                ") " +
//                "ORDER BY roomTime DESC LIMIT :limit OFFSET :offSet " +
//                ")"
//    )
//    abstract fun updateDataOldType4(ownerId: String, offSet: Int, limit: Int)
//
//    //bubble load more cache to show when network is disconnected
//    @Transaction
//    @Query(
//        "UPDATE ChannelTable SET roomBubbleSave = '${SaveType.OLD}' " +
//                "WHERE roomId  IN (" +
//                "SELECT R.roomId FROM ChannelTable R, LastMsgTable L " +
//                "WHERE R.roomId = L.roomId " +
//                "AND R.roomId IN (" +
//                "SELECT UI.roomId FROM MemberTable UI " +
//                "WHERE UI.userId=:ownerId " +
//                "AND UI.status ='${MemberStatusType.ACTIVE}'" +
//                ") " +
//                "ORDER BY roomTime DESC LIMIT :limit OFFSET :offSet " +
//                ")"
//    )
//    abstract fun updateBubbleDataOldType4(ownerId: String, offSet: Int, limit: Int)
//
//    //load more cache to show when network is disconnected
//    @Transaction
//    @Query(
//        "UPDATE ChannelTable SET roomSave = '${SaveType.OLD}' " +
//                "WHERE roomId  IN (" +
//                "SELECT M.roomId FROM ChannelTable M " +
//                "WHERE M.roomTime <:latest " +
//                "ORDER BY M.roomTime DESC LIMIT :limit " +
//                ")"
//    )
//    abstract fun updateDataOldType(latest: Long, limit: Int)
//
//
//    //clear cache before load data from server
//    fun clearCache(limit: Int, isBubble: Boolean) {
//        if (isBubble) {
//            updateDataBubbleOldType(limit)
//            updateDataBubbleCacheType(limit)
//        } else {
//            updateDataOldType(limit)
//            updateDataCacheType(limit)
//        }
//    }
//
//    //update v2 for paging
////    @Transaction
////    @Query(
////        "UPDATE ChannelTable SET roomSave = '${SaveType.OLD}' " +
////                "WHERE roomId  IN (" +
////                "SELECT M.roomId FROM ChannelTable M " +
////                "ORDER BY M.roomTime DESC LIMIT :limit " +
////                ")"
////    )
////    abstract fun updateDataOldType(limit: Int)
////
////    @Transaction
////    @Query(
////        "UPDATE ChannelTable SET roomSave = '${SaveType.CACHE}' " +
////                "WHERE roomId NOT IN (" +
////                "SELECT M.roomId FROM ChannelTable M " +
////                "ORDER BY M.roomTime DESC LIMIT :limit " +
////                ")"
////    )
////    abstract fun updateDataCacheType(limit: Int)
//
//    //update v2 for paging bubble
//    @Transaction
//    @Query(
//        "UPDATE ChannelTable SET roomBubbleSave = '${SaveType.OLD}' " +
//                "WHERE roomId  IN (" +
//                "SELECT M.roomId FROM ChannelTable M " +
//                "ORDER BY M.roomTime DESC LIMIT :limit " +
//                ")"
//    )
//    abstract fun updateDataBubbleOldType(limit: Int)
//
//    @Transaction
//    @Query(
//        "UPDATE ChannelTable SET roomBubbleSave = '${SaveType.CACHE}' " +
//                "WHERE roomId NOT IN (" +
//                "SELECT M.roomId FROM ChannelTable M " +
//                "ORDER BY M.roomTime DESC LIMIT :limit " +
//                ")"
//    )
//    abstract fun updateDataBubbleCacheType(limit: Int)
//    /*UPDATE END*/
//
//    /*GET INFO*/
//    @Transaction
//    @Query(
//        "SELECT * FROM ChannelTable R, MemberTable I, UserTable U" +
//                " WHERE R.roomId = :roomId " +
//                "AND R.roomId= I.roomId " +
//                "AND I.userId = U.userId " +
//                "AND U.userId = :userId " +
//                "AND I.status = '${MemberStatusType.ACTIVE}' "
//    )
//    abstract fun getRoomInfor(roomId: String, userId: String): LiveData<ChannelEntity>
//
//    @Transaction
//    @Query(
//        "SELECT * FROM ChannelTable R, MemberTable I, MemberTable O " +
//                "WHERE I.userId = :userId " +
//                "AND O.userId =:friendId  " +
//                "AND I.status = '${MemberStatusType.ACTIVE}' " +
//                "AND O.status = '${MemberStatusType.ACTIVE}' " +
//                "AND I.roomId = O.roomId " +
//                "AND I.roomId = R.roomId  " +
//                "AND R.roomType= '${ChannelType.PRIVATE}' "
//    )
//    abstract fun getPrivateRoomInfor(userId: String, friendId: String): LiveData<ChannelEntity>
//
//    @Query(
//        "SELECT * FROM ChannelTable R " +
//                "WHERE R.roomId =:roomId"
//    )
//    abstract fun getRoomWithId(roomId: String): ChannelTable?
//
//    @Query(
//        "SELECT COUNT(roomId) FROM MemberTable  " +
//                "WHERE MemberTable.userId=:ownerId " +
//                "AND newMessage > 0"
//    )
//    abstract fun getCountNewRoom(ownerId: String): LiveData<Int>
//
//    @Transaction
//    @Query(
//        "SELECT U.newMessage FROM MemberTable U " +
//                "WHERE U.roomId=:roomId " +
//                "AND U.userId=:userId "
//    )
//    abstract fun getCountMsgUnReadOfRoom(roomId: String, userId: String): LiveData<Int>
//
//    @Transaction
//    @Query(
//        "SELECT M.msgTime FROM LastSeenMessageTable U , MessageTable M " +
//                "WHERE U.roomId =:roomId " +
//                "AND U.userId=:userId " +
//                "AND U.msgId = M.msgId"
//    )
//    abstract fun lastTimeSeenMessage(roomId: String, userId: String): LiveData<Long?>
//    /*GET END*/
//
//    /*LOAD */
//    @Transaction
//    @Query(
//        "SELECT * FROM ChannelTable R, MemberTable I " +
//                "WHERE  R.roomType= '${ChannelType.GROUP}' " +
//                "AND R.roomId= I.roomId " +
//                "AND I.userId = :userId " +
//                "AND I.status ='${MemberStatusType.ACTIVE}'  " +
//                "ORDER BY roomTime DESC"
//    )
//    abstract fun getListGroups(userId: String): LiveData<MutableList<ChannelEntity>>
//
//    /*SEARCH*/
//    /*use for search with query  */
//    @Transaction
//    @Query(
//        "SELECT * FROM ChannelTable R, LastMsgTable L " +
//                "WHERE R.roomId = L.roomId " +
//                "AND R.roomType= '${ChannelType.GROUP}' " +
//                "AND R.roomId IN (" +
//                "SELECT UI.roomId FROM MemberTable UI " +
//                "WHERE UI.userId=:userId " +
//                "AND UI.status ='${MemberStatusType.ACTIVE}'" +
//                ") " +
//                "AND (R.roomName like :query " +
//                "OR R.roomNameSearch LIKE :query " +
//                "OR R.roomId IN (" +
//                "SELECT I.roomId FROM MemberTable I " +
//                "WHERE I.status = '${MemberStatusType.ACTIVE}' " +
//                "AND (I.nickName LIKE :query " +
//                "OR I.nickNameSearch LIKE :query " +
//                "OR I.userId IN (" +
//                "SELECT U.userId FROM UserTable U " +
//                "WHERE U.userFullName LIKE :query " +
//                "OR U.userNameSearch LIKE :query " +
//                ")" +
//                ")" +
//                ")" +
//                ") " +
//                "ORDER BY roomTime DESC "
//    )
//    abstract fun searchListGroups(
//        userId: String,
//        query: String
//    ): LiveData<MutableList<ChannelEntity>>
//
//    /*trả về list room(group type) DataSource */
//    @Transaction
//    @Query(
//        "SELECT * FROM ChannelTable R, MemberTable I " +
//                "WHERE  R.roomType= '${ChannelType.GROUP}' " +
//                "AND R.roomId= I.roomId " +
//                "AND I.userId = :userId " +
//                "AND I.status ='${MemberStatusType.ACTIVE}' " +
//                "ORDER BY roomTime DESC"
//    )
//    abstract fun getListGroupsSource(userId: String): DataSource.Factory<Int, ChannelEntity>
//
//    @Transaction
//    @Query(
//        "SELECT * FROM ChannelTable R, LastMsgTable L " +
//                "WHERE R.roomId = L.roomId " +
//                "AND R.roomType= '${ChannelType.GROUP}' " +
//                "AND R.roomId IN (" +
//                "SELECT UI.roomId FROM MemberTable UI " +
//                "WHERE UI.userId=:userId " +
//                "AND UI.status ='${MemberStatusType.ACTIVE}'" +
//                ") " +
//                "AND (R.roomName LIKE :query " +
//                "OR R.roomNameSearch LIKE :query " +
//                "OR R.roomId IN (" +
//                "SELECT I.roomId FROM MemberTable I " +
//                "WHERE I.status = '${MemberStatusType.ACTIVE}' " +
//                "AND (I.nickName LIKE :query " +
//                "OR I.nickNameSearch LIKE :query " +
//                "OR I.userId IN (" +
//                "SELECT U.userId FROM UserTable U " +
//                "WHERE U.userFullName LIKE :query " +
//                "OR U.userNameSearch LIKE :query " +
//                ")" +
//                ")" +
//                ")" +
//                ") " +
//                " ORDER BY roomTime DESC "
//    )
//    abstract fun getListGroupsSource(
//        userId: String,
//        query: String
//    ): DataSource.Factory<Int, ChannelEntity>
//
//    @Transaction
//    @Query(
//        "SELECT * FROM ChannelTable R, LastMsgTable L " +
//                "WHERE R.roomId = L.roomId " +
//                "AND R.roomId IN ( " +
//                "SELECT UI.roomId FROM MemberTable UI " +
//                "WHERE UI.userId=:userId " +
//                "AND UI.status ='${MemberStatusType.ACTIVE}' " +
//                ") " +
//                "AND (R.roomName LIKE :query " +
//                "OR R.roomNameSearch LIKE :query " +
//                "OR R.roomId IN ( " +
//                "SELECT I.roomId FROM MemberTable I " +
//                "WHERE I.status = '${MemberStatusType.ACTIVE}' " +
//                "AND (I.nickName LIKE :query " +
//                "OR I.nickNameSearch LIKE :query " +
//                "OR I.userId IN (" +
//                "SELECT U.userId FROM UserTable U " +
//                "WHERE U.userFullName LIKE :query " +
//                "OR U.userNameSearch LIKE :query " +
//                ") " +
//                ") " +
//                ") " +
//                ") " +
//                "ORDER BY roomTime DESC LIMIT ${10}"
//    )
//    abstract fun getListRoomsSource(
//        userId: String,
//        query: String
//    ): DataSource.Factory<Int, ChannelEntity>
//
//
//    @Transaction
//    @Query(
//        "SELECT * FROM ChannelTable R, LastMsgTable L " +
//                "WHERE R.roomId = L.roomId " +
//                "AND R.roomId IN ( " +
//                "SELECT UI.roomId FROM MemberTable UI " +
//                "WHERE UI.userId=:userId " +
//                "AND UI.status ='${MemberStatusType.ACTIVE}' " +
//                ") " +
//                "AND (R.roomName LIKE :query " +
//                "OR R.roomNameSearch LIKE :query " +
//                "OR R.roomId IN ( " +
//                "SELECT I.roomId FROM MemberTable I " +
//                "WHERE I.status = '${MemberStatusType.ACTIVE}' " +
//                "AND (I.nickName LIKE :query " +
//                "OR I.nickNameSearch LIKE :query " +
//                "OR I.userId IN (" +
//                "SELECT U.userId FROM UserTable U " +
//                "WHERE U.userFullName LIKE :query " +
//                "OR U.userNameSearch LIKE :query " +
//                ") " +
//                ") " +
//                ") " +
//                ") " +
//                "ORDER BY roomTime DESC LIMIT ${10}"
//    )
//    abstract fun getListRoomsSearch(
//        userId: String,
//        query: String
//    ): LiveData<MutableList<ChannelEntity>>
//
//
//    @Transaction
//    @Query(
//        "SELECT * FROM ChannelTable R, LastMsgTable L " +
//                "WHERE R.roomId = L.roomId " +
//                "AND R.roomId IN (" +
//                "SELECT UI.roomId FROM MemberTable UI " +
//                "WHERE UI.userId=:userId " +
//                "AND UI.status ='${MemberStatusType.ACTIVE}'" +
//                ") " +
//                "AND R.roomSave != '${SaveType.CACHE}' " +
//                "ORDER BY roomTime DESC "
//    )
//    abstract fun getListRoomsSourceV2(userId: String): DataSource.Factory<Int, ChannelEntity>
//
//    //update Paging v4
//
//    @Transaction
//    @Query(
//        "SELECT * FROM ChannelTable R, LastMsgTable L " +
//                "WHERE R.roomId = L.roomId " +
//                "AND R.roomId IN (" +
//                "SELECT UI.roomId FROM MemberTable UI " +
//                "WHERE UI.userId=:userId " +
//                "AND UI.status ='${MemberStatusType.ACTIVE}'" +
//                ") " +
//                "AND R.roomSave != '${SaveType.CACHE}' " +
//                "ORDER BY roomTime DESC "
//    )
//    abstract fun getListRoomsSourceV4(userId: String): LiveData<MutableList<ChannelEntity>>
//
//    @Transaction
//    @Query(
//        "SELECT * FROM ChannelTable R, LastMsgTable L " +
//                "WHERE R.roomId = L.roomId " +
//                "AND R.roomId IN (" +
//                "SELECT UI.roomId FROM MemberTable UI " +
//                "WHERE UI.userId=:userId " +
//                "AND UI.status ='${MemberStatusType.ACTIVE}'" +
//                ") " +
//                "AND R.roomBubbleSave != '${SaveType.CACHE}' " +
//                "ORDER BY roomTime DESC "
//    )
//    abstract fun getBubbleListRoomsSourceV4(userId: String): LiveData<MutableList<ChannelEntity>>
//    /*LOAD END*/
//
//    /*DELETE*/
//    @Query("DELETE FROM ChannelTable")
//    abstract fun deleteAll()

    /*DELETE END*/


}