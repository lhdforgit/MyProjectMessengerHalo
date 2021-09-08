/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.bubble.conversation.home.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.halo.data.common.paging.NetworkState

/**
 * @author ndn
 * Created by ndn
 * Created on 10/5/18
 */
interface ChatBaseAdapter<F> {
    fun onCreateChatHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder

    fun onCreateHeaderHolder(parent: ViewGroup): RecyclerView.ViewHolder

    fun onBindChatHolder(holder: RecyclerView.ViewHolder, f: F)

    fun onBindHeaderHolder(holder: RecyclerView.ViewHolder)

    fun getChatViewType(position: Int): Int

    fun getHeaderViewType(): Int

    fun hasExtraRow(): Boolean

    fun setNetworkState(newNetworkState: NetworkState?)
}