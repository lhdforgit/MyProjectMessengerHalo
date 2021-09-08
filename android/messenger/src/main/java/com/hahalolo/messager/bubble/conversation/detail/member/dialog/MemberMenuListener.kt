package com.hahalolo.messager.bubble.conversation.detail.member.dialog

import com.halo.data.room.entity.MemberEntity

interface MemberMenuListener {
    fun changeNickName(memberEntity: MemberEntity)
    fun setAdmin(memberEntity: MemberEntity)
    fun removeAdmin(memberEntity: MemberEntity)
    fun removeMember(memberEntity: MemberEntity)
    fun sendMessage(memberEntity: MemberEntity)
    fun navigatePersonalWall(memberEntity: MemberEntity)
    fun leaveGroup(memberEntity: MemberEntity)
}