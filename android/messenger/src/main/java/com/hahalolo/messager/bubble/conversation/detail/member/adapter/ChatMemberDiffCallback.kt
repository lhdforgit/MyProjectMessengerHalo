/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.bubble.conversation.detail.member.adapter

import android.text.TextUtils
import androidx.recyclerview.widget.DiffUtil
import com.halo.data.room.entity.MemberEntity

class ChatMemberDiffCallback(var oldList: List<MemberEntity>?, var newList: List<MemberEntity>?) :
    DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList?.size ?: 0
    }

    override fun getNewListSize(): Int {
        return newList?.size ?: 0
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return TextUtils.equals(
            oldList?.get(oldItemPosition)?.userId(),
            newList?.get(newItemPosition)?.userId()
        )
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return TextUtils.equals(oldList?.get(oldItemPosition)?.memberName(), newList?.get(newItemPosition)?.memberName())
                && TextUtils.equals(oldList?.get(oldItemPosition)?.userId(), newList?.get(newItemPosition)?.userId())
                && oldList?.get(oldItemPosition)?.memberRole() == newList?.get(newItemPosition)?.memberRole()
    }
}