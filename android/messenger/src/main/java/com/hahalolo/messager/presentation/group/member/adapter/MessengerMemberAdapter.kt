package com.hahalolo.messager.presentation.group.member.adapter

import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.hahalolo.messager.presentation.adapter.paging.MessengerPagingAdapter
import com.hahalolo.messager.presentation.group.member.MemberRole
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatMessageMemberGroupItemBinding
import com.halo.common.utils.SpanBuilderUtil
import com.halo.data.entities.member.MemberChannel
import com.halo.widget.ErrorHideEmptyHolder
import com.halo.widget.HaloTypefaceSpan

class MessengerMemberAdapter(
    val isShowMenu: Boolean = true,
    diffCallback: MessengerMemberDiffCallback,
    preloadSizeProvider: ViewPreloadSizeProvider<MemberChannel>,
    private val requestManager: RequestManager,
    private val listener: MessengerMemberListener
) : MessengerPagingAdapter<MemberChannel>(
    diffCallback,
    preloadSizeProvider,
    requestManager,
    HEADER_COUNT
) {

    companion object {
        const val HEADER_COUNT = 0
        const val MEMBER_ITEM_VIEW = 111
        const val BLANK_ITEM_VIEW = 112
        const val NETWORK_ITEM_VIEW = 113
        const val EMPTY_DATA_ITEM_VIEW = 114
    }

    override fun getItemViewType(position: Int): Int {
        return if (isErrorData(position)) {
            BLANK_ITEM_VIEW
        } else {
            MEMBER_ITEM_VIEW
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {


        val holder: RecyclerView.ViewHolder = when (viewType) {
            BLANK_ITEM_VIEW -> {
                ErrorHideEmptyHolder.build(parent)
            }
            else -> {
                MessengerMemberViewHolder.create(parent, requestManager, listener, isShowMenu)
            }
        }
        checkPreloadHolder(holder)
        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MessengerMemberViewHolder -> {
                holder.bind(getItem(position))
            }
            is ErrorHideEmptyHolder -> {
                holder.bind()
            }
            else -> {

            }
        }
        checkPreloadHolder(holder)
    }

    private fun isErrorData(position: Int): Boolean {
        return false
    }
}

class MessengerMemberViewHolder(
    val binding: ChatMessageMemberGroupItemBinding,
    val requestManager: RequestManager,
    val listener: MessengerMemberListener,
    val isShowMenu: Boolean
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(member: MemberChannel?) {
        member?.apply {
            this.user?.let { userInfo ->
                binding.memberName.text = this.displayName
                binding.captionTv.text = userInfo.getFullName()
                binding.captionTv.visibility = if (isHasNickname && !isShowMenu) View.VISIBLE else View.GONE
                requestManager.load(userInfo.avatar)
                    .error(R.drawable.ic_dummy_personal_circle)
                    .placeholder(R.drawable.ic_dummy_personal_circle)
                    .circleCrop()
                    .into(binding.avatarImg)

                binding.actionBt.visibility = if (isShowMenu) View.VISIBLE else View.GONE
                binding.permissionTv.visibility = if (isShowMenu) View.VISIBLE else View.GONE
            }
            itemView.context?.run {
                when{
                    member.isOwner ->{
                        binding.permissionTv.text = SpanBuilderUtil().append(
                            getString(R.string.chat_message_owner),
                            HaloTypefaceSpan.REGULAR(this@run),
                            ForegroundColorSpan(
                                ContextCompat.getColor(this@run, R.color.text_primary)
                            )
                        ).build()
                    }
                    member.isAdmin ->{
                        binding.permissionTv.text = SpanBuilderUtil().append(
                            getString(R.string.chat_message_member_admin),
                            HaloTypefaceSpan.REGULAR(this@run),
                            ForegroundColorSpan(
                                ContextCompat.getColor(this@run, R.color.text_dark)
                            )
                        ).build()
                    }
                    member.isMember->{
                        binding.permissionTv.text = getString(R.string.chat_message_member)
                    }
                }
            }
            binAction(this)
        }
    }

    private fun binAction(member: MemberChannel) {
        binding.apply {
            avatarImg.setOnClickListener { listener.onAvatarClick(member.userId ?: "") }
            actionBt.setOnClickListener { listener.onMenuClick(member, actionBt) }
            if (!isShowMenu){
                root.setOnClickListener { listener.onItemClick(member) }
            }
        }
    }

    companion object {
        fun create(
            view: ViewGroup,
            requestManager: RequestManager,
            listener: MessengerMemberListener,
            isShowMenu: Boolean,
        ): MessengerMemberViewHolder {
            val binding = DataBindingUtil.inflate<ChatMessageMemberGroupItemBinding>(
                LayoutInflater.from(view.context),
                R.layout.chat_message_member_group_item, view, false
            )
            return MessengerMemberViewHolder(binding, requestManager, listener, isShowMenu)
        }
    }
}

interface MessengerMemberListener {
    fun onMenuClick(item: MemberChannel, view: View)
    fun onAvatarClick(userId: String)
    fun onItemClick(item: MemberChannel)
}

class MessengerMemberDiffCallback : DiffUtil.ItemCallback<MemberChannel>() {

    override fun areItemsTheSame(oldItem: MemberChannel, newItem: MemberChannel): Boolean {
        return TextUtils.equals(oldItem.memberId, newItem.memberId)
    }

    override fun areContentsTheSame(oldItem: MemberChannel, newItem: MemberChannel): Boolean {
        return TextUtils.equals(oldItem.memberId, newItem.memberId)
                && TextUtils.equals(oldItem.nickName, newItem.nickName)
                && TextUtils.equals(oldItem.updatedAt, newItem.updatedAt)
                && TextUtils.equals(oldItem.deletedAt, newItem.deletedAt)
                && oldItem.roles?.size == newItem.roles?.size
    }

}