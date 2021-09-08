package com.hahalolo.messager.presentation.adapter.paging

import android.view.View
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.hahalolo.messenger.databinding.MessengerLoadStatePagingItemBinding

class MessengerLoadStateViewHolder(
    val callback: MessengerLoadStateCallback,
    val binding: MessengerLoadStatePagingItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(loadState: LoadState) {
        binding.apply {
            loading.visibility = if (loadState is LoadState.Loading) View.VISIBLE else View.GONE
            retryBt.visibility = if (loadState is LoadState.Error) View.VISIBLE else View.GONE
            retryBt.setOnClickListener { callback.retry() }
        }
    }
}

interface MessengerLoadStateCallback {
    fun retry()
}
