/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.presentation.search.group.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hahalolo.messager.chatkit.commons.ImageLoader
import com.hahalolo.messager.presentation.adapter.holder.NetworkStateHolder
import com.hahalolo.messager.presentation.adapter.holder.NetworkStateRetryHolder
import com.halo.data.room.entity.ChannelEntity
import com.hahalolo.messenger.R
import com.halo.data.common.paging.NetworkState
import java.util.*

/**
 * @author BuiSon
 * Created by BuiSon
 * Created on 10/23/2018.
 */
class ChatSearchGroupAdapter(private val imageLoader: ImageLoader,
                             private val listener: ChatSearchGroupListener
)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var networkState: NetworkState? = null

    private var roomSockets: List<ChannelEntity> = ArrayList()

    fun submitList(roomSockets: List<ChannelEntity>) {
        this.roomSockets = roomSockets
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val viewHolder: RecyclerView.ViewHolder
        if (viewType == R.layout.chat_message_search_group_item) {
            viewHolder = ChatSearchGroupViewHolder.getHolder(viewGroup, imageLoader, listener)
        } else if (viewType == R.layout.message_network_state_retry_item) {
            viewHolder = NetworkStateRetryHolder.createHolder(viewGroup, null)
        } else {
            viewHolder = NetworkStateHolder.createHolder(viewGroup)
        }
        return viewHolder
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        if (viewHolder is ChatSearchGroupViewHolder && i >= 0 && i < roomSockets.size) {
            viewHolder.bind(Objects.requireNonNull(roomSockets[i]))
        } else if (viewHolder is NetworkStateHolder) {
            viewHolder.bind()
        } else if (viewHolder is NetworkStateRetryHolder) {
            viewHolder.bind(networkState)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            R.layout.message_network_state_retry_item
        } else {
            R.layout.chat_message_search_group_item
        }
    }

    override fun getItemCount(): Int {
        return roomSockets.size + if (hasExtraRow()) 1 else 0
    }

    private fun hasExtraRow(): Boolean {
        return networkState != null && networkState!!.status != NetworkState.Status.SUCCESS
    }

    fun setNetworkState(newNetworkState: NetworkState) {
        val previousState = this.networkState
        val previousExtraRow = hasExtraRow()
        this.networkState = newNetworkState
        val newExtraRow = hasExtraRow()
        if (previousExtraRow != newExtraRow) {
            if (previousExtraRow) {
                notifyItemRemoved(itemCount)
            } else {
                notifyItemInserted(itemCount)
            }
        } else if (newExtraRow && previousState !== newNetworkState) {
            notifyItemChanged(itemCount - 1)
        }
    }
}