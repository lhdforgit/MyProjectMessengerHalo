package com.hahalolo.incognito.presentation.main.contact.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class IncognitoContactAdapter(private val listener: IncognitoContactListener) : RecyclerView.Adapter<IncognitoContactViewHolder>() {

    private val listData = mutableListOf<String>("","","","","","")


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): IncognitoContactViewHolder {
        return IncognitoContactViewHolder.create(parent, listener)
    }

    override fun onBindViewHolder(holder: IncognitoContactViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return  listData.size
    }


}