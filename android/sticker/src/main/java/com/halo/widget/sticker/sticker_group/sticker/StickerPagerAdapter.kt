/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.sticker.sticker_group.sticker

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.RequestManager
import com.halo.widget.felling.model.StickerPackEntity
import com.halo.widget.room.table.StickerPackTable
import com.halo.widget.sticker.sticker_group.StickerGroupView.StickerGroupListener
import com.halo.widget.sticker.sticker_group.sticker.pack.StickerPackView

class StickerPagerAdapter(
    private val requestManager: RequestManager
) : PagerAdapter() {
    private var stickerPackEntities: MutableList<StickerPackTable> = mutableListOf()

    fun updateList(list: MutableList<StickerPackTable>){
        stickerPackEntities = list
        notifyDataSetChanged()
    }

    private var listener: StickerGroupListener? = null
    private var onItemTouchListener: RecyclerView.OnItemTouchListener? = null

    fun setOnItemTouchListener(onItemTouchListener: RecyclerView.OnItemTouchListener?) {
        this.onItemTouchListener = onItemTouchListener
    }

    fun setListener(listener: StickerGroupListener?) {
        this.listener = listener
    }

    override fun getCount(): Int {
        return stickerPackEntities.size
    }

    override fun isViewFromObject(view: View, o: Any): Boolean {
        return view === o
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val pack =  stickerPackEntities.get(position)
        val view: View = StickerPackView(
            container.context
        )
        (view as StickerPackView).setListener(listener)
        (view as StickerPackView).setRequestManager(requestManager)
        (view as StickerPackView).setStickerPackTable(stickerPackEntities.get(position))
        onItemTouchListener?.run {
            view.setOnItemTouchListener(this)
        }
        container.addView(view)
        return view
    }

    override fun destroyItem(
        container: ViewGroup,
        position: Int,
        `object`: Any
    ) {
        container.removeView(`object` as View)
    }

    fun getPageIcon(position: Int): String {
        return  stickerPackEntities
            .takeIf {
                it.isNotEmpty()
                        && it.size > position
                        && position >= 0
            }
            ?.get(position)
            ?.packIcon ?: ""
    }
}