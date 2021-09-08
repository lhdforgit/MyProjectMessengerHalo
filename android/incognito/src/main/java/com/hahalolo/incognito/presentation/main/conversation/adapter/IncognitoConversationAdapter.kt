package com.hahalolo.incognito.presentation.main.conversation.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class IncognitoConversationAdapter (private val listener: IncognitoConversationListener)
    : RecyclerView.Adapter<IncognitoConversationViewHolder>() {

    private val listData = mutableListOf<String>("","","","","","")


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): IncognitoConversationViewHolder {
        return IncognitoConversationViewHolder.create(parent, listener)
    }

    override fun onBindViewHolder(holder: IncognitoConversationViewHolder, position: Int) {
        holder.onBind()
    }

    override fun getItemCount(): Int {
        return  listData.size
    }


}