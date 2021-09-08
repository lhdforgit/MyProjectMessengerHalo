/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget

import android.content.Context
import android.text.style.StrikethroughSpan
import android.util.AttributeSet
import android.view.LayoutInflater
import com.halo.common.utils.SpanBuilderUtil
import com.halo.common.utils.ktx.priceFormatWithCurr
import com.halo.widget.databinding.PriceViewBinding

/**
 * @author ndn
 * Created by ndn
 * Created on 4/1/20
 * com.halo.widget
 */
class PriceView : HaloTextView {

    var binding: PriceViewBinding? = null

    constructor(context: Context)
            : this(context, null)

    constructor(
        context: Context,
        attr: AttributeSet?
    ) : this(context, attr, 0)

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        binding = PriceViewBinding.inflate(
            LayoutInflater.from(context),
            null,
            false
        )
    }

    fun updatePrice(curr: String, price: Double, disable: Boolean) {
        val s = if (disable) {
            SpanBuilderUtil().clear()
                .appendWithLineBreak(
                    price.priceFormatWithCurr(curr = curr),
                    StrikethroughSpan()
                ).build()
        } else {
            price.priceFormatWithCurr(curr = curr)
        }
        binding?.price?.text = s
    }

    fun updatePrice(curr: String, price: Double) {
        binding?.price?.text = price.priceFormatWithCurr(curr = curr)
    }
}