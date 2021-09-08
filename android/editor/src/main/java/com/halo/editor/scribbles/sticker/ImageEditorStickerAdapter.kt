/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.editor.scribbles.sticker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.halo.editor.R
import com.halo.editor.databinding.ImageEditorStickerItemBinding
import com.halo.widget.felling.model.StickerPackEntity
import com.halo.widget.room.table.StickerPackTable
import com.halo.widget.sticker.sticker_group.StickerGroupView
import com.halo.widget.sticker.sticker_group.sticker.pack.StickerPackView

/**
 * Create by ndn
 * Create on 5/4/20
 * com.halo.editor.scribbles.sticker
 */
class ImageEditorStickerAdapter(
    val requestManager: RequestManager,
    val listener: StickerGroupView.StickerGroupListener
) : RecyclerView.Adapter<StickerViewHolder>() {

    var packs: List<StickerPackEntity> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StickerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return StickerViewHolder(
            DataBindingUtil.inflate(
                inflater,
                R.layout.image_editor_sticker_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return packs.size
    }

    override fun onBindViewHolder(holder: StickerViewHolder, position: Int) {
        if (packs.isNotEmpty() && position >= 0 && position < packs.size) {
            holder.bind(packs[position], requestManager, listener)
        }
    }
}

class StickerViewHolder
internal constructor(
    private val binding: ImageEditorStickerItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    internal fun bind(
        pack: StickerPackEntity,
        requestManager: RequestManager?,
        listener: StickerGroupView.StickerGroupListener
    ) {
        binding.container.removeAllViews()
        val view = StickerPackView(
            binding.root.context
        )
        view.setRequestManager(requestManager?: Glide.with(binding.root.context))
        view.setListener(listener)
        view.setStickerPackTable(StickerPackTable(
            pack.id?:""
        ) .apply{
            packIcon = pack.image
            packUrl = pack.image
            packName = pack.name
        })
        binding.container.addView(view)
        binding.executePendingBindings()
    }
}