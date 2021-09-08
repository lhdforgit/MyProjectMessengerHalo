package com.hahalolo.messager.bubble.conversation.detail.adapter

import com.halo.data.room.table.AttachmentTable

interface ConversationDetailMediaListener {
    fun onLastItemClick()
    fun onItemClick(position : Int, entity: AttachmentTable?)
}