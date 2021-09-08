/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.bubble.conversation.home.adapter

import androidx.recyclerview.widget.RecyclerView
import com.hahalolo.messenger.databinding.MessageSearchItemBinding
import com.halo.common.utils.ktx.executeAfter

class SearchViewHolder(
    val binding: MessageSearchItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind() {
        binding.executeAfter {
        }
    }
}