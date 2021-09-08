package com.hahalolo.messager.presentation.main.contacts.adapter.state

import android.view.View
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.hahalolo.messager.presentation.main.contacts.adapter.ContactStateListener
import com.hahalolo.messenger.databinding.ChatContactItemBinding
import com.hahalolo.messenger.databinding.ChatContactStateItemBinding

class ContactStateViewHolder(
    private val binding: ChatContactStateItemBinding? = null,
    private var listener: ContactStateListener?
) : RecyclerView.ViewHolder(binding?.root!!) {

    fun onBind(state: LoadState) {

        binding?.stateError?.visibility = if (state is LoadState.Error) View.VISIBLE else View.GONE
        binding?.stateLoadding?.visibility = if (state is LoadState.Loading) View.VISIBLE else View.GONE

        binding?.btnRetry?.setOnClickListener {
            listener?.retry()
        }
    }
}