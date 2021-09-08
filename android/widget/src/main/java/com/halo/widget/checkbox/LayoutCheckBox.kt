/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.checkbox

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.view.ViewCompat
import com.halo.widget.R

interface LayoutCheckBoxCallback {
    fun onIndexOfBoxChecking(value: Int?)
}
/** Layout radio check box with custom  CheckBoxWithSingleText margin start 16dp*/
class LayoutCheckBox : LinearLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributes: AttributeSet?) : super(context, attributes)
    constructor(context: Context, attributes: AttributeSet?, def: Int) : super(context, attributes)

    private val cbs by lazy { mutableListOf<CheckBoxWithSingleText>() }
    private var currentChecked: Int? = null
    private var callback: LayoutCheckBoxCallback? = null

    override fun onFinishInflate() {
        super.onFinishInflate()
        for (index in 0 until childCount) {
            when (val viewPosition = getChildAt(index)) {
                is CheckBoxWithSingleText -> {
                    viewPosition.setTag("checkBox$index")
                    viewPosition.checked = {
                        checked(viewPosition.getTag() as String, index)
                    }
                    cbs.add(viewPosition)
                }
            }
        }
        cbs.distinctBy { it.tag }
        cbs.firstOrNull()?.isChecked = true
    }

    private fun checked(tag: String, indexChecked: Int) {
        try {
            val cb = findViewWithTag<CheckBoxWithSingleText>(tag)
            this.currentChecked = indexChecked
            cbs.forEach {
                if (it != cb){
                    it.isChecked = false
                }
               // it.isEnabled = !it.isChecked
            }
            callback?.onIndexOfBoxChecking(cbs.indexOf(cb))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun addListenerPosition(callback: LayoutCheckBoxCallback) {
        this.callback = callback
    }

    fun setCheckedPosition(position : Int){
        if(cbs.isNullOrEmpty()) return
        cbs.getOrNull(position)?.isChecked = true
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        cbs.clear()
        currentChecked = null
        callback = null
    }
}

/** check box with custom */
class CheckBoxWithSingleText : AppCompatCheckBox {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributes: AttributeSet?) : super(context, attributes)
    constructor(context: Context, attributes: AttributeSet?, def: Int) : super(context, attributes)

    internal var checked: (() -> Unit)? = null

    init {
        setButtonDrawable(R.drawable.check_box_around_margin_start)
        ViewCompat.setLayoutDirection(this,ViewCompat.LAYOUT_DIRECTION_LTR)
        isEnabled = !isChecked
        setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                checked?.invoke()
            }
        }
    }

    fun setText(sContent: String) {
        text = sContent
    }
}
