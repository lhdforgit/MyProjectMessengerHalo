/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.widget

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.button.MaterialButton

/**
 * @author ngannd
 * Create by ngannd on 11/07/2019
 */
open class HaloButton : MaterialButton {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attr: AttributeSet?) : this(context, attr, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )
}