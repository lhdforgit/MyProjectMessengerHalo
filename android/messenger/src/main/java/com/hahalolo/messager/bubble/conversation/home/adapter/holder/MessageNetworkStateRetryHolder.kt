/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.bubble.conversation.home.adapter.holder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.hahalolo.messager.presentation.adapter.holder.NetworkStateListener
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.MessageNetworkStateRetryItemBinding

import com.halo.data.common.paging.NetworkState

/**
 * @author ndn
 * Created by ndn
 * Created on 6/20/18.
 */
class MessageNetworkStateRetryHolder private constructor(
    private val binding: MessageNetworkStateRetryItemBinding,
    private val listener: NetworkStateListener?
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(networkState: NetworkState?) {
        invalidateNetwork()
        bindActions()
        if (networkState != null) {
            binding.status = networkState.status
            binding.msg = networkState.message
        }
        binding.executePendingBindings()
    }

    private fun bindActions() {
        binding.retryButton.setOnClickListener {
            listener?.retryCallback()
        }
    }

    private fun invalidateNetwork() {
        binding.status = NetworkState.Status.SUCCESS
        binding.msg = ""
    }

    companion object {

        fun createHolder(
            parent: ViewGroup,
            listener: NetworkStateListener?
        ): MessageNetworkStateRetryHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding =
                DataBindingUtil.inflate<MessageNetworkStateRetryItemBinding>(
                    layoutInflater,
                    R.layout.message_network_state_retry_item, parent, false
                )
            return MessageNetworkStateRetryHolder(
                binding,
                listener
            )
        }
    }
}
