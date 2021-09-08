package com.hahalolo.messager.presentation.main.dialog

interface MenuConversationListener {
    fun onNotification()
    fun onMemberDetail()
    fun onAddMember()
    fun onLeaveGroup()
    fun onRemoveConversation()
    fun isEnableNotify() : Boolean
    fun isGroup() : Boolean
}