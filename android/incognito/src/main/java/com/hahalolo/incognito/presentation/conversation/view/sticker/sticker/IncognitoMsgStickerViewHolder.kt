package com.hahalolo.incognito.presentation.conversation.view.sticker.sticker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoMsgStickerItemBinding
import com.halo.widget.room.table.StickerTable

class IncognitoMsgStickerViewHolder(private val  binding: IncognitoMsgStickerItemBinding,
                                    private val onClick: (sticker: StickerTable) -> Unit)
    : RecyclerView.ViewHolder(binding.root){
    fun onBind(sticker: StickerTable) {
        Glide.with(itemView.context)
            .load(sticker.stickerUrl)
            .thumbnail(Glide.with(itemView.context).load(sticker.stickerImage))
            .into(binding.icon)
        itemView.setOnClickListener {
            onClick.invoke(sticker)
        }
    }

    companion object{
        fun create(parent: ViewGroup, onClick: ((sticker: StickerTable) -> Unit)): IncognitoMsgStickerViewHolder{
            val binding = DataBindingUtil.inflate<IncognitoMsgStickerItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.incognito_msg_sticker_item,
                parent, false )
           return  IncognitoMsgStickerViewHolder(binding, onClick)
        }
    }
}