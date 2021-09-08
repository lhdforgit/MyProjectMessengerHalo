/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.editor.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.halo.editor.databinding.EditorThumbnailControlBinding

/**
 * Create by ndn
 * Create on 4/18/20
 * com.halo.editor.components
 */
class ThumbnailViewControl : FrameLayout {

    lateinit var binding: EditorThumbnailControlBinding
    var listener: ThumbnailViewControlListener? = null

    var editable = false
        set(value) {
            field = value
            binding.editable = value
        }

    constructor(context: Context)
            : this(context, null)

    constructor(context: Context, attrs: AttributeSet?)
            : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        this.init()
    }

    private fun init() {
        val inflater = LayoutInflater.from(context)
        binding = EditorThumbnailControlBinding.inflate(inflater, this, true)
        binding.editIv.setOnClickListener {
            listener?.editThumbnail()
        }
        binding.removeIv.setOnClickListener {
            listener?.removeThumbnail()
        }
    }
}

interface ThumbnailViewControlListener {
    fun removeThumbnail()
    fun editThumbnail()
}