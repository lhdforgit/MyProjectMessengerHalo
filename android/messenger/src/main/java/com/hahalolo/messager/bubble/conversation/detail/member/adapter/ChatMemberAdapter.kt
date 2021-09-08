/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.messager.bubble.conversation.detail.member.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.halo.data.room.entity.MemberEntity
import com.halo.widget.ErrorHideEmptyHolder

class ChatMemberAdapter(
    val requestManager: RequestManager,
    val listener: ChatMemberListener,
    var isMember: Boolean
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val ITEM_MEMBER_ERROR = 111
    }

    private var listData = mutableListOf<MemberEntity>()

    fun updateList(newList: MutableList<MemberEntity>) {
        val diffResult: DiffUtil.DiffResult =
            DiffUtil.calculateDiff(ChatMemberDiffCallback(listData, newList))
        listData.clear()
        this.listData.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == ITEM_MEMBER_ERROR) {
            return ErrorHideEmptyHolder.build(parent)
        }
        return ChatMemberViewHolder.create(parent, requestManager, listener)
    }

    override fun getItemViewType(position: Int): Int {
        listData.takeIf { it.isNotEmpty() && it.size > position }
            ?.get(position)
            ?.takeIf { it.isActive() }
            ?.run {
                return super.getItemViewType(position)
            }
        return ITEM_MEMBER_ERROR
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        listData.takeIf {
            holder is ChatMemberViewHolder
                    && it.isNotEmpty()
                    && it.size > position
        }
            ?.get(position)
            ?.run {
                (holder as ChatMemberViewHolder).onBind(this, isMember)
            }
    }

    override fun getItemCount(): Int {
        return listData.size
    }
}