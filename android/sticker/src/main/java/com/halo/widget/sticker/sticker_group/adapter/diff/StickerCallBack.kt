package com.halo.widget.sticker.sticker_group.adapter.diff

import android.text.TextUtils
import androidx.recyclerview.widget.DiffUtil
import com.halo.widget.felling.model.StickerEntity

class StickerCallBack(
    private val oldList: List<StickerEntity>,
    private val newList: List<StickerEntity>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(i: Int, i1: Int): Boolean {
        return TextUtils.equals(oldList[i].id, newList[i1].id)
    }

    override fun areContentsTheSame(i: Int, i1: Int): Boolean {
        return TextUtils.equals(oldList[i].id, newList[i1].id)
    }

}