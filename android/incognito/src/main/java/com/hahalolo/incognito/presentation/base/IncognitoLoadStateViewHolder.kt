package com.hahalolo.incognito.presentation.base

import android.view.View
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.hahalolo.incognito.databinding.LoadStatePagingItemBinding

class IncognitoLoadStateViewHolder(
    val callback: IncognitoLoadStateCallback,
    val binding: LoadStatePagingItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(loadState: LoadState) {
        binding.apply {
            loading.visibility = if (loadState is LoadState.Loading) View.VISIBLE else View.GONE
            retryBt.visibility = if (loadState is LoadState.Error) View.VISIBLE else View.GONE
            retryBt.setOnClickListener { callback.retry() }
        }
    }
}

interface IncognitoLoadStateCallback {
    fun retry()
}
