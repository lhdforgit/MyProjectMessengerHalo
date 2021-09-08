/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.presentation.adapter.holder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.halo.R
import com.halo.databinding.EmptyUndefineErrorItemBinding

/**
 * @author ndn
 * Created by ndn
 * Created on 8/6/18
 */
class EmptyUndefineErrorHolder
private constructor(
    private val binding: EmptyUndefineErrorItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind() {
        binding.executePendingBindings()
    }

    companion object {

        fun createHolder(parent: ViewGroup): EmptyUndefineErrorHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = DataBindingUtil.inflate<EmptyUndefineErrorItemBinding>(
                inflater,
                R.layout.empty_undefine_error_item,
                parent,
                false
            )
            return EmptyUndefineErrorHolder(
                binding
            )
        }
    }
}
