/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.appbar

import android.content.Context
import android.util.AttributeSet
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout

class AppBarCustom : AppBarLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context,attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, def: Int) : super(context,attributeSet,def)

    override fun setExpanded(expanded: Boolean) {
        val params = layoutParams as CoordinatorLayout.LayoutParams
        if (params.behavior == null) params.behavior = Behavior()
        val behavior = params.behavior as Behavior
        behavior.setDragCallback(object : Behavior.DragCallback() {
            override fun canDrag(p0: AppBarLayout): Boolean {
                return expanded
            }
        })
        super.setExpanded(expanded)
    }
}