/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.common.utils.ktx

import android.os.SystemClock
import android.view.View
import android.widget.Button

/**
 * @author ndn
 * Created by ndn
 * Created on 2019-11-05
 * com.halo.presentation.utils
 */

fun Button.updateStateButton(enable: Boolean) {
    isActivated = enable
    isEnabled = enable
    isSelected = enable
    if (enable) {
        this.alpha = 1f
    } else {
        this.alpha = 0.5f
    }
}

inline var Button.isEnableState: Boolean
    get() {
        return isActivated && isEnabled && isSelected
    }
    set(value) {
        updateStateButton(value)
    }

object ButtonUtils {
    @JvmStatic
    fun updateStateButton(button: Button, enable: Boolean) {
        button.updateStateButton(enable)
    }
}

class SafeClickListener(
    private var defaultInterval: Int = 1000,
    private val onSafeCLick: (View) -> Unit
) : View.OnClickListener {
    private var lastTimeClicked: Long = 0
    override fun onClick(v: View) {
        if (SystemClock.elapsedRealtime() - lastTimeClicked < defaultInterval) {
            return
        }
        lastTimeClicked = SystemClock.elapsedRealtime()
        onSafeCLick(v)
    }
}

fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
    val safeClickListener = SafeClickListener {
        onSafeClick(it)
    }
    setOnClickListener(safeClickListener)
}