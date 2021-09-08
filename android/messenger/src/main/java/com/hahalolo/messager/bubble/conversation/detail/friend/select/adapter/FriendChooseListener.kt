/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.bubble.conversation.detail.friend.select.adapter


interface FriendChooseListener {
    fun onRemoveFriendSelected(position: Int, data: FriendSelectData)
    fun onCreateGroup(groupName : String)
}