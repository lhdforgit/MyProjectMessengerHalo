package com.hahalolo.incognito.presentation.conversation.adapter.model

import android.text.TextUtils
import androidx.recyclerview.widget.DiffUtil

class IncognitoMsgDiffCallback (
    val oldList: MutableList<IncognitoMsgModel>,
    val newList: MutableList<IncognitoMsgModel>): DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean {
        return areItemsTheSame(oldList.get(oldItemPosition), newList.get(newItemPosition))
    }

    private fun areItemsTheSame(oldItem: IncognitoMsgModel, newItem: IncognitoMsgModel): Boolean {
        return TextUtils.equals(oldItem.id(), newItem.id())
    }

    override fun areContentsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean {
        return areContentsTheSame(oldList.get(oldItemPosition), newList.get(newItemPosition))
    }

    private fun areContentsTheSame(oldItem: IncognitoMsgModel, newItem: IncognitoMsgModel): Boolean {
        return TextUtils.equals(oldItem.id(), newItem.id())
                && oldItem.groupType == newItem.groupType
                && oldItem.dataContentsTheSame(newItem.data)
    }
}