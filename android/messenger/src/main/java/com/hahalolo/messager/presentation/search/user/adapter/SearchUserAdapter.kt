package com.hahalolo.messager.presentation.search.user.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.hahalolo.messager.presentation.adapter.preload.AbsPreloadAdapter
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatSearchUserItemBinding
import com.halo.common.utils.GlideRequestBuilder
import com.halo.data.entities.user.User
import com.halo.widget.ErrorHideEmptyHolder

class SearchUserAdapter(
    private val callback: SearchUserCallback,
    diffCallback: SearchUserDiff,
    viewPreloadSizeProvider: ViewPreloadSizeProvider<User>,
    requestManager: RequestManager
) : AbsPreloadAdapter<User>(diffCallback, viewPreloadSizeProvider, requestManager) {

    var isShowMore: Boolean = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    companion object {
        const val COUNT_DISPLAY = 5
        const val USER_ITEM_VIEW = 111
        const val ERROR_ITEM_VIEW = 112
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val holder = when (viewType) {
            USER_ITEM_VIEW -> {
                SearchUserVewHolder.createHolder(parent, requestManager, callback)
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
            is SearchUserVewHolder -> holder.bind(getItem(position))
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
        if (isShowMore) return listDatas.size
        return COUNT_DISPLAY
    }
}

class SearchUserVewHolder(
    private val binding: ChatSearchUserItemBinding,
    private val requestManager: RequestManager,
    private val callback: SearchUserCallback
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(user: User) {
        binding.apply {
            nameTv.text = user.userName()
            GlideRequestBuilder
                .getCircleCropRequest(requestManager)
                .load(user.avatar)
                .placeholder(R.drawable.ic_dummy_personal_circle)
                .into(avatarIv)
            sendBt.setOnClickListener { callback.onSendMessage(user.userId ?: "") }
            avatarIv.setOnClickListener { callback.onViewPersonalWall(user.userId ?: "") }
            executePendingBindings()
        }
    }

    companion object {
        @JvmStatic
        fun createHolder(
            parent: ViewGroup,
            requestManager: RequestManager,
            callback: SearchUserCallback
        ): SearchUserVewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = DataBindingUtil.inflate<ChatSearchUserItemBinding>(
                layoutInflater,
                R.layout.chat_search_user_item,
                parent,
                false
            )
            return SearchUserVewHolder(binding, requestManager, callback)
        }
    }

}


class SearchUserDiff : DiffUtil.ItemCallback<User>() {

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