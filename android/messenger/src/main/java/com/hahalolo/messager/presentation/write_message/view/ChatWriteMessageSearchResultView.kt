package com.hahalolo.messager.presentation.write_message.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.hahalolo.messager.presentation.main.contacts.ChatContactsListener
import com.hahalolo.messager.presentation.write_message.search.ChatWriteSearchAdapter
import com.hahalolo.messager.presentation.write_message.search.ChatWriteSearchDiff
import com.hahalolo.messager.presentation.write_message.search.ChatWriteSearchInterface
import com.hahalolo.messager.utils.Strings
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatWriteMessageSearchResultViewBinding
import com.halo.common.utils.RecyclerViewUtils
import com.halo.data.entities.user.User
import com.halo.widget.HaloLinearLayoutManager

class ChatWriteMessageSearchResultView : FrameLayout {
    private var binding: ChatWriteMessageSearchResultViewBinding? = null
    private var adapter: ChatWriteSearchAdapter? = null
    private val requestManager by lazy { Glide.with(this) }

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
        val layoutInflater = LayoutInflater.from(context)
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.chat_write_message_search_result_view,
            null,
            false
        )
        addView(
            binding?.root,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    fun initView() {
        binding?.apply {
            userRec.layoutManager = HaloLinearLayoutManager(context)
            RecyclerViewUtils.optimization(userRec, this@ChatWriteMessageSearchResultView)
            val viewPreloadSizeProvider = ViewPreloadSizeProvider<User>()
            adapter = ChatWriteSearchAdapter(
                false,
                ChatWriteSearchDiff(),
                viewPreloadSizeProvider,
                requestManager
            )
            userRec.adapter = adapter
        }
    }

    fun submitList(users: MutableList<User>) {
        adapter?.submitList(users)
    }

    fun notifyItemChanged(position: Int) {
        adapter?.notifyItemChanged(position)
    }

    val currentList: List<User>?
    get() {
        return adapter?.currentList()
    }
}