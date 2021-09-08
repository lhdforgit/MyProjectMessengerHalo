/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.edittext

import android.content.Context
import android.util.AttributeSet
import com.halo.widget.HaloEditText

class HideHintEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : HaloEditText(context, attrs, defStyleAttr) {
    /*override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        Log.i("===", "onFocusChanged :...")
    }*/
}

