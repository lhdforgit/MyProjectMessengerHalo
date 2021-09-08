package com.hahalolo.messager.chatkit.view.mention.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.halo.data.room.entity.MemberEntity
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.MentionMemberItemBinding
import com.halo.common.utils.ThumbImageUtils
import com.halo.common.utils.ktx.bindIsVisible

class ChatMentionViewHolder(
    private val binding: MentionMemberItemBinding,
    private val requestManager: RequestManager,
    private val listener: ChatMentionClickListener
) :
    RecyclerView.ViewHolder(
        binding.root
    ) {

    fun bind(member: MemberEntity) {
        requestManager.load(thumb(member.getAvatar()))
            .placeholder(R.drawable.ic_dummy_personal_circle)
            .error(R.drawable.ic_dummy_personal_circle)
            .circleCrop()
            .into(binding.avatar)

        binding.subNameTv.bindIsVisible(member.isHaveNickname())
        binding.nameTv.text = member.memberName()
        binding.subNameTv.text = member.getFullName()

        itemView.setOnClickListener {
            listener.onClickMember(member)
        }
    }

    fun bindTagAll(member: MemberEntity) {
        requestManager.load(R.drawable.ic_dummy_personal_circle)
            .placeholder(R.drawable.ic_dummy_personal_circle)
            .error(R.drawable.ic_dummy_personal_circle)
            .circleCrop()
            .into(binding.avatar)
        binding.nameTv.text = member.getFullName()

        itemView.setOnClickListener {
            listener.onClickMember(member)
        }
    }

    private fun thumb(path:String?): String?{
        return ThumbImageUtils.thumbAvatar(path)
    }

    companion object {

        fun create(parent: ViewGroup, requestManager: RequestManager, listener: ChatMentionClickListener): ChatMentionViewHolder {
            return ChatMentionViewHolder(
                MentionMemberItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ), requestManager, listener
            )
        }
    }
}