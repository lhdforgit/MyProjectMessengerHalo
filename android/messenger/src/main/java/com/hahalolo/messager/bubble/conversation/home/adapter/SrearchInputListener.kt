package com.hahalolo.messager.bubble.conversation.home.adapter
import android.widget.EditText

interface SearchInputListener {
    fun onSearch(query: String?)
    fun onCloseSearch(input: EditText)
    fun onOpenSearch(input: EditText)
}