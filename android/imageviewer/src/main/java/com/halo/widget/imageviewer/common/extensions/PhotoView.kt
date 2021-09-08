/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.imageviewer.common.extensions

import com.halo.widget.photoview.PhotoView

internal fun PhotoView.resetScale(animate: Boolean) {
    setScale(minimumScale, animate)
}