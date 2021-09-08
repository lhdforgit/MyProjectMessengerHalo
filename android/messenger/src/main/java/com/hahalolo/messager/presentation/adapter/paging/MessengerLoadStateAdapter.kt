package com.hahalolo.messager.presentation.adapter.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.MessengerLoadStatePagingItemBinding

// Adapter that displays a loading spinner when
// state = LoadState.Loading, and an error message and retry
// button when state is LoadState.Error.
class MessengerLoadStateAdapter(val callback: MessengerLoadStateCallback) :
    LoadStateAdapter<MessengerLoadStateViewHolder>() {
    override fun onBindViewHolder(holder: MessengerLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): MessengerLoadStateViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<MessengerLoadStatePagingItemBinding>(
            inflater,
            R.layout.messenger_load_state_paging_item,
            parent,
            false
        )
        return MessengerLoadStateViewHolder(callback, binding)
    }
}