/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.bubble.conversation.detail.friend.select.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatFriendSelectHeaderItemBinding
import java.util.*

class FriendSelectAdapter constructor(
    val listener: FriendChooseListener?,
    val requestManager: RequestManager?
) :
    RecyclerView.Adapter<FriendSelectAdapter.FriendSelectViewHolder>() {

    private var listData = mutableListOf<FriendSelectData>()

    private lateinit var binding: ChatFriendSelectHeaderItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendSelectViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.chat_friend_select_header_item,
            parent,
            false
        )
        return FriendSelectViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: FriendSelectViewHolder, position: Int) {
        listData[position].let {
            holder.bind(it, position)
        }
    }

    fun updateList(data: List<FriendSelectData>) {
        val diffCallback =
            FriendSelectDiffCallback(
                listData,
                data
            )
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        listData.clear()
        listData.addAll(ArrayList(data))
        diffResult.dispatchUpdatesTo(this)
    }

    inner class FriendSelectViewHolder(val binding: ChatFriendSelectHeaderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: FriendSelectData, position: Int) {
            data.run {
                updateAvatar(avatar)
                updateUsername(fullName)
            }
            binding.root.setOnClickListener {
                listener?.onRemoveFriendSelected(position, data)
            }
        }

        private fun updateAvatar(avatar: String?) {
            requestManager?.apply {
                load(avatar ?: "")
                    .placeholder(R.drawable.ic_dummy_personal_circle)
                    .error(R.drawable.ic_dummy_personal_circle)
                    .circleCrop()
                    .into(binding.avatarImg)
            }
        }

        private fun updateUsername(fullName: String?){
            binding.nameTv.text = fullName ?: ""
        }
    }
}

