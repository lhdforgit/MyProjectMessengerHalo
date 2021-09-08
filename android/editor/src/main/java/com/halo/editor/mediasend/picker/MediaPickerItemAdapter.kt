/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.editor.mediasend.picker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.halo.editor.R
import com.halo.editor.databinding.MediapickerMediaItemBinding
import com.halo.editor.mediasend.Media
import com.halo.editor.mediasend.picker.MediaPickerItemAdapter.ItemViewHolder
import com.halo.editor.util.MediaUtil
import com.halo.editor.util.StableIdGenerator
import java.util.*

class MediaPickerItemAdapter(
    private val glideRequests: RequestManager,
    private val eventListener: EventListener,
    maxSelection: Int
) : RecyclerView.Adapter<ItemViewHolder>() {

    private val media: MutableList<Media>
    private val selected: MutableList<Media>
    private val maxSelection: Int
    private val stableIdGenerator: StableIdGenerator<Media>
    private var forcedMultiSelect = false

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ItemViewHolder {
        return ItemViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(viewGroup.context),
                R.layout.mediapicker_media_item,
                viewGroup, false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, i: Int) {
        holder.bind(
            media[i],
            forcedMultiSelect,
            selected,
            maxSelection,
            glideRequests,
            eventListener
        )
    }

    override fun onViewRecycled(holder: ItemViewHolder) {
        holder.recycle()
    }

    override fun getItemCount(): Int {
        return media.size
    }

    override fun getItemId(position: Int): Long {
        return stableIdGenerator.getId(media[position])
    }

    fun setMedia(media: List<Media>) {
        this.media.clear()
        this.media.addAll(media)
        notifyDataSetChanged()
    }

    fun setSelected(selected: Collection<Media>) {
        this.selected.clear()
        this.selected.addAll(selected)
        notifyDataSetChanged()
    }

    fun getSelected(): List<Media> {
        return selected
    }

    fun setForcedMultiSelect(forcedMultiSelect: Boolean) {
        this.forcedMultiSelect = forcedMultiSelect
        notifyDataSetChanged()
    }

    class ItemViewHolder(val binding: MediapickerMediaItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            media: Media,
            multiSelect: Boolean,
            selected: MutableList<Media>,
            maxSelection: Int,
            glideRequests: RequestManager,
            eventListener: EventListener
        ) {
            glideRequests.load(media.uri)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.mediapickerImageItemThumbnail)
            binding.mediapickerPlayOverlay.visibility =
                if (MediaUtil.isVideoType(media.mimeType)) View.VISIBLE else View.GONE
            if (selected.isEmpty() && !multiSelect) {
                itemView.setOnClickListener {
                    eventListener.onMediaChosen(
                        media
                    )
                }
                binding.mediapickerSelectOn.visibility = View.GONE
                binding.mediapickerSelectOff.visibility = View.GONE
                binding.mediapickerSelectOverlay.visibility = View.GONE
                if (maxSelection > 1) {
                    itemView.setOnLongClickListener {
                        selected.add(media)
                        eventListener.onMediaSelectionStarted()
                        eventListener.onMediaSelectionChanged(
                            ArrayList(selected)
                        )
                        true
                    }
                }
            } else if (selected.contains(media)) {
                binding.mediapickerSelectOff.visibility = View.VISIBLE
                binding.mediapickerSelectOn.visibility = View.VISIBLE
                binding.mediapickerSelectOverlay.visibility = View.VISIBLE
                binding.mediapickerSelectOrder.text = (selected.indexOf(media) + 1).toString()
                itemView.setOnLongClickListener(null)
                itemView.setOnClickListener {
                    selected.remove(media)
                    ArrayList(selected).let { it1 ->
                        eventListener.onMediaSelectionChanged(
                            it1
                        )
                    }
                }
            } else {
                binding.mediapickerSelectOff.visibility = View.VISIBLE
                binding.mediapickerSelectOn.visibility = View.GONE
                binding.mediapickerSelectOverlay.visibility = View.GONE
                itemView.setOnLongClickListener(null)
                itemView.setOnClickListener {
                    if (selected.size < maxSelection) {
                        selected.add(media)
                        eventListener.onMediaSelectionChanged(
                            ArrayList(selected)
                        )
                    } else {
                        eventListener.onMediaSelectionOverflow(maxSelection)
                    }
                }
            }
        }

        fun recycle() {
            itemView.setOnClickListener(null)
        }
    }

    interface EventListener {
        fun onMediaChosen(media: Media)
        fun onMediaSelectionStarted()
        fun onMediaSelectionChanged(media: ArrayList<Media>)
        fun onMediaSelectionOverflow(maxSelection: Int)
    }

    init {
        media = ArrayList()
        this.maxSelection = maxSelection
        stableIdGenerator = StableIdGenerator()
        selected = LinkedList()
        setHasStableIds(true)
    }
}