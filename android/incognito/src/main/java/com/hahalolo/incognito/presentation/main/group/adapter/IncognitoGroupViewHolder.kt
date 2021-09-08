package com.hahalolo.incognito.presentation.main.group.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoGroupItemBinding

class IncognitoGroupViewHolder(
    private val binding: IncognitoGroupItemBinding,
    private val listener: IncognitoGroupListener
) :
    RecyclerView.ViewHolder(binding.root) {

    fun onBind() {
        itemView.setOnClickListener { listener.onClick() }

    }


    companion object {
        fun create(parent: ViewGroup, listener: IncognitoGroupListener): IncognitoGroupViewHolder {
            val binding = DataBindingUtil.inflate<IncognitoGroupItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.incognito_group_item,
                parent,
                false
            )
            return IncognitoGroupViewHolder(binding, listener)
        }
    }

}