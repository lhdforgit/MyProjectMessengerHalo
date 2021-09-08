package com.hahalolo.incognito.presentation.create.forward.container.group.adapter

import android.text.TextUtils
import androidx.recyclerview.widget.DiffUtil
import com.hahalolo.incognito.presentation.setting.model.ContactModel

class IncognitoForwardGroupDiffUtil : DiffUtil.ItemCallback<ContactModel>() {

    override fun areItemsTheSame(oldItem: ContactModel, newItem: ContactModel): Boolean {
        return TextUtils.equals(oldItem.id, newItem.id)
    }

    override fun areContentsTheSame(oldItem: ContactModel, newItem: ContactModel): Boolean {
        return TextUtils.equals(oldItem.id, newItem.id) &&
                TextUtils.equals(oldItem.name, newItem.name) &&
                TextUtils.equals(oldItem.avatar, newItem.avatar) &&
                oldItem.status == newItem.status
    }
}