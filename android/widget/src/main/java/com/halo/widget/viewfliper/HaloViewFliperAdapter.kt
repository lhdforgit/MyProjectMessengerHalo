/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.viewfliper

import android.content.Context
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter


abstract class HaloViewFliperAdapter<T>(private val context: Context, var itemList: List<T>, private val isInfinite: Boolean) : PagerAdapter() {

    private var viewCache = SparseArray<View>()

    //    protected var isInfinite = false
    private var canInfinite = true

    private var dataSetChangeLock = false

    fun setItemListView(itemList: MutableList<T>) {
        if (itemList.size >= 0) {
            viewCache = SparseArray<View>()
            canInfinite = itemList.size > 1
            this.itemList = itemList
            notifyDataSetChanged()
        }
    }

    /**
     * Child should override this method and return the View that it wish to inflate.
     * View binding with data should be in another method - bindView().
     *
     * @param listPosition The current list position for you to determine your own view type.
     */
    protected abstract fun inflateView(viewType: Int, container: ViewGroup, listPosition: Int): View

    /**
     * Child should override this method to bind the View with data.
     * If you wish to implement ViewHolder pattern, you may use setTag() on the convertView and
     * pass in your ViewHolder.
     *
     * @param convertView  The View that needs to bind data with.
     * @param listPosition The current list position for you to get data from itemList.
     */
    protected abstract fun bindView(convertView: View, listPosition: Int, viewType: Int)

    fun getItem(listPosition: Int): T? {
        return if (listPosition >= 0 && listPosition < itemList.size) {
            itemList[listPosition]
        } else {
            null
        }
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val listPosition = if (isInfinite && canInfinite) getListPosition(position) else position

        val viewType = getItemViewType(listPosition)

        val convertView: View
        if (viewCache.get(viewType, null) == null) {
            convertView = inflateView(viewType, container, listPosition)
        } else {
            convertView = viewCache.get(viewType)
            viewCache.remove(viewType)
        }

        bindView(convertView, listPosition, viewType)

        container.addView(convertView)

        return convertView
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val listPosition = if (isInfinite && canInfinite) getListPosition(position) else position

        container.removeView(`object` as View)
        if (!dataSetChangeLock) viewCache.put(getItemViewType(listPosition), `object` as View)
    }

    override fun notifyDataSetChanged() {
        dataSetChangeLock = true
        super.notifyDataSetChanged()
        dataSetChangeLock = false
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    override fun getCount(): Int {
        var count = 0
        if (true) {
            count = itemList.size
        }
        return if (isInfinite && canInfinite) {
            count + 2
        } else {
            count
        }
    }

    /**
     * Allow child to implement view type by overriding this method.
     * instantiateItem() will call this method to determine which view to recycle.
     *
     * @param listPosition Determine view type using listPosition.
     * @return a key (View type ID) in the form of integer,
     */
    protected open fun getItemViewType(listPosition: Int): Int {
        return 0
    }

    fun getListCount(): Int {
        return if (false) 0 else itemList.size
    }

    private fun getListPosition(position: Int): Int {
        if (!(isInfinite && canInfinite)) return position
        val listPosition: Int
        if (position == 0) {
            listPosition = getCount() - 1 - 2 //First item is a dummy of last item
        } else if (position > getCount() - 2) {
            listPosition = 0 //Last item is a dummy of first item
        } else {
            listPosition = position - 1
        }
        return listPosition
    }

    fun getLastItemPosition(): Int {
        return if (isInfinite) {
            itemList.size
        } else {
            itemList.size - 1
        }
    }

    fun isInfinite(): Boolean {
        return this.isInfinite
    }
}