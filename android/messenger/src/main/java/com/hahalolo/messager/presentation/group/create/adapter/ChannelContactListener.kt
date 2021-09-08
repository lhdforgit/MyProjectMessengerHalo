/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.presentation.group.create.adapter

import com.hahalolo.messager.bubble.conversation.detail.friend.select.adapter.FriendSelectData

interface ChannelContactListener{
    fun isChecked(userId:String ):Boolean
    fun onClickItem(data: FriendSelectData, checked: Boolean)
    fun isMember(userId: String?):Boolean
    fun onSearchFriend(query : String?)
    fun onRetryNetwork()
}