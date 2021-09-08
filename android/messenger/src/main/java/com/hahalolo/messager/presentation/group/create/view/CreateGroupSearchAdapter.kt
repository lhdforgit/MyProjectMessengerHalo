package com.hahalolo.messager.presentation.group.create.view

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.hahalolo.messager.bubble.conversation.detail.friend.select.adapter.FriendSelectData
import com.hahalolo.messager.presentation.adapter.preload.AbsPreloadAdapter
import com.hahalolo.messager.presentation.group.create.adapter.ChannelContactListener
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatFriendCreateGroupItemBinding
import com.halo.common.utils.GlideRequestBuilder
import com.halo.data.entities.user.User
import com.halo.widget.ErrorHideEmptyHolder

class CreateGroupSearchAdapter(
    private val listener: ChannelContactListener,
    diffCallback: CreateGroupSearchDiff,
    viewPreloadSizeProvider: ViewPreloadSizeProvider<User>,
    requestManager: RequestManager
) : AbsPreloadAdapter<User>(diffCallback, viewPreloadSizeProvider, requestManager) {

    companion object {
        const val USER_ITEM_VIEW = 111
        const val ERROR_ITEM_VIEW = 112
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val holder = when (viewType) {
            USER_ITEM_VIEW -> {
                CreateGroupSearchVewHolder.createHolder(parent, requestManager, listener)
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
            is CreateGroupSearchVewHolder -> holder.bind(getItem(position))
            is ErrorHideEmptyHolder -> holder.bind()
        }
        checkPreloadHolder(holder)
    }

    override fun getItemViewType(position: Int): Int {
        getItem(position).run {
            if (userId?.isNotEmpty() == true) return USER_ITEM_VIEW
        }
        return ERROR_ITEM_VIEW
    }

    override fun getItemCount(): Int {
        return listDatas.size
    }
}

class CreateGroupSearchVewHolder(
    private val binding: ChatFriendCreateGroupItemBinding,
    private val requestManager: RequestManager,
    private val listener: ChannelContactListener
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(user: User) {
        binding.apply {

            usernameTv.text = user.userName()
            GlideRequestBuilder
                .getCircleCropRequest(requestManager)
                .load(user.avatar)
                .placeholder(R.drawable.ic_dummy_personal_circle)
                .into(avatarImg)

            // status
            checkBt.isChecked = listener.isChecked(user.userId ?: "")
            // action
            root.setOnClickListener {
                checkBt.isChecked = !checkBt.isChecked
                listener.onClickItem(
                    FriendSelectData.transformFriendSelectData(
                        user
                    ), checkBt.isChecked
                )
            }
            checkBt.setOnCheckedChangeListener { compoundButton, isChecked ->
                compoundButton.setOnClickListener {
                    listener.onClickItem(
                        FriendSelectData.transformFriendSelectData(
                            user
                        ), isChecked
                    )
                }
            }
            executePendingBindings()
        }
    }

    companion object {
        @JvmStatic
        fun createHolder(
            parent: ViewGroup,
            requestManager: RequestManager,
            callback: ChannelContactListener
        ): CreateGroupSearchVewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = DataBindingUtil.inflate<ChatFriendCreateGroupItemBinding>(
                layoutInflater,
                R.layout.chat_friend_create_group_item,
                parent,
                false
            )
            return CreateGroupSearchVewHolder(binding, requestManager, callback)
        }
    }

}


class CreateGroupSearchDiff : DiffUtil.ItemCallback<User>() {

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