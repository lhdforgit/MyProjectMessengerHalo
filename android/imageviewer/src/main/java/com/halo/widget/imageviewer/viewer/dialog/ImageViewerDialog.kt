/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.imageviewer.viewer.dialog

import android.content.Context
import android.view.KeyEvent
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.halo.widget.R
import com.halo.widget.imageviewer.viewer.builder.BuilderData
import com.halo.widget.imageviewer.viewer.view.ImageViewerView

internal class ImageViewerDialog<T>(
        context: Context,
        private val builderData: BuilderData<T>
) {

    private val dialog: AlertDialog
    private val viewerView: ImageViewerView<T> = ImageViewerView(context)
    private var animateOpen = true

    private val dialogStyle: Int
        get() = if (builderData.shouldStatusBarHide)
            R.style.ImageViewerDialog_NoStatusBar
        else
            R.style.ImageViewerDialog_Default

    init {
        setupViewerView()
        dialog = AlertDialog
                .Builder(context, dialogStyle)
                .setView(viewerView)
                .setOnKeyListener { _, keyCode, event -> onDialogKeyEvent(keyCode, event) }
                .create()
                .apply {
                    setOnShowListener { viewerView.open(builderData.transitionView, animateOpen) }
                    setOnDismissListener { builderData.onDismissListener?.onDismiss() }
                }
    }

    fun show(animate: Boolean) {
        animateOpen = animate
        dialog.show()
    }

    fun close() {
        viewerView.close()
    }

    fun updateImages(images: List<T>) {
        viewerView.updateImages(images)
    }

    fun getCurrentPosition(): Int =
            viewerView.currentPosition

    fun updateTransitionImage(imageView: ImageView?) {
        viewerView.updateTransitionImage(imageView)
    }

    private fun onDialogKeyEvent(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK &&
                event.action == KeyEvent.ACTION_UP &&
                !event.isCanceled
        ) {
            if (viewerView.isScaled) {
                viewerView.resetScale()
            } else {
                viewerView.close()
            }
        }
        return true
    }

    private fun setupViewerView() {
        viewerView.apply {
            isZoomingAllowed = builderData.isZoomingAllowed
            isSwipeToDismissAllowed = builderData.isSwipeToDismissAllowed

            containerPadding = builderData.containerPaddingPixels
            imagesMargin = builderData.imageMarginPixels
            overlayView = builderData.overlayView

            setBackgroundColor(builderData.backgroundColor)
            setImages(builderData.images, builderData.startPosition, builderData.imageLoader)
            onDownloadImageListener = builderData.onDownloadImageListener

            onPageChange = { position -> builderData.imageChangeListener?.onImageChange(position) }
            onDismiss = { dialog.dismiss() }
        }
    }
}