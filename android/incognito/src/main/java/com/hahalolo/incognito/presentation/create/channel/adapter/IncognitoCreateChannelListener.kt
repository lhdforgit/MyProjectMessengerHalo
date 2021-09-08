package com.hahalolo.incognito.presentation.create.channel.adapter

interface IncognitoCreateChannelListener {
    fun onSearch(query: String)
    fun onSelect(targetId: String)
    fun onUnSelect(targetId: String)
    fun isSelected(targetId: String): Boolean
}