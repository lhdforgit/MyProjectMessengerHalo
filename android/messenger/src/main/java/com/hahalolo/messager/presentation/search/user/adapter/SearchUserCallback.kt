package com.hahalolo.messager.presentation.search.user.adapter

interface SearchUserCallback {
    fun onSendMessage(userId: String)
    fun onViewPersonalWall(userId: String)
}