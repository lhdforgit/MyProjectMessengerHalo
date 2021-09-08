package com.hahalolo.messager.chatkit.view.mention

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.hahalolo.messager.chatkit.view.mention.adapter.ChatMentionAdapter
import com.hahalolo.messager.chatkit.view.mention.adapter.ChatMentionClickListener
import com.halo.data.room.entity.MemberEntity
import com.hahalolo.messenger.databinding.ChatMentionViewBinding

class ChatMentionView : FrameLayout {
    constructor(context: Context) : super(context) {initView()}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {initView()}
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    private lateinit var binding: ChatMentionViewBinding

    private var adapter: ChatMentionAdapter ?= null

    private var listener: ChatMentionListener?=null

    private var requestManager : RequestManager?= null

    fun setRequestManager(requestManager: RequestManager?){
        this.requestManager = requestManager
        initAdapter()
    }

    fun setChatMentionListener(listener: ChatMentionListener?){
        this.listener = listener
        initHandleMentions()
    }

    private fun initView(){
        binding = ChatMentionViewBinding.inflate(LayoutInflater.from(context), this, false)
        addView(binding.root,ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }


    private fun initAdapter(){
        adapter = ChatMentionAdapter(requestManager?: Glide.with(context), object :
            ChatMentionClickListener {
            override fun onClickMember(member: MemberEntity) {
                listener?.onMentionMember(member)
            }
        })
        binding.listItem.layoutManager = LinearLayoutManager(context)
        binding.listItem.adapter = adapter
    }

    private fun initHandleMentions(){
        listener?.observerList {
            val list = it ?: mutableListOf()

            listener?.queryMention()?.takeIf { MemberEntity.TAG_ALL.contains(it) }?.run {
                // thêm item mention all
//                list.add(0, MemberEntity.itemAll())
            }

            list.takeIf { it.isNotEmpty() }?.run {
                binding.layoutContainer.visibility = View.VISIBLE
                adapter?.updateList(this)
            } ?: run {
                binding.layoutContainer.visibility = View.GONE
            }
        }
    }

    interface ChatMentionListener{
        fun observerList(observer: Observer<MutableList<MemberEntity>>)
        fun onMentionMember(member: MemberEntity)
        fun queryMention():String?
    }
}