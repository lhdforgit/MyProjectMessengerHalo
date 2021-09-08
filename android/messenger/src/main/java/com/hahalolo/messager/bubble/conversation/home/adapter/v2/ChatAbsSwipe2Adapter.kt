package com.hahalolo.messager.bubble.conversation.home.adapter.v2

import androidx.annotation.NonNull
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.hahalolo.messager.bubble.conversation.home.adapter.ChatAdapterListener
import com.hahalolo.messager.presentation.adapter.holder.NetworkStateListener
import com.hahalolo.swipe.SwipeLayout
import com.hahalolo.swipe.implments.SwipeItemMangerImpl
import com.hahalolo.swipe.interfaces.SwipeAdapterInterface
import com.hahalolo.swipe.interfaces.SwipeItemMangerInterface
import com.hahalolo.swipe.util.Attributes

abstract class ChatAbsSwipe2Adapter<T: Any> constructor(
        listener: ChatAdapterListener<T>,
        requestManager: RequestManager,
        preloadSizeProvider: ViewPreloadSizeProvider<T>,
        @NonNull diffCallback: DiffUtil.ItemCallback<T>,
        networkStateListener: NetworkStateListener
) : ChatAbs2Adapter<T>(diffCallback = diffCallback,
    requestManager = requestManager,
    listener = listener,
    preloadSizeProvider = preloadSizeProvider,
    networkStateListener = networkStateListener),
    SwipeItemMangerInterface,
    SwipeAdapterInterface{

    private var mItemManger = SwipeItemMangerImpl(this)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.itemView.findViewById<SwipeLayout>(getSwipeLayoutResourceId(position))?.let {
            mItemManger.bind(holder.itemView , position)
        }
    }

    override fun openItem(position: Int) {
        mItemManger.openItem(position)
    }

    override fun notifyDatasetChanged() {
        super.notifyDataSetChanged()
    }

    override fun closeItem(position: Int) {
        mItemManger.closeItem(position)
    }

    override fun closeAllExcept(layout: SwipeLayout) {
        mItemManger.closeAllExcept(layout)
    }

    override fun closeAllItems() {
        mItemManger.closeAllItems()
    }

    override fun getOpenItems(): List<Int> {
        return mItemManger.openItems
    }

    override fun getOpenLayouts(): List<SwipeLayout> {
        return mItemManger.openLayouts
    }

    override fun removeShownLayouts(layout: SwipeLayout) {
        mItemManger.removeShownLayouts(layout)
    }

    override fun isOpen(position: Int): Boolean {
        return mItemManger.isOpen(position)
    }

    override fun getMode(): Attributes.Mode {
        return mItemManger.mode
    }

    override fun setMode(mode: Attributes.Mode) {
        mItemManger.mode = mode
    }

}