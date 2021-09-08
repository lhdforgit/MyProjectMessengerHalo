/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.editor.scribbles.widget

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.halo.editor.R
import com.halo.editor.scribbles.widget.ColorPaletteAdapter.ColorViewHolder
import java.util.*

class ColorPaletteAdapter : RecyclerView.Adapter<ColorViewHolder>() {

    private val colors: MutableList<Int> = ArrayList()
    private var eventListener: EventListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        return ColorViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_color, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        holder.bind(colors[position], eventListener)
    }

    override fun getItemCount(): Int {
        return colors.size
    }

    fun setColors(colors: Set<Int>) {
        this.colors.clear()
        this.colors.addAll(colors)
        notifyDataSetChanged()
    }

    fun setEventListener(eventListener: EventListener?) {
        this.eventListener = eventListener
        notifyDataSetChanged()
    }

    interface EventListener {
        fun onColorSelected(color: Int)
    }

    class ColorViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private var foreground: ImageView
                = itemView.findViewById(R.id.palette_item_foreground)
        fun bind(
            color: Int,
            eventListener: EventListener?
        ) {
            foreground.setColorFilter(color, PorterDuff.Mode.SRC_IN)
            itemView.setOnClickListener {
                eventListener?.onColorSelected(
                    color
                )
            }
        }
    }
}