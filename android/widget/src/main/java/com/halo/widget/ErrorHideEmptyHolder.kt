/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.halo.widget.databinding.ErrorHideEmptyHolderBinding

/**
 * Create by ndn
 * Create on 4/20/20
 * com.halo.widget
 */
class ErrorHideEmptyHolder(
    var binding: ErrorHideEmptyHolderBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind() {
        binding.executePendingBindings()
    }

    companion object {
        @JvmStatic
        fun build(parent: ViewGroup): ErrorHideEmptyHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = DataBindingUtil.inflate<ErrorHideEmptyHolderBinding>(
                layoutInflater,
                R.layout.error_hide_empty_holder, parent, false
            )
            return ErrorHideEmptyHolder(binding)
        }
    }
}