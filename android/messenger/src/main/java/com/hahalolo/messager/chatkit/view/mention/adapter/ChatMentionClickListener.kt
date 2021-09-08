package com.hahalolo.messager.chatkit.view.mention.adapter

import com.halo.data.room.entity.MemberEntity

interface ChatMentionClickListener {
    fun onClickMember(member: MemberEntity)
}