package com.hahalolo.messager.bubble.conversation.dialog

interface DialogMenuListener {
    fun onClickMemberList()
    fun onClickAddMember()
    fun onClickDeleteConversation()
    fun onClickLeaveGroup()
    fun onClickNotification()
    fun isEnableNotify() : Boolean
    fun isGroup() : Boolean
}