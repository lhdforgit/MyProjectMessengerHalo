package com.hahalolo.messager.presentation.forward_message.container

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.hahalolo.messager.presentation.adapter.paging.MessengerPagingAdapter
import com.hahalolo.messager.presentation.forward_message.ForwardModel
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatForwardMessageItemBinding
import com.hahalolo.messenger.databinding.ChatFriendEmptyItemBinding
import com.halo.common.utils.GlideRequestBuilder
import com.halo.common.utils.ThumbImageUtils
import com.halo.common.utils.ktx.executeAfter
import com.halo.common.utils.list.ListUtils
import com.halo.widget.databinding.ErrorHideEmptyHolderBinding

class ForwardMessageChannelAdapter(
    val listener: ForwardMessageListener,
    val preloadSizeProvider: ViewPreloadSizeProvider<ForwardModel>,
    val requestManager: RequestManager
) : MessengerPagingAdapter<ForwardModel>(
    ITEM_COMPARATOR,
    preloadSizeProvider,
    requestManager,
    headerCount
) {

    var notifyEmpty = false
        set(value) {
            field = value
            notifyItemChanged(0)
        }


    fun notifyUpdateStatus(id: String, status: Int) {
        ListUtils.getPosition(snapshot()) { input -> TextUtils.equals(input?.id, id) }
            .let { postion ->
                if (postion >= 0 && postion < snapshot().size) {
                    snapshot()[postion]?.status = status
                    notifyItemChanged(postion + headerCount)
                }
            }
    }

    companion object {
        const val headerCount = 1
        val ITEM_COMPARATOR =
            object : DiffUtil.ItemCallback<ForwardModel>() {
                override fun areContentsTheSame(
                    oldItem: ForwardModel,
                    newItem: ForwardModel
                ): Boolean {
                    return TextUtils.equals(oldItem.id, newItem.id)
                }

                override fun areItemsTheSame(
                    oldItem: ForwardModel,
                    newItem: ForwardModel
                ): Boolean {
                    return TextUtils.equals(
                        oldItem.id,
                        newItem.id
                    ) && TextUtils.equals(
                        oldItem.name,
                        newItem.name
                    ) && TextUtils.equals(
                        oldItem.avatar,
                        newItem.avatar
                    )
                }
            }
        private const val ITEM_VIEW_ERROR_TYPE = 0
        private const val ITEM_VIEW_CONTACT_TYPE = 1
        private const val ITEM_VIEW_EMPTY_TYPE = 2
    }


    override fun getItemViewType(position: Int): Int {
        return when {
            isEmptyData(position) -> {
                if (notifyEmpty) ITEM_VIEW_EMPTY_TYPE else ITEM_VIEW_ERROR_TYPE
            }
            isErrorData(position) -> ITEM_VIEW_ERROR_TYPE
            else -> ITEM_VIEW_CONTACT_TYPE
        }
    }


    private fun isEmptyData(position: Int): Boolean {
        return position == 0
    }

    private fun isErrorData(position: Int): Boolean {
        getItem(position)?.apply {
            return id.isEmpty()
        }
        return true
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_EMPTY_TYPE -> {
                IncognitoInviteContactHolder.EmptyHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.chat_friend_empty_item,
                        parent,
                        false
                    )
                )
            }
            ITEM_VIEW_ERROR_TYPE -> {
                IncognitoInviteContactHolder.ErrorHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.error_hide_empty_holder,
                        parent,
                        false
                    )
                )
            }
            else -> {
                IncognitoInviteContactHolder.ContactHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.chat_forward_message_item,
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
            is IncognitoInviteContactHolder.ErrorHolder -> {
                holder.binding.executeAfter {

                }
            }
            is IncognitoInviteContactHolder.EmptyHolder -> {
                holder.binding.executeAfter {
                    this.emptyTv.text = ""
                }
            }
            is IncognitoInviteContactHolder.ContactHolder -> {
                holder.binding.executeAfter {
                    getItem(position)?.let { contact ->
                        nameTv.text = contact.name
                        GlideRequestBuilder
                            .getCenterCropRequest(requestManager)
                            .load(ThumbImageUtils.thumb(contact.avatar))
                            .placeholder(R.drawable.ic_dummy_personal_circle)
                            .into(avatarImg)
                        statusView.setStatus(contact.status)
                        statusView.setOnClickListener {
                            listener.onItemClick(contact)
                        }
                    }
                }
            }
        }
    }

    sealed class IncognitoInviteContactHolder(view: View) : RecyclerView.ViewHolder(view) {

        class ErrorHolder(val binding: ErrorHideEmptyHolderBinding) :
            IncognitoInviteContactHolder(binding.root)

        class EmptyHolder(val binding: ChatFriendEmptyItemBinding) :
            IncognitoInviteContactHolder(binding.root)

        class ContactHolder(val binding: ChatForwardMessageItemBinding) :
            IncognitoInviteContactHolder(binding.root)
    }
}

interface ForwardMessageListener {
    fun onItemClick(contact: ForwardModel)
}
