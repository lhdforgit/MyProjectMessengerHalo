package com.hahalolo.incognito.presentation.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.LoadStatePagingItemBinding

// Adapter that displays a loading spinner when
// state = LoadState.Loading, and an error message and retry
// button when state is LoadState.Error.
class IncognitoLoadStateAdapter(val callback: IncognitoLoadStateCallback) :
    LoadStateAdapter<IncognitoLoadStateViewHolder>() {
    override fun onBindViewHolder(holder: IncognitoLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): IncognitoLoadStateViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<LoadStatePagingItemBinding>(
            inflater,
            R.layout.load_state_paging_item,
            parent,
            false
        )
        return IncognitoLoadStateViewHolder(callback, binding)
    }
}