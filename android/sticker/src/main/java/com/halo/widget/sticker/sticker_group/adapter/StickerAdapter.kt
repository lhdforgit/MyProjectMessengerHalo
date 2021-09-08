/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.sticker.sticker_group.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.halo.data.entities.mongo.tet.GifCard
import com.halo.widget.felling.model.StickerEntity
import com.halo.widget.sticker.R
import com.halo.widget.sticker.sticker_group.adapter.StickerAdapter.StickerItemHolder
import com.halo.widget.sticker.sticker_group.adapter.diff.GifCardCallBack
import com.halo.widget.sticker.sticker_group.adapter.diff.StickerCallBack

class StickerAdapter(private var requestManager: RequestManager?) :
    RecyclerView.Adapter<StickerItemHolder>() {
    private val stickerEntities: MutableList<StickerEntity> = mutableListOf()
    private val gifCards: MutableList<GifCard> = mutableListOf()

    fun updateList(newList: List<StickerEntity>) {
        val diffCallback = StickerCallBack(stickerEntities, newList)
        val diffResult =
            DiffUtil.calculateDiff(diffCallback)
        stickerEntities.clear()
        stickerEntities.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    fun updateListGifCards(gifCards: MutableList<GifCard>) {
        val diffCallback = GifCardCallBack(this.gifCards, gifCards)
        val diffResult =
            DiffUtil.calculateDiff(diffCallback)
        this.gifCards.clear()
        this.gifCards.addAll(gifCards)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): StickerItemHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val view = inflater.inflate(
            R.layout.layout_sticker_item,
            viewGroup, false
        )
        return StickerItemHolder(view)
    }

    override fun onBindViewHolder(
        stickerItemHolder: StickerItemHolder,
        i: Int
    ) {
        stickerEntities.takeIf { it.isNotEmpty() && it.size > i }?.get(i)?.run {
            stickerItemHolder.bind(this)
        }
        gifCards.takeIf { it.isNotEmpty() && it.size > i }?.get(i)?.run {
            stickerItemHolder.bind(this)
        }
    }

    override fun getItemCount(): Int {
        return stickerEntities.size + gifCards.size
    }

    inner class StickerItemHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView
        private val avLoading: View
        private fun loadImage(path: String) {
            avLoading.visibility = View.VISIBLE
            if (requestManager == null) {
                requestManager = Glide.with(itemView.context)
            }
            requestManager!!
                .load(path)
                .apply(
                    RequestOptions.placeholderOf(
                        ContextCompat.getDrawable(
                            imageView.context,
                            com.halo.widget.R.drawable.holder_rect
                        )
                    )
                )
                .apply(
                    RequestOptions.errorOf(
                        ContextCompat.getDrawable(
                            imageView.context,
                            com.halo.widget.R.drawable.holder_rect
                        )
                    )
                )
                .listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any,
                        target: Target<Drawable?>,
                        isFirstResource: Boolean
                    ): Boolean {
                        avLoading.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any,
                        target: Target<Drawable?>,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        avLoading.visibility = View.GONE
                        return false
                    }
                })
                .into(imageView)
        }

        fun bind(stickerEntity: StickerEntity) {
            imageView.scaleType = ImageView.ScaleType.FIT_CENTER
            loadImage(stickerEntity.image?:"")
            itemView.setOnClickListener {
                listener?.itemOnClick(stickerEntity)
            }
        }

        fun bind(gifCard: GifCard) {
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            loadImage(gifCard.path?:"")
            itemView.setOnClickListener {
                listener?.itemOnClick(gifCard)
            }
        }

        init {
            imageView = itemView.findViewById(R.id.image)
            avLoading = itemView.findViewById(R.id.av_loading)
        }
    }

    private var listener: StickerListener? = null
    fun setListener(listener: StickerListener?) {
        this.listener = listener
    }

    interface StickerListener {
        fun itemOnClick(stickerEntity: StickerEntity)
        fun itemOnClick(gifCard: GifCard)
    }
}