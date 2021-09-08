package com.hahalolo.incognito.presentation.create.forward.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hahalolo.incognito.databinding.IncognitoBlankViewHolderItemBinding

class BlankViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bind() {

    }
    companion object {
        @JvmStatic
        fun createHolder(viewGroup: ViewGroup): BlankViewHolder {
            val layoutInflater = LayoutInflater.from(viewGroup.context)
            val binding =
                IncognitoBlankViewHolderItemBinding.inflate(layoutInflater, viewGroup, false)

            return BlankViewHolder(binding.root)
        }
    }
}