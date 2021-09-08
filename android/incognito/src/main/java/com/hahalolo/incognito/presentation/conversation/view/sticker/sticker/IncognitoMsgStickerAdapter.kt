package com.hahalolo.incognito.presentation.conversation.view.sticker.sticker

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.halo.widget.room.table.StickerTable

class IncognitoMsgStickerAdapter (private val onClick: (sticker: StickerTable) -> Unit)
    : RecyclerView.Adapter<IncognitoMsgStickerViewHolder>() {

    private var list = mutableListOf<StickerTable>()


    fun submitList(stickers: MutableList<StickerTable>) {
        list = stickers.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): IncognitoMsgStickerViewHolder {
        return IncognitoMsgStickerViewHolder.create(parent, onClick)
    }

    override fun onBindViewHolder(holder: IncognitoMsgStickerViewHolder, position: Int) {
        holder.onBind(list.get(position))
    }

    override fun getItemCount(): Int {
        return list.size
    }

}