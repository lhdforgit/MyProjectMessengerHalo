/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.messager.chatkit.adapter

import android.view.View
import androidx.lifecycle.Observer
import com.halo.data.entities.invite.InviteInfo
import com.halo.data.room.entity.MemberEntity
import com.halo.data.room.entity.MessageEntity
import com.halo.data.room.entity.ChannelEntity
import com.halo.data.room.table.AttachmentTable
import com.halo.data.room.table.MentionTable

interface ChatListener {

    fun onReactionMessage(messageId: String?, type: String)
    fun onDeleteReaction(messageId: String, emoji: String)

    fun onAddMemberInGroup()

    fun onChangeGroupName()

    fun onChangeGroupAvatar()

    fun onChangeNickName()

    fun onImageClickListener(listImage: MutableList<AttachmentTable>?, position: Int)

    fun onAvatarClick(view: View?, messageEntity: MessageEntity?)

    fun getLanguageCode(): String

    fun ownerId(): String

    fun onDeleteMessage(message: MessageEntity?)

    fun onRevokeMessage(message: MessageEntity?)

    fun onQuoteMessage(message: MessageEntity?)

    fun onEditMessage(message: MessageEntity?)

    fun onDownloadMedia(message: MessageEntity?)

    fun onRetrySendMessage(message: MessageEntity?)

    fun onCancelMessageError(message: MessageEntity?)

    fun onForwardMessage(message: MessageEntity?)

    fun onShowMenuAction()

    fun detailReactionOfMsg(message: MessageEntity?)


    //for item create
    fun observerRoomEntity(observer: Observer<ChannelEntity>)

    fun removeObserverRoomEntity(observer: Observer<ChannelEntity>)

    fun onClickMention(mention: MentionTable)

    fun onClickReaderDetail(readers: MutableList<MemberEntity>?)

    fun onRemoveMessageError(messageEntity: MessageEntity)

    fun onInvitedChannel(data: InviteInfo)
    fun openWebUrl(url : String) {


    }
}