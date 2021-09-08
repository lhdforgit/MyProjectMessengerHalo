package com.hahalolo.messager.presentation.main.conversation.adapter

import android.text.TextUtils
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.hahalolo.messager.MessengerController
import com.hahalolo.messenger.R
import com.hahalolo.swipe.SwipeLayout
import com.hahalolo.swipe.implments.SwipeItemMangerImpl
import com.hahalolo.swipe.interfaces.SwipeAdapterInterface
import com.hahalolo.swipe.interfaces.SwipeItemMangerInterface
import com.hahalolo.swipe.util.Attributes
import com.halo.data.room.entity.ChannelEntity

/**
 * Create by ndn
 * Create on 8/26/20
 * com.halo.presentation.wall.community.wall.member
 */
class ConversationAdapter(
    private val appController: MessengerController,
    private val requestManager: RequestManager,
    private val listener: ConversationListener,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    SwipeItemMangerInterface,
    SwipeAdapterInterface {

    private var listData = mutableListOf<ChannelEntity>()

    fun submitList(newList: MutableList<ChannelEntity>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return listData.size
            }

            override fun getNewListSize(): Int {
                return newList.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return TextUtils.equals(
                    listData[oldItemPosition].channelId(),
                    newList[newItemPosition].channelId()
                )
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return false
            }
        }
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listData.clear()
        this.listData.addAll(newList)
        diffResult.dispatchUpdatesTo(AdapterUpdateCallback(this))
    }

    class AdapterUpdateCallback(private val adapter: RecyclerView.Adapter<*>) :
        ListUpdateCallback {

        fun offsetPosition(originalPosition: Int): Int {
            return originalPosition
        }

        override fun onInserted(position: Int, count: Int) {
            adapter.notifyItemRangeInserted(offsetPosition(position), count)
        }

        override fun onRemoved(position: Int, count: Int) {
            adapter.notifyItemRangeRemoved(offsetPosition(position), count)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            adapter.notifyItemMoved(offsetPosition(fromPosition), offsetPosition(toPosition))
        }

        override fun onChanged(position: Int, count: Int, payload: Any?) {
            adapter.notifyItemRangeChanged(offsetPosition(position), count, payload)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return ConversationViewHolder.create(
            parent,
            listener = listener,
            swipeListener = this,
            userIdToken = appController.ownerId,
            appLang = appController.appLang()
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ConversationViewHolder) {
            listData.getOrNull(position)?.run {
                holder.bind(this)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.message_conversation_item
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    private var mItemManger = SwipeItemMangerImpl(this)

    override fun openItem(position: Int) {
        mItemManger.openItem(position)
    }

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.swipe
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
