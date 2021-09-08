/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.messager.bubble.conversation.detail.member.adapter

import android.view.View
import com.halo.data.room.entity.MemberEntity

interface ChatMemberListener{

    fun avatarOnClick(memberEntity: MemberEntity)

    fun onItemMenuClick(view: View, memberEntity: MemberEntity)

    fun onClickItemView(memberEntity: MemberEntity)
}