package com.hahalolo.incognito.presentation.main.group.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class IncognitoGroupAdapter (private val listener: IncognitoGroupListener): RecyclerView.Adapter<IncognitoGroupViewHolder>() {

    private val listData = mutableListOf<String>("","","","","","","","","","","","","","","","","","")

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): IncognitoGroupViewHolder {
        return IncognitoGroupViewHolder.create(parent, listener)
    }

    override fun onBindViewHolder(holder: IncognitoGroupViewHolder, position: Int) {
        holder.onBind()
    }

    override fun getItemCount(): Int {
        return  listData.size
    }
}