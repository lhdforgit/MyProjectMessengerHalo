package com.hahalolo.messager.bubble.conversation.home.adapter.holder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.MessageEmptyUndefineErrorItemBinding

/**
 * @author ndn
 * Created by ndn
 * Created on 8/6/18
 */
class MessageEmptyUndefineErrorHolder
private constructor(
    private val binding: MessageEmptyUndefineErrorItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind() {
        binding.executePendingBindings()
    }

    companion object {

        fun createHolder(parent: ViewGroup): MessageEmptyUndefineErrorHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = DataBindingUtil.inflate<MessageEmptyUndefineErrorItemBinding>(
                inflater,
                R.layout.message_empty_undefine_error_item,
                parent,
                false
            )
            return MessageEmptyUndefineErrorHolder(
                binding
            )
        }
    }
}