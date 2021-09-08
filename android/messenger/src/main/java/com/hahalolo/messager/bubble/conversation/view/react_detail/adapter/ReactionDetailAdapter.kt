/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.bubble.conversation.view.react_detail.adapter

import android.text.TextUtils
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.hahalolo.messager.bubble.conversation.view.react_detail.listener.ChatReactionDetailListener
import com.hahalolo.messager.bubble.conversation.view.react_detail.listener.Constant
import com.hahalolo.messenger.R
import com.halo.data.room.entity.MemberEntity
import com.halo.widget.ErrorHideEmptyHolder

class ReactionDetailAdapter(
    private var listReaction:MutableList<MemberEntity>? = null,
    val requestManager: RequestManager
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var listener: ChatReactionDetailListener? = null
    private var typeReact: String = Constant.ALL_REACT

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == R.layout.chat_reaction_detail_item) {
            ReactionDetailViewHolder.createViewHolder(
                parent,
                requestManager
            )
        } else {
            ErrorHideEmptyHolder.build(parent)
        }
    }

    override fun getItemCount(): Int {
        return listReaction?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        listReaction?.get(position)?.let {
            when (holder) {
                is ReactionDetailViewHolder -> {
                    holder.onBind(it, typeReact)
                }
                is ErrorHideEmptyHolder -> {
                    holder.bind()
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        listReaction?.get(position)?.let {
            if (!TextUtils.isEmpty(it.userId())) {
                return R.layout.chat_reaction_detail_item
            }
        }
        return R.layout.error_hide_empty_holder
    }

    fun updateData(listMember: MutableList<MemberEntity>, typeReact: String?) {
        this.listReaction = listMember
        typeReact?.let {
            this.typeReact = it
        }
        notifyDataSetChanged()
    }

    fun setListener(listener: ChatReactionDetailListener?) {
        this.listener = listener
    }

}