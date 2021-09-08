package com.hahalolo.messager.presentation.main.contacts.adapter.state

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.hahalolo.messager.presentation.main.contacts.adapter.ContactStateListener
import com.hahalolo.messenger.databinding.ChatContactStateItemBinding

class ContactStateAdapter(private val listener: ContactStateListener?) :
    LoadStateAdapter<ContactStateViewHolder>() {
    override fun onBindViewHolder(holder: ContactStateViewHolder, loadState: LoadState) {
        holder.onBind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): ContactStateViewHolder {
        return ContactStateViewHolder(
            ChatContactStateItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            listener)
    }
}