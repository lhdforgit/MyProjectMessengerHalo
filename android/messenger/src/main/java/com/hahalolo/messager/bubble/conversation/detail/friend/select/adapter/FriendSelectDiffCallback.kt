/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.bubble.conversation.detail.friend.select.adapter

import android.text.TextUtils
import androidx.recyclerview.widget.DiffUtil

class FriendSelectDiffCallback(
    val oldList: List<FriendSelectData>,
    val newList: List<FriendSelectData>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return areItemsTheSame(oldList.get(oldItemPosition), newList.get(newItemPosition))
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return areContentsTheSame(oldList.get(oldItemPosition), newList.get(newItemPosition))
    }

    protected fun areItemsTheSame(oldItem: FriendSelectData, newItem: FriendSelectData): Boolean {
        return TextUtils.equals(oldItem.userId, newItem.userId)
    }

    private fun areContentsTheSame(oldItem: FriendSelectData, newItem: FriendSelectData): Boolean {
        return TextUtils.equals(oldItem.userId, newItem.userId)
                && TextUtils.equals(oldItem.fullName, newItem.fullName)
                && TextUtils.equals(oldItem.avatar, newItem.avatar)
    }
}