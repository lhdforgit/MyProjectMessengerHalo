/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.presentation.adapter.holder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.halo.R

import com.halo.data.common.paging.NetworkState

/**
 * @author ndn
 * Created by ndn
 * Created on 6/20/18.
 */
class NetworkStateRetryHolder private constructor(
    private val binding: com.halo.databinding.NetworkStateRetryItemBinding,
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
        ): NetworkStateRetryHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding =
                DataBindingUtil.inflate<com.halo.databinding.NetworkStateRetryItemBinding>(
                    layoutInflater,
                    R.layout.network_state_retry_item, parent, false
                )
            return NetworkStateRetryHolder(
                binding,
                listener
            )
        }
    }
}
