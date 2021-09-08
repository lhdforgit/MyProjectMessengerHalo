/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.sticker.sticker_group.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.giphy.sdk.core.models.Media
import com.halo.widget.sticker.R
import java.util.*

class GiphyAdapter(private val requestManager: RequestManager) :
    RecyclerView.Adapter<GiphyAdapter.GiphyItemHolder>() {
    private val mediaList: MutableList<Media> =
        ArrayList()
    private var width = 0
    fun updateList(newList: List<Media>?) {
        val diffCallback =
            GiphyCallBack(
                mediaList,
                newList ?: mutableListOf()
            )
        val diffResult =
            DiffUtil.calculateDiff(diffCallback)
        mediaList.clear()
        mediaList.addAll(newList ?: mutableListOf())
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): GiphyItemHolder {
        if (width != viewGroup.width && viewGroup.width != 0) {
            width = viewGroup.width
        }
        val inflater = LayoutInflater.from(viewGroup.context)
        val view = inflater.inflate(
            R.layout.layout_giphy_item,
            viewGroup, false
        )
        return GiphyItemHolder(view)
    }

    override fun onBindViewHolder(
        giphyItemHolder: GiphyItemHolder,
        i: Int
    ) {
        mediaList.takeIf { it.isNotEmpty() && it.size > i && i >= 0 }?.get(i)?.run {
            giphyItemHolder.onBind(this)
        }
    }

    override fun getItemCount(): Int {
        return mediaList.size
    }

    inner class GiphyItemHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView
        fun onBind(media: Media) {
            requestManager.asGif()
                .load(
                    String.format(
                        itemView.context.getString(R.string.media_giphy_url),
                        media.id
                    )
                )
                .apply(
                    RequestOptions()
                        .override(width / 3, width / 3 / 16 * 9)
                        .priority(Priority.HIGH)
                        .centerCrop()
                )
                .into(imageView)
            itemView.setOnClickListener {
                giphyListener?.itemOnClick(media)
            }
        }

        init {
            imageView = itemView.findViewById(R.id.item_giphy)
        }
    }

    inner class GiphyCallBack(
        private val oldList: List<Media>,
        private val newList: List<Media>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(i: Int, i1: Int): Boolean {
            return TextUtils.equals(oldList[i].id, newList[i1].id)
        }

        override fun areContentsTheSame(i: Int, i1: Int): Boolean {
            return TextUtils.equals(oldList[i].id, newList[i1].id)
        }

    }

    private var giphyListener: GiphyListener? =
        null

    fun setGiphyListener(giphyListener: GiphyListener?) {
        this.giphyListener = giphyListener
    }

    interface GiphyListener {
        fun itemOnClick(media: Media)
    }

}