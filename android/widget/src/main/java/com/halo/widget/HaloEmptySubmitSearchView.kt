/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import androidx.appcompat.widget.SearchView

/**
 * @author ngannd
 * Create by ngannd on 11/07/2019
 */
class HaloEmptySubmitSearchView : SearchView {

    private var mSearchSrcTextView: SearchAutoComplete? = null

    internal var listener: OnQueryTextListener? = null

    constructor(context: Context) : super(context) {
        initLayout()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initLayout()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initLayout()
    }

    private fun initLayout() {
        try {
            mSearchSrcTextView = findViewById(R.id.search_src_text)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mSearchSrcTextView?.setTextAppearance(R.style.Messenger_Character_H6_OneLine)
            } else {
                mSearchSrcTextView?.setTextAppearance(context, R.style.Messenger_Character_H6_OneLine)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun setOnQueryTextListener(listener: OnQueryTextListener?) {
        super.setOnQueryTextListener(listener)
        this.listener = listener
        mSearchSrcTextView?.setOnEditorActionListener { _, _, _ ->
            listener?.onQueryTextSubmit(query.toString())
            true
        }
    }
}