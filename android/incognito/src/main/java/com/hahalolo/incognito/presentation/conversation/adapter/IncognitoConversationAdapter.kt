package com.hahalolo.incognito.presentation.conversation.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hahalolo.incognito.presentation.conversation.adapter.model.IncognitoMsgDiffCallback
import com.hahalolo.incognito.presentation.conversation.adapter.model.IncognitoMsgModel
import com.hahalolo.incognito.presentation.conversation.adapter.viewholder.IncognitoMsgViewHolder
import com.hahalolo.incognito.presentation.conversation.adapter.viewholder.IncognitoMsgViewType
import com.halo.widget.sticky_header.StickyRecyclerHeadersAdapter

class IncognitoConversationAdapter (private val listener:IncognitoConversationListener)
    : RecyclerView.Adapter<IncognitoMsgViewHolder>(),
    StickyRecyclerHeadersAdapter<IncognitoMsgViewHolder> {

    private val listData = mutableListOf<IncognitoMsgModel>()

    fun submitList(list: MutableList<String>) {
        val newList = mutableListOf<IncognitoMsgModel>()
        list.forEachIndexed { index, data ->
            val before = list.takeIf { (index-1)>= 1 && (index-1) <it.size }?.get(index-1)
            val after = list.takeIf { (index+1)>= 0 && (index+1) <it.size }?.get(index+1)
            newList.add(IncognitoMsgModel(data, before, after)).apply {


            }
        }
        val diffCallback = IncognitoMsgDiffCallback(this.listData, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listData.clear()
        this.listData.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncognitoMsgViewHolder {
        return IncognitoMsgViewHolder.create(parent, viewType, listener)
    }

    override fun onBindViewHolder(holder: IncognitoMsgViewHolder, position: Int) {
        getItem(position)?.run {
            holder.onBind(this)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return listData[position].viewType()
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    private fun getItem(position: Int):IncognitoMsgModel? {
        return listData.takeIf { position>=0 && position<it.size }?.get(position)
    }

    /*HEADER DATE*/
    override fun getHeaderId(position: Int): Long {
        return getItem(position)?.timeId()?:-1
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup?): IncognitoMsgViewHolder {
        return IncognitoMsgViewHolder.create(
            parent!!,
            IncognitoMsgViewType.VIEW_TYPE_DATE,
            listener
        )
    }

    override fun onBindHeaderViewHolder(holder: IncognitoMsgViewHolder?, position: Int) {
        getItem(position)?.run {
            (holder as? IncognitoMsgViewHolder.Date)?.onBind(this )
        }
    }
}