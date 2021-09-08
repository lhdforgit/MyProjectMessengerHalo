/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.sticker.sticker_group.sticker.pack

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.halo.data.entities.mongo.tet.GifCard
import com.halo.widget.HaloGridLayoutManager
import com.halo.widget.felling.model.StickerEntity
import com.halo.widget.felling.repository.EditorFeelingRepositoryImpl
import com.halo.widget.room.table.StickerPackTable
import com.halo.widget.sticker.R
import com.halo.widget.sticker.sticker_group.StickerGroupView.StickerGroupListener
import com.halo.widget.sticker.sticker_group.adapter.StickerAdapter
import com.halo.widget.sticker.sticker_group.adapter.StickerAdapter.StickerListener

class StickerPackView : FrameLayout {
    private var root: View? = null
    private var recyclerView: RecyclerView? = null
    private var stickerAdapter: StickerAdapter? = null
    private var requestManager: RequestManager? = null
    private var stickerPackEntity: StickerPackTable? = null
    private var gifCards: MutableList<GifCard>? = null
    private var listener: StickerGroupListener? = null
    private var onItemTouchListener: RecyclerView.OnItemTouchListener? = null

    fun setOnItemTouchListener(onItemTouchListener: RecyclerView.OnItemTouchListener?) {
        this.onItemTouchListener = onItemTouchListener
    }

    fun setListener(listener: StickerGroupListener?) {
        this.listener = listener
    }

    fun setRequestManager(requestManager: RequestManager?) {
        this.requestManager = requestManager
    }

    fun setStickerPackTable(table: StickerPackTable) {
        this.stickerPackEntity = table
        listener?.run {
            this.stickerRepository()?.stickers(this.token(),  table.id)?.observe(this.lifecycleOwner()) {
                stickerAdapter?.updateList(it.map {
                    StickerEntity(
                        id = it.id,
                        code = it.id,
                        image = it.stickerUrl,
                        type = it.stickerType.toString(),
                        urlFull = it.stickerUrl,
                    )
                })
            }
        }
    }

    constructor(
        context: Context
    ) : super(context) {
        initView()
    }

    constructor(
        context: Context,
        requestManager: RequestManager?
    ) : super(context) {
        this.requestManager = requestManager
        initView()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        initView()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        initView()
    }

    private fun initView() {
        root = LayoutInflater.from(context).inflate(R.layout.layout_sticker_pack, null)
        recyclerView = root?.findViewById(R.id.sticker_rec)
        this.addView(root)
    }

    private fun initRec() {
        stickerAdapter = StickerAdapter(requestManager)
        recyclerView?.layoutManager =
            HaloGridLayoutManager(context, if (stickerPackEntity == null) 3 else 4)
        recyclerView?.adapter = stickerAdapter
        stickerAdapter?.setListener(object : StickerListener {
            override fun itemOnClick(stickerEntity: StickerEntity) {
                listener?.run {
                    EditorFeelingRepositoryImpl.getInstance(context)
                        .addLastStickerEntity(stickerEntity)
                    this.onClickStickerItem(stickerEntity)
                }
            }

            override fun itemOnClick(gifCard: GifCard) {
                listener?.onClickGifCardItem(gifCard)
            }
        })
        onItemTouchListener?.run {
            recyclerView?.addOnItemTouchListener(this)
        }
    }

    fun updateGifCards(gifCards: MutableList<GifCard>) {
        stickerAdapter?.updateListGifCards(gifCards)
    }

    private fun updateRec() {
        gifCards?.takeIf { it.isNotEmpty() }?.run {
            stickerAdapter?.updateListGifCards(this)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        initRec()
    }

    override fun onWindowVisibilityChanged(visibility: Int) {
        super.onWindowVisibilityChanged(visibility)
        if (visibility == View.VISIBLE) {
            updateRec()
        }
    }
}