/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.messager.presentation.group.create.adapter

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.hahalolo.messager.bubble.base.PagingBubbleAdapterWithHeader
import com.halo.common.utils.list.ListUtils
import com.hahalolo.messager.bubble.conversation.detail.friend.select.adapter.FriendSelectData
import com.hahalolo.messager.utils.Strings
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.*
import com.halo.common.utils.ktx.executeAfter
import com.halo.data.entities.contact.Contact
import com.halo.widget.databinding.ErrorHideEmptyHolderBinding

class ChannelContactPageListAdapter
constructor(
    val listener: ChannelContactListener,
    val requestManager: RequestManager
) : PagingBubbleAdapterWithHeader<Contact, RecyclerView.ViewHolder>(
    ITEM_COMPARATOR,
    HEADER_OFFSET
) {

    var empty = false
        set(value) {
            field = value
            notifyItemChanged(1)
        }

    var error = false
        set(value) {
            field = value
            notifyItemChanged(2)
        }

    fun updateStatusCheck(index: Int) {
        notifyItemChanged(index + HEADER_OFFSET)
    }

    companion object {
        const val HEADER_OFFSET = 3
        val ITEM_COMPARATOR =
            object : DiffUtil.ItemCallback<Contact>() {
                override fun areContentsTheSame(
                    oldItem: Contact,
                    newItem: Contact
                ): Boolean {
                    return TextUtils.equals(oldItem.contactId, newItem.contactId)
                }

                override fun areItemsTheSame(
                    oldItem: Contact,
                    newItem: Contact
                ): Boolean {
                    return TextUtils.equals(
                        oldItem.contactId,
                        newItem.contactId
                    ) && TextUtils.equals(
                        oldItem.firstName,
                        newItem.firstName
                    ) && TextUtils.equals(
                        oldItem.lastName,
                        newItem.lastName
                    )
                }
            }
        private const val SEARCH_VIEW_TYPE = 0 // layout search view
        private const val EMPTY_VIEW_TYPE = 1 // layout list empty
        private const val HIDE_VIEW_TYPE = 2 // layout empty to hide item
        private const val NETWORK_VIEW_TYPE = 3 // layout error network
        private const val ITEM_VIEW_TYPE = 4 // layout item
    }


    override fun getItemViewType(position: Int) = when (position) {
        0 -> {
            HIDE_VIEW_TYPE
        }
        1 -> when {
            empty -> EMPTY_VIEW_TYPE
            else -> HIDE_VIEW_TYPE
        }
        2 -> when {
            error -> NETWORK_VIEW_TYPE
            else -> HIDE_VIEW_TYPE
        }
        else -> when {
            isExist(getPositionItemWithOffset(position)) || listener.isMember(getItem(position)?.userId) || error || isErrorData(
                position
            ) -> HIDE_VIEW_TYPE
            else -> ITEM_VIEW_TYPE
        }
    }

    private fun isExist(position: Int): Boolean {
        differ.snapshot().items.takeIf { it.size > position }?.run {
            val item = get(position)
            val preList = subList(0, position)
            return ListUtils.isDataExits(preList) { input ->
                TextUtils.equals(
                    item.userId,
                    input?.userId
                )
            }
        } ?: kotlin.run {
            return false
        }
    }

    private fun isErrorData(position: Int): Boolean {
        getItem(position)?.apply {
            return userId?.isEmpty() ?: true
        }
        return true
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            SEARCH_VIEW_TYPE -> {
                ChatFriendHolder.SearchHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.chat_friend_search_item,
                        parent,
                        false
                    )
                )
            }
            EMPTY_VIEW_TYPE -> {
                ChatFriendHolder.EmptyHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.chat_friend_empty_item,
                        parent,
                        false
                    )
                )
            }
            NETWORK_VIEW_TYPE -> {
                ChatFriendHolder.ErrorNetworkHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.error_network_item,
                        parent,
                        false
                    )
                )
            }
            HIDE_VIEW_TYPE -> {
                ChatFriendHolder.ErrorHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.error_hide_empty_holder,
                        parent,
                        false
                    )
                )
            }
            else -> {
                ChatFriendHolder.FriendHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.chat_friend_create_group_item,
                        parent,
                        false
                    )
                )
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ChatFriendHolder.SearchHolder -> {
                holder.binding.executeAfter {
                    this.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(p0: String?): Boolean {
                            return false
                        }

                        override fun onQueryTextChange(query: String?): Boolean {
                            listener.onSearchFriend(query)
                            return false
                        }
                    })
                }
            }
            is ChatFriendHolder.EmptyHolder -> {
                holder.binding.executeAfter {

                }
            }
            is ChatFriendHolder.ErrorNetworkHolder -> {
                holder.binding.executeAfter {
                    this.retryBt.setOnClickListener { listener.onRetryNetwork() }
                }
            }
            is ChatFriendHolder.ErrorHolder -> {
                holder.binding.executeAfter {

                }
            }
            is ChatFriendHolder.FriendHolder -> {
                holder.binding.executeAfter {
                    getItem(position)?.run {
                        // avatar
                        requestManager.apply {
                            load(avatar ?: "")
                                .placeholder(R.drawable.ic_dummy_personal_circle)
                                .error(R.drawable.ic_dummy_personal_circle)
                                .circleCrop()
                                .into(avatarImg)
                        }
                        // name
                        usernameTv.text = this.fullName
                        // status
                        checkBt.isChecked = listener.isChecked(userId ?: "")
                        // action
                        root.setOnClickListener {
                            checkBt.isChecked = !checkBt.isChecked
                            listener.onClickItem(
                                FriendSelectData.transformFriendSelectData(
                                    this
                                ), checkBt.isChecked
                            )
                        }
                        checkBt.setOnCheckedChangeListener { compoundButton, isChecked ->
                            compoundButton.setOnClickListener {
                                listener.onClickItem(
                                    FriendSelectData.transformFriendSelectData(
                                        this
                                    ), isChecked
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    sealed class ChatFriendHolder(view: View) : RecyclerView.ViewHolder(view) {
        class SearchHolder(val binding: ChatFriendSearchItemBinding) :
            ChatFriendHolder(binding.root)

        class EmptyHolder(val binding: ChatFriendEmptyItemBinding) :
            ChatFriendHolder(binding.root)

        class ErrorNetworkHolder(val binding: ErrorNetworkItemBinding) :
            ChatFriendHolder(binding.root)

        class ErrorHolder(val binding: ErrorHideEmptyHolderBinding) :
            ChatFriendHolder(binding.root)

        class FriendHolder(val binding: ChatFriendCreateGroupItemBinding) :
            ChatFriendHolder(binding.root)
    }
}