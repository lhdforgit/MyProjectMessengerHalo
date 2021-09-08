package com.hahalolo.incognito.presentation.main.contact.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoContactItemBinding

class IncognitoContactViewHolder(private val binding: IncognitoContactItemBinding,
private val listener: IncognitoContactListener) :
    RecyclerView.ViewHolder(binding.root) {

    fun onBind(){
        itemView.setOnClickListener {
            listener.onClick()
        }
    }

    companion object {
        fun create(parent: ViewGroup, listener: IncognitoContactListener): IncognitoContactViewHolder {
            val binding = DataBindingUtil.inflate<IncognitoContactItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.incognito_contact_item,
                parent,
                false
            )
            return IncognitoContactViewHolder(binding, listener)
        }
    }
}