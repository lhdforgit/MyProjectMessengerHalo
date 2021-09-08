/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.room.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.halo.data.common.resource.Resource
import com.halo.data.common.utils.Strings
import com.halo.data.entities.reaction.Reactions
import com.halo.data.room.entity.MessageEntity
import com.halo.data.room.table.MessageTable
import com.halo.data.room.type.MessageStatus
import com.halo.data.room.type.SaveType

@Dao
abstract class MessageDao {

    @Transaction
    @Query("SELECT * from MessageTable M WHERE M.workspaceId =:workspaceId AND M.channelId= :channelId AND M.msgSave !='${SaveType.CACHE}' ORDER BY CASE WHEN M.msgStatus='${MessageStatus.SENDED}' THEN 1 WHEN M.msgStatus='${MessageStatus.SENDDING}' THEN 2 WHEN M.msgStatus='${MessageStatus.ERROR}' THEN 2 WHEN M.msgStatus='${MessageStatus.TYPING}' THEN 3 END DESC, M.msgTime DESC ")
    abstract fun messageList(
        workspaceId: String,
        channelId: String
    ): LiveData<MutableList<MessageEntity>>

    @Transaction
    @Query("SELECT M.msgBubbleSave from MessageTable M WHERE M.msgId =:messageId")
    abstract fun bubbleSaveType(messageId: String): String?

