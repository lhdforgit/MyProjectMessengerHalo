/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.widget.materialdialogs.callbacks

import com.halo.widget.materialdialogs.DialogCallback
import com.halo.widget.materialdialogs.MaterialDialog


/**
 *
 * @author ndn
 * Create by ndn on 03/11/2018
 *
 * Adds a listener that's invoked right before the dialog is [MaterialDialog.show]'n. If this is called
 * multiple times, it appends additional callbacks, rather than overwriting.
 */
fun MaterialDialog.onPreShow(callback: DialogCallback): MaterialDialog {
    this.preShowListeners.add(callback)
    return this
}

/**
 * Adds a listener that's invoked when the dialog is [MaterialDialog.show]'n. If this is called
 * multiple times, it appends additional callbacks, rather than overwriting.
 *
 * If the dialog is already showing, the callback be will be invoked immediately.
 */
fun MaterialDialog.onShow(callback: DialogCallback): MaterialDialog {
    this.showListeners.add(callback)
    if (this.isShowing) {
        // Already showing, invoke now
        this.showListeners.invokeAll(this)
    }
    setOnShowListener { this.showListeners.invokeAll(this) }
    return this
}

/**
 * Adds a listener that's invoked when the dialog is [MaterialDialog.dismiss]'d. If this is called
 * multiple times, it appends additional callbacks, rather than overwriting.
 */
fun MaterialDialog.onDismiss(callback: DialogCallback): MaterialDialog {
    this.dismissListeners.add(callback)
    setOnDismissListener { dismissListeners.invokeAll(this) }
    return this
}

/**
 * Adds a listener that's invoked when the dialog is [MaterialDialog.cancel]'d. If this is called
 * multiple times, it appends additional callbacks, rather than overwriting.
 */
fun MaterialDialog.onCancel(callback: DialogCallback): MaterialDialog {
    this.cancelListeners.add(callback)
    setOnCancelListener { cancelListeners.invokeAll(this) }
    return this
}

internal fun MutableList<DialogCallback>.invokeAll(dialog: MaterialDialog) {
    for (callback in this) {
        callback.invoke(dialog)
    }
}
