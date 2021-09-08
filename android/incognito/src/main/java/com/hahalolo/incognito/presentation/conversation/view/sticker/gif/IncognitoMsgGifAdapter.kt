package com.hahalolo.incognito.presentation.conversation.view.sticker.gif

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.giphy.sdk.core.models.Media

class IncognitoMsgGifAdapter(private val onClick: (media: Media)->Unit) : RecyclerView.Adapter<IncognitoMsgGifViewHolder>() {

    private var list = mutableListOf<Media>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncognitoMsgGifViewHolder {
        return IncognitoMsgGifViewHolder.create(parent, onClick)
    }

    override fun onBindViewHolder(holder: IncognitoMsgGifViewHolder, position: Int) {
        holder.onBind(list.get(position))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateList(list: List<Media>) {
        this.list = list.toMutableList()
        notifyDataSetChanged()
    }
}