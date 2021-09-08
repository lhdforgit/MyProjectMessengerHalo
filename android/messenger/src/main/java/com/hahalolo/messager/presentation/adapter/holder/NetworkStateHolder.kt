/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.presentation.adapter.holder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.NetworkStateItemBinding

/**
 * @author ndn
 * Created by ndn
 * Created on 6/20/18.
 */
class NetworkStateHolder
private constructor(
    private val binding: NetworkStateItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind() {
        binding.executePendingBindings()
    }

    companion object {
        fun createHolder(parent: ViewGroup): NetworkStateHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = DataBindingUtil.inflate<NetworkStateItemBinding>(
                layoutInflater,
                R.layout.network_state_item,
                parent,
                false
            )
            return NetworkStateHolder(
                binding
            )
        }
    }
}
