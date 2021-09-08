/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.messager.chatkit.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.google.common.collect.Iterators
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.MessageGalleryInputViewBinding
import com.halo.widget.gallery.MediaAdapterV2
import com.halo.widget.gallery.MediaListenerV2
import com.halo.widget.gallery.MediaStoreData
import com.halo.widget.recyclerview.RecyclerViewPreloader

class MessageGalleryInputView : RelativeLayout {
    constructor(context: Context?) : super(context) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
        initView()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        initView()
    }

    private lateinit var binding: MessageGalleryInputViewBinding

    var listener: Listener? = null

    private val MAX_ITEM = 5

    private val listSelected = mutableListOf<MediaStoreData>()

    private val mediaListenerV2 = object : MediaListenerV2 {
        override fun isMaxItem(): Boolean {
            return (listSelected.size == MAX_ITEM)
        }

        override fun onChecked(data: MediaStoreData?, checked: Boolean): Boolean {
            var result = false
            data?.run {
                val indext = Iterators.indexOf(listSelected.iterator()) {
                    it?.rowId == this.rowId
                }
                if (checked && indext < 0 && listSelected.size < MAX_ITEM) {
                    listSelected.add(this)
                    if (isMaxItem) {
                        binding.listMedia.adapter?.notifyDataSetChanged()
                    }
                    result = true
                } else if (!checked && indext >= 0) {
                    listSelected.removeAt(indext)
                    if (listSelected.size == MAX_ITEM - 1) {
                        binding.listMedia.adapter?.notifyDataSetChanged()
                    }
                    result = true
                } else {

                }
                checkListSelected()
            }
            return result
        }

        override fun isSelected(rowId: Long): Boolean {
            return Iterators.indexOf(listSelected.iterator()) {
                it?.rowId == rowId
            } >= 0
        }
    }

    private fun initView() {
        binding = MessageGalleryInputViewBinding.inflate(LayoutInflater.from(context))
        addView(
            binding.root,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        bindAction()
    }

    private fun bindAction() {
        binding.btnSend.setOnClickListener {
            listSelected.takeIf { it.isNotEmpty() }?.run {
                listener?.onSendMedia(this)
                clearSelected()
            } ?: run {
                checkListSelected()
            }
        }

        binding.listMedia.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

                listener?.onTouchEvent(e)
            }

            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {

                return listener?.onTouchScroll(e) ?: false
            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

            }
        })
    }

    private fun checkListSelected() {
        binding.btnSend.setText(
            if (listSelected.size > 1) {
                if (listSelected.size == MAX_ITEM) {
                    context.getString(
                        R.string.chat_message_gallery_input_send_max_item,
                        listSelected.size
                    )
                } else {
                    context.getString(
                        R.string.chat_message_gallery_input_sent_item,
                        listSelected.size
                    )
                }
            } else context.getString(R.string.chat_message_gallery_input_send_one_item)
        )
        binding.btnSend.visibility = if (listSelected.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun clearSelected() {
        listSelected.clear()
        binding.listMedia.adapter?.notifyDataSetChanged()
        checkListSelected()
    }

    fun setAdapter(
        requestManager: RequestManager,
        mediaStoreData: List<MediaStoreData>?
    ) {
        val listSorted = mediaStoreData?.sortedBy {
            -it.dateModified
        }
        val adapter = MediaAdapterV2(listSorted, requestManager, mediaListenerV2)
        val preloader = RecyclerViewPreloader(requestManager, adapter, adapter, 3)
        binding.listMedia.layoutManager = GridLayoutManager(context, 3)
        binding.listMedia.addOnScrollListener(preloader)
        binding.listMedia.itemAnimator = null
        binding.listMedia.adapter = adapter
        val isEmpty = listSorted.isNullOrEmpty()
        binding.layoutEmpty.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }


    interface Listener {
        fun onSendMedia(list: MutableList<MediaStoreData>)
        fun onTouchScroll(e: MotionEvent?): Boolean
        fun onTouchEvent(e: MotionEvent)
    }
}