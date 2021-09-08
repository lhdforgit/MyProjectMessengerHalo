/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.bubble.conversation.detail.friend.select

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.RequestManager
import com.hahalolo.messager.bubble.conversation.detail.friend.select.adapter.FriendChooseListener
import com.hahalolo.messager.bubble.conversation.detail.friend.select.adapter.FriendSelectAdapter
import com.hahalolo.messager.bubble.conversation.detail.friend.select.adapter.FriendSelectData
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatCreateGroupUserSelectedViewBinding
import com.halo.widget.HaloLinearLayoutManager

class ChatGroupUserSelectView : LinearLayout {
    private var binding: ChatCreateGroupUserSelectedViewBinding? = null
    private var adapter: FriendSelectAdapter? = null
    var listener: FriendChooseListener? = null
    var requestManager: RequestManager? = null
    var isAddMember = false

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context)
    }

    private fun initView(context: Context) {
        val inflater = LayoutInflater.from(context)
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.chat_create_group_user_selected_view,
            null,
            false
        )
        addView(binding?.root)
    }

    fun bind(data: List<FriendSelectData>) {
        binding?.run {
            memberRec.animation = null
            memberRec.setHasFixedSize(true)
            memberRec.layoutManager =
                HaloLinearLayoutManager(context, HaloLinearLayoutManager.HORIZONTAL)
            if (adapter == null) adapter = FriendSelectAdapter(listener, requestManager)
            memberRec.adapter = adapter
        }
        adapter?.updateList(data)
        binding?.isAddMember = isAddMember
        binding?.selectedGr?.setOnClickListener { }
        binding?.createGroupBt?.setOnClickListener {
            binding?.groupNameEdt?.text?.toString()?.trim()?.let { groupName ->
                listener?.onCreateGroup(groupName)
            }
        }
        data.takeIf { it.isNotEmpty() }?.run {
            binding?.memberRec?.scrollToPosition(size - 1)
        }
    }
}