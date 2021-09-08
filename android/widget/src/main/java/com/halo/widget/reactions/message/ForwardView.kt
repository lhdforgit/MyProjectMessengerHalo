/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.reactions.message

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import androidx.appcompat.widget.AppCompatImageView
import com.halo.widget.R

/**
 * @author ndn
 * Created by ndn
 * Created on 5/30/19.
 */
@SuppressLint("ViewConstructor")
class ForwardView constructor(
        context: Context) : AppCompatImageView(context) {

    val location = Point()
        get() {
            if (field.x == 0 || field.y == 0) {
                val location = IntArray(2).also(::getLocationOnScreen)
                field.set(location[0], location[1])
            }
            return field
        }

    init {
        setImageResource(R.drawable.ic_message_forward)
    }

    @SuppressLint("DrawAllocation")
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        location.set(0, 0)
    }
}
