/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.imageviewer.viewer.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.halo.widget.imageviewer.common.pager.RecyclingPagerAdapter
import com.halo.widget.imageviewer.loader.ImageLoader
import com.halo.widget.imageviewer.viewer.view.HaloPhotoView
import com.halo.widget.photoview.PhotoView

internal class ImagesPagerAdapter<T>(
    private val context: Context,
    _images: List<T>,
    private val imageLoader: ImageLoader<T>,
    private val isZoomingAllowed: Boolean
) : RecyclingPagerAdapter<ImagesPagerAdapter<T>.ViewHolder>() {

    private var images = _images
    private val holders = mutableListOf<ViewHolder>()

    fun isScaled(position: Int): Boolean =
        holders.firstOrNull { it.position == position }?.isScaled ?: false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val photoView = HaloPhotoView<T>(context)
        return ViewHolder(photoView).also { holders.add(it) }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(position)

    override fun getItemCount() = images.size

    internal fun updateImages(images: List<T>) {
        this.images = images
        notifyDataSetChanged()
    }

    internal fun resetScale(position: Int) =
        holders.firstOrNull { it.position == position }?.resetScale()

    internal inner class ViewHolder(itemView: View)
        : RecyclingPagerAdapter.ViewHolder(itemView) {

        internal var isScaled: Boolean = false
            get() = photoView.isSacleZoom

        private val photoView: HaloPhotoView<T> = itemView as HaloPhotoView<T>

        fun bind(position: Int) {
            this.position = position
            photoView.loadImage(imageLoader, images[position]);


        }

        val s = PhotoView(context)

        fun resetScale() = photoView.resetScale()
    }
}