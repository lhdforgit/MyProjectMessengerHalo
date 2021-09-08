/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.messager.bubble.conversation.detail.member.adapter

import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.halo.data.room.entity.MemberEntity
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatMessageMemberGroupItemBinding
import com.halo.common.utils.SpanBuilderUtil
import com.halo.data.room.type.RoleType
import com.halo.widget.HaloTypefaceSpan

class ChatMemberViewHolder(
    val binding: ChatMessageMemberGroupItemBinding,
    val requestManager: RequestManager,
    val listener: ChatMemberListener
) :
    RecyclerView.ViewHolder(binding.root) {


    fun onBind(member: MemberEntity, isMember: Boolean) {
        bindName(member, isMember)
        bindAvatar(member.getAvatar())
        bindAction(member, isMember)
    }

    private fun bindName(member: MemberEntity, isMember: Boolean) {
        binding.actionBt.visibility = if (isMember) View.VISIBLE else View.GONE
        binding.permissionTv.visibility = if (isMember) View.VISIBLE else View.GONE
        binding.onlineIndicator.visibility = if (member.isOnline()) View.VISIBLE else View.GONE
        binding.memberName.text = member.memberName()
        binding.captionTv.visibility = if (member.isHaveNickname() && !isMember)  View.VISIBLE else View.GONE
        binding.captionTv.text = member.getFullName()
        itemView.context?.apply {
            when (member.memberRole()) {
                RoleType.MEMBER -> {
                    binding.permissionTv.text = getString(R.string.chat_message_member)
                }
                RoleType.ADMIN -> {
                    binding.permissionTv.text = SpanBuilderUtil().append(
                        getString(R.string.chat_message_member_admin),
                        HaloTypefaceSpan.REGULAR(this),
                        ForegroundColorSpan(
                            ContextCompat.getColor(this, R.color.text_dark)
                        )
                    ).build()
                }
                RoleType.OWNER -> {
                    binding.permissionTv.text = SpanBuilderUtil().append(
                        getString(R.string.chat_message_owner),
                        HaloTypefaceSpan.REGULAR(this),
                        ForegroundColorSpan(
                            ContextCompat.getColor(this, R.color.text_primary)
                        )
                    ).build()
                }
            }
        }
    }

    private fun bindAvatar(avatar: String) {
        requestManager.load(avatar)
            .error(R.drawable.ic_dummy_personal_circle)
            .placeholder(R.drawable.ic_dummy_personal_circle)
            .circleCrop()
            .into(binding.avatarImg)
    }

    private fun bindAction(member: MemberEntity, isMember: Boolean) {
        if (isMember) {
            binding.avatarImg.setOnClickListener {
                listener.avatarOnClick(member)
            }
            binding.actionBt.setOnClickListener {
                listener.onItemMenuClick(it, member)
            }
            binding.root.setOnClickListener {}
        } else {
            binding.root.setOnClickListener { listener.onClickItemView(member) }
        }
    }

    companion object {
        fun create(
            view: ViewGroup,
            requestManager: RequestManager,
            listener: ChatMemberListener
        ): ChatMemberViewHolder {
            val binding = DataBindingUtil.inflate<ChatMessageMemberGroupItemBinding>(
                LayoutInflater.from(view.context),
                R.layout.chat_message_member_group_item, view, false
            )
            return ChatMemberViewHolder(binding, requestManager, listener)
        }
    }
}