    @Transaction
    @Query("SELECT M.msgSave from MessageTable M WHERE M.msgId =:messageId")
    abstract fun saveType(messageId: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertMessage(messageTable: MessageTable)

    @Query("DELETE FROM MessageTable WHERE clientId=:clientId")
    abstract fun deleteMessageCache(clientId:String )


    @Query("DELETE FROM MessageTable WHERE clientId IN (:clientIds)")
    abstract fun deleteMessageCache(clientIds :MutableList<String> )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertMessageTables(list: List<MessageTable>)

    @Transaction
    open fun insertMessages(channelId: String, list: MutableList<MessageTable>) {
        updateDataCacheType(channelId)
        insertMessageTables(list)
    }

    /*UPDATE */
    @Query("UPDATE MessageTable SET msgStatus = '${MessageStatus.ERROR}' WHERE clientId =:clientId")
    abstract fun sendMessageError(clientId: String)

    @Query("UPDATE MessageTable SET msgSave = '${SaveType.CACHE}' WHERE channelId =:channelId AND msgSave ='${SaveType.OLD}'")
    abstract fun updateDataCacheType(channelId: String)

    @Query("UPDATE MessageTable SET msgSave = '${SaveType.OLD}' WHERE channelId =:channelId AND msgId  IN (SELECT M.msgId FROM MessageTable M WHERE M.channelId =:channelId ORDER BY M.msgTime DESC LIMIT :limit OFFSET :offSet)")
    abstract fun updateDataOldType(channelId: String, limit: Int, offSet: Int = 0)

    @Query("UPDATE MessageTable SET msgSave = '${SaveType.CACHE}' WHERE channelId =:channelId AND msgId NOT IN (SELECT M.msgId FROM MessageTable M WHERE M.channelId =:channelId ORDER BY M.msgTime DESC LIMIT :limit )")
    abstract fun updateDataCacheType(channelId: String, limit: Int)

    @Transaction
    @Query("UPDATE MessageTable SET msgSave = '${SaveType.OLD}' WHERE channelId = :channelId")
    open fun clearCache(channelId: String, limit: Int) {
        updateDataOldType(channelId, limit)
        updateDataCacheType(channelId, limit)
    }

    @Query("UPDATE MessageTable SET msgReaction = :msgReaction WHERE msgId = :messageId")
    abstract fun updateMessageReaction(messageId: String, msgReaction: String)

    @Query("SELECT MAX(M.msgTime) FROM MessageTable M WHERE channelId = :channelId")
    abstract fun lastTime(channelId: String?): Long?


    @Query("SELECT * FROM MessageTable M WHERE msgId = :messageId")
    abstract fun getMessage(messageId: String?): MessageTable?


//
//    @Query("UPDATE MessageTable SET msgStatus = :status  WHERE msgId =:msgId")
//    abstract fun updateMessageStatus(msgId: String, status: Int)
//
//    @Transaction
//    @Query("SELECT * from MessageTable M WHERE M.roomId= :roomId  ORDER BY  msgTime ASC")
//    abstract fun getListMessageOfRoom(roomId: String): LiveData<List<MessageEntity>>
//
//    @Transaction
//    @Query("SELECT * FROM MessageTable M WHERE M.roomId= :roomId  ORDER BY  msgTime DESC")
//    abstract fun getListMessageSource(roomId: String): DataSource.Factory<Int, MessageEntity>
//
//    @Query("SELECT * from MessageTable R WHERE R.msgId =:msgId")
//    abstract fun getMessageWithId(msgId: String): MessageTable?
//
//    @Query("SELECT * from MessageTable R WHERE R.msgId =:msgId")
//    abstract fun getMessageEntityWithId(msgId: String): MessageEntity?
//
//    @Query("SELECT * from MessageTable R WHERE R.msgTime =:msgTime")
//    abstract fun getMessageWithTime(msgTime: Long): MessageTable?
//
//    //delete
//    @Query("DELETE FROM MessageTable  WHERE msgId = :id")
//    abstract fun deleteId(id: String)
//
//    @Query("DELETE FROM MessageTable  WHERE roomId = :roomId")
//    abstract fun deleteMsgInRoom(roomId: String)
//
//    @Query("DELETE FROM MessageTable WHERE roomId = :roomId AND msgId NOT IN (SELECT M.msgId from MessageTable M WHERE M.roomId= :roomId  ORDER BY  msgTime DESC limit :range) ")
//    abstract fun deleteRange(roomId: String, range: Int)
//
//    @Query("DELETE FROM MessageTable")
//    abstract fun deleteAll()
//    //end delete
//
//    /*UPDATE */
//    @Query("UPDATE MessageTable SET msgSave = '${SaveType.OLD}' WHERE roomId =:roomId AND msgId  IN (SELECT M.msgId FROM MessageTable M WHERE M.roomId =:roomId ORDER BY M.msgTime DESC LIMIT :limit OFFSET :offSet)")
//    abstract fun updateDataOldType(roomId: String, limit: Int, offSet:Int = 0)
//
//    @Query("UPDATE MessageTable SET msgSave = '${SaveType.CACHE}' WHERE roomId =:roomId AND msgId NOT IN (SELECT M.msgId FROM MessageTable M WHERE M.roomId =:roomId ORDER BY M.msgTime DESC LIMIT :limit )")
//    abstract fun updateDataCacheType(roomId: String, limit: Int)
//
//    @Query("UPDATE MessageTable SET msgBubbleSave = '${SaveType.OLD}' WHERE roomId =:roomId AND msgId  IN (SELECT M.msgId FROM MessageTable M WHERE M.roomId =:roomId ORDER BY M.msgTime DESC LIMIT :limit OFFSET :offSet)")
//    abstract fun updateBubbleDataOldType(roomId: String, limit: Int, offSet:Int = 0)
//
//    @Query("UPDATE MessageTable SET msgBubbleSave = '${SaveType.CACHE}' WHERE roomId =:roomId AND msgId NOT IN (SELECT M.msgId FROM MessageTable M WHERE M.roomId =:roomId ORDER BY M.msgTime DESC LIMIT :limit )")
//    abstract fun updateBubbleDataCacheType(roomId: String, limit: Int)
//
//    @Transaction
//    open fun clearCache(roomId: String, isBubble:Boolean, limit: Int) {
//        if (isBubble){
//            updateBubbleDataOldType(roomId, limit)
//            updateBubbleDataCacheType(roomId, limit)
//        }else{
//            updateDataOldType(roomId, limit)
//            updateDataCacheType(roomId, limit)
//        }
//    }
//
//    //set những tin nhắn sau latest saveType = OLD for Bubble
//    @Transaction
//    @Query("UPDATE MessageTable SET msgBubbleSave = '${SaveType.OLD}' WHERE roomId =:roomId AND msgId  IN (SELECT M.msgId FROM MessageTable M WHERE M.roomId =:roomId AND M.msgTime <:latest ORDER BY M.msgTime DESC LIMIT :limit )")
//    abstract fun updateBubbleDataOldType(roomId: String, latest: Long, limit: Int)
//
//    @Query("UPDATE MessageTable SET msgBubbleSave = '${SaveType.OLD}' WHERE roomId =:roomId AND msgBubbleSave = '${SaveType.NEW}'")
//    abstract fun updateBubbleDataOldType(roomId: String)
//
//    //xóa hết 20 tin nhắn cuối
//    @Query("DELETE FROM MessageTable WHERE roomId =:roomId  AND msgId NOT IN (SELECT M.msgId FROM MessageTable M WHERE M.roomId =:roomId ORDER BY M.msgTime DESC LIMIT :limit)")
//    abstract fun clearRangeMessage(roomId: String, limit: Int)
//
//    //todo update getlist mesage V4
//    @Transaction
//    @Query("SELECT * from MessageTable M WHERE M.roomId= :roomId AND M.msgSave !='${SaveType.CACHE}' ORDER BY M.msgTime DESC ")
//    abstract fun getListMessageOfRoomSourceV4(roomId: String): LiveData<MutableList<MessageEntity>>
//
//    //todo update getlist mesage V4 for Bubble
//    @Transaction
//    @Query("SELECT * from MessageTable M WHERE M.roomId= :roomId AND M.msgBubbleSave !='${SaveType.CACHE}' ORDER BY M.msgTime DESC ")
//    abstract fun getBubbleListMessageOfRoomSourceV4(roomId: String): LiveData<MutableList<MessageEntity>>
}
