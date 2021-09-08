/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.presentation.search.group.adapter


interface ChatSearchGroupListener {
    fun detailFriend(id: String)
    fun messageFriend(id: String)
    fun messageRoomChat(roomId: String)
}
