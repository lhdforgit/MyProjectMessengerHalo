package com.hahalolo.messager.bubble.conversation.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.bumptech.glide.Glide
import com.halo.data.room.entity.MemberEntity
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatMentionDialogBinding
import com.halo.common.utils.ThumbImageUtils

class MentionDialog(context: Context) : Dialog(context) {

    private lateinit var binding : ChatMentionDialogBinding

    init {
        initView()
    }

    private fun initView() {
        binding = ChatMentionDialogBinding.inflate(LayoutInflater.from(this.context), null)
        setContentView(binding.root)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    fun showMention(member: MemberEntity, listener: Listener){
        Glide.with(context)
            .load(
                ThumbImageUtils.thumb(
                    ThumbImageUtils.Size.AVATAR_NORMAL,
                    member.getAvatar(),
                    ThumbImageUtils.TypeSize._1_1
                )
            )
            .placeholder(R.drawable.ic_dummy_personal_circle)
            .error(R.drawable.ic_dummy_personal_circle)
            .circleCrop()
            .into(binding.avatar)
        binding.userNameTv.text = member.memberName()
        binding.subNameTv.text = member.getFullName()
        binding.subNameTv.visibility = if( member.isHaveNickname() ) View.VISIBLE else View.GONE

        binding.actionChat.visibility = if (listener.isOwner(member.userId())) View.INVISIBLE else View.VISIBLE
        binding.actionChat.setOnClickListener {
            listener.navigateChat(member)
            dismiss()
        }
        binding.actionPersonalWall.setOnClickListener {
            listener.navigatePersonalWall(member)
            dismiss()
        }
        show()
    }

    interface Listener{
        fun isOwner(userId:String) : Boolean
        fun navigatePersonalWall(mention: MemberEntity)
        fun navigateChat(mention: MemberEntity)
    }

}