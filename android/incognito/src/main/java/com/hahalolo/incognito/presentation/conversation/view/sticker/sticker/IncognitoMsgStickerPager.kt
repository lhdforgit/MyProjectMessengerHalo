package com.hahalolo.incognito.presentation.conversation.view.sticker.sticker

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.hahalolo.incognito.presentation.conversation.view.sticker.IncognitoMessageStickerListener
import com.halo.widget.room.table.StickerPackTable

class IncognitoMsgStickerPager(
    private val listener: IncognitoMessageStickerListener?
) : PagerAdapter(){
    private var packs =  mutableListOf<StickerPackTable>()

    fun submit(packs: MutableList<StickerPackTable>){
        this.packs = packs
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return packs.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return `object` == view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as? View)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = IncognitoMsgStickerPackView(container.context)
        listener?.run{
            view.updateListener(this)
        }
        view.updateSticker(packs[position])
        container.addView(view)
        return view
    }

    fun getPageIcon(position: Int): String {
        return packs.takeIf { it.isNotEmpty() && position<it.size }?.get(position)?.packUrl?:""
    }

}