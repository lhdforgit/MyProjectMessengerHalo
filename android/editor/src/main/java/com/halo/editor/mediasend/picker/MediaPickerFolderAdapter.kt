/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.editor.mediasend.picker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.halo.editor.R
import com.halo.editor.databinding.MediapickerFolderItemBinding
import com.halo.editor.mediasend.MediaFolder
import com.halo.editor.mediasend.picker.MediaPickerFolderAdapter.FolderViewHolder

internal class MediaPickerFolderAdapter(
    private val glideRequests: RequestManager,
    private val eventListener: EventListener
) : RecyclerView.Adapter<FolderViewHolder>() {

    private val folders = mutableListOf<MediaFolder>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): FolderViewHolder {
        return FolderViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(viewGroup.context),
                R.layout.mediapicker_folder_item,
                viewGroup, false
            )
        )
    }

    override fun onBindViewHolder(folderViewHolder: FolderViewHolder, i: Int) {
        folderViewHolder.bind(folders[i], glideRequests, eventListener)
    }

    override fun onViewRecycled(holder: FolderViewHolder) {
        holder.recycle()
    }

    override fun getItemCount(): Int {
        return folders.size
    }

    fun setFolders(folders: List<MediaFolder>) {
        this.folders.clear()
        this.folders.addAll(folders)
        notifyDataSetChanged()
    }

    inner class FolderViewHolder(var binding: MediapickerFolderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            folder: MediaFolder,
            glideRequests: RequestManager,
            eventListener: EventListener
        ) {
            binding.mediapickerFolderItemTitle.text = folder.title
            binding.mediapickerFolderItemCount.text = folder.itemCount.toString()
            binding.mediapickerFolderItemIcon.setImageResource(
                if (folder.folderType === MediaFolder.FolderType.CAMERA)
                    R.drawable.ic_photo_camera
                else R.drawable.ic_folder
            )
            glideRequests.load(folder.thumbnailUri)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.mediapickerFolderItemThumbnail)
            itemView.setOnClickListener {
                eventListener.onFolderClicked(
                    folder
                )
            }
        }

        fun recycle() {
            itemView.setOnClickListener(null)
        }
    }

    internal interface EventListener {
        fun onFolderClicked(mediaFolder: MediaFolder)
    }
}