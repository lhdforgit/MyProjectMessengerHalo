package com.halo.widget.sticker.sticker_group.adapter.diff

import android.text.TextUtils
import androidx.recyclerview.widget.DiffUtil
import com.halo.data.entities.mongo.tet.GifCard

class GifCardCallBack (
    private val oldList: List<GifCard>,
    private val newList: List<GifCard>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(i: Int, i1: Int): Boolean {
        return TextUtils.equals(oldList[i].path, newList[i1].path)
    }

    override fun areContentsTheSame(i: Int, i1: Int): Boolean {
        return TextUtils.equals(oldList[i].path, newList[i1].path)
    }

}