package com.halo.widget.otp

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import com.halo.widget.R

internal class OTPChildEditText : androidx.appcompat.widget.AppCompatEditText {

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        isCursorVisible = false
        setTextColor(context.resources.getColor(R.color.transparent))
        setBackgroundDrawable(null)
        inputType = InputType.TYPE_CLASS_NUMBER
        setSelectAllOnFocus(false)
        setTextIsSelectable(false)
    }

    public override fun onSelectionChanged(start: Int, end: Int) {
        val text = text
        text?.let { t ->
            if (start != t.length || end != t.length) {
                setSelection(t.length, t.length)
                return
            }
        }

        super.onSelectionChanged(start, end)
    }

}