/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.editor.mediasend.send

import android.net.Uri
import android.view.View

/**
 * A page that sits in the [MediaSendFragmentPagerAdapter].
 */
interface MediaSendPageFragment {
    var uri: Uri?
    val playbackControls: View?
    fun saveState(): Any?
    fun restoreState(state: Any?)
    fun notifyHidden()
}