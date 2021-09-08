package com.halo.widget.googleplace

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.halo.widget.databinding.HaloAutocompletePlaceBinding

class HaloAutocompletePlace @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var binding = HaloAutocompletePlaceBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    init {
        initView()
    }

    fun initView() {

    }

    fun isValid(): Boolean {
        var isValid = true


        return isValid
    }

    fun dismiss() {

    }
}