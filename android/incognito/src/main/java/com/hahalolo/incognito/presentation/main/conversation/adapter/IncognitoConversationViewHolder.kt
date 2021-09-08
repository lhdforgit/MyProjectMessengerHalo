package com.hahalolo.incognito.presentation.main.conversation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoConversationItemBinding

class IncognitoConversationViewHolder(
    private val binding: IncognitoConversationItemBinding,
    private val listener: IncognitoConversationListener
) : RecyclerView.ViewHolder(binding.root) {

    fun onBind() {
        binding.root.setOnClickListener {
            listener.onClick()
        }
    }

    companion object {
        fun create(
            parent: ViewGroup,
            listener: IncognitoConversationListener
        ): IncognitoConversationViewHolder {
            val binding = DataBindingUtil.inflate<IncognitoConversationItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.incognito_conversation_item,
                parent,
                false
            )
            return IncognitoConversationViewHolder(binding, listener)
        }
    }
}