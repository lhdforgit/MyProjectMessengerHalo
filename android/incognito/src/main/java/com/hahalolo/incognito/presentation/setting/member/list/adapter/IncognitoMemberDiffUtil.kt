package com.hahalolo.incognito.presentation.setting.member.list.adapter

import android.text.TextUtils
import androidx.recyclerview.widget.DiffUtil
import com.hahalolo.incognito.presentation.setting.model.MemberModel

class IncognitoMemberDiffUtil : DiffUtil.Callback {

    private var oldList = listOf<MemberModel>()
    private var newList = listOf<MemberModel>()

    constructor(oldList: List<MemberModel>, newList: List<MemberModel>) {
        this.oldList = oldList
        this.newList = newList
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return TextUtils.equals(oldList[oldItemPosition].id, newList[newItemPosition].id)
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return TextUtils.equals(oldList[oldItemPosition].id, newList[newItemPosition].id) &&
                TextUtils.equals(oldList[oldItemPosition].name, newList[newItemPosition].name) &&
                TextUtils.equals(oldList[oldItemPosition].avatar, newList[newItemPosition].avatar)
    }
}