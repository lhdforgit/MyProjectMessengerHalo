package com.halo.presentation.search.general.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.halo.R

class SearchEmptyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    companion object {
        fun getHolder(viewGroup: ViewGroup): SearchEmptyViewHolder {
            val inflater = LayoutInflater.from(viewGroup.context)
            val view = inflater.inflate(R.layout.search_main_empty_item, viewGroup, false)
            return SearchEmptyViewHolder(view)
        }
    }

}