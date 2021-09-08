package com.hahalolo.messager.presentation.write_message.search

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.hahalolo.messager.presentation.adapter.preload.AbsPreloadAdapter
import com.hahalolo.messager.presentation.main.contacts.ChatContactsListener
import com.hahalolo.messager.utils.Strings
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatContactItemBinding
import com.halo.common.utils.GlideRequestBuilder
import com.halo.data.entities.user.User
import com.halo.widget.ErrorHideEmptyHolder

class ChatWriteSearchAdapter(
    private val info: Boolean,
//    private val listener: ChatContactsListener,
    diffCallback: ChatWriteSearchDiff,
    viewPreloadSizeProvider: ViewPreloadSizeProvider<User>,
    requestManager: RequestManager
) : AbsPreloadAdapter<User>(diffCallback, viewPreloadSizeProvider, requestManager) {

    companion object {
        const val CONTACT_ITEM_VIEW = 111
        const val ERROR_ITEM_VIEW = 112
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val holder = when (viewType) {
            CONTACT_ITEM_VIEW -> {
                ChatWriteSearchVewHolder.createHolder(info, parent, requestManager)
            }
            else -> {
                ErrorHideEmptyHolder.build(parent)
            }
        }
        checkPreloadHolder(holder)
        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ChatWriteSearchVewHolder -> holder.bind(getItem(position))
            is ErrorHideEmptyHolder -> holder.bind()
        }
        checkPreloadHolder(holder)
    }

    override fun getItemViewType(position: Int): Int {
        getItem(position).run {
            if(userId?.isNotEmpty() == true) return CONTACT_ITEM_VIEW
        }
        return ERROR_ITEM_VIEW
    }

    override fun getItemCount(): Int {
        return listDatas.size
    }
}

class ChatWriteSearchVewHolder(
    private val infoCheck: Boolean,
    private val binding: ChatContactItemBinding,
    private val requestManagerr: RequestManager,
//    private val listener: ChatContactsListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(user: User) {
        binding.apply {
            conversationNameTv.text = user.userName()
            GlideRequestBuilder
                .getCircleCropRequest(requestManagerr)
                .load(user.avatar)
                .placeholder(R.drawable.ic_dummy_personal_circle)
                .into(avatarIv)

            isInfo = infoCheck

//            itemView.setOnClickListener { listener.onMessage(user) }
            executePendingBindings()
        }
    }

    companion object {
        @JvmStatic
        fun createHolder(
            infoCheck: Boolean,
            parent: ViewGroup,
            requestManager: RequestManager,
//            callback: ChatContactsListener
        ): ChatWriteSearchVewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = DataBindingUtil.inflate<ChatContactItemBinding>(
                layoutInflater,
                R.layout.chat_contact_item,
                parent, false
            )
            return ChatWriteSearchVewHolder(infoCheck, binding, requestManager)
        }
    }
}

class ChatWriteSearchDiff : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return TextUtils.equals(oldItem.userId, newItem.userId)
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return TextUtils.equals(oldItem.userId, newItem.userId) ||
                TextUtils.equals(oldItem.firstName, newItem.firstName) ||
                TextUtils.equals(oldItem.lastName, newItem.lastName) ||
                TextUtils.equals(oldItem.avatar, newItem.avatar)
    }
}

interface ChatWriteSearchInterface {
    fun detailUser(user: String?, contact: String?)
    fun onMessage(contact: User)
}

