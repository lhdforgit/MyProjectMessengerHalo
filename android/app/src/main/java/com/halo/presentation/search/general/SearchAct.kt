/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.presentation.search.general

import android.content.Context
import android.content.Intent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.halo.R
import com.halo.databinding.SearchActBinding
import com.halo.presentation.base.AbsBackActivity
import java.util.*
import javax.inject.Inject

/**
 * @author ndn
 * Created by ndn
 * Created on 10/29/18.
 */
class SearchAct : AbsBackActivity() {
    @Inject
    lateinit var factory: ViewModelProvider.Factory
    internal var binding: SearchActBinding? = null
    internal var viewModel: SearchViewModel? = null

    override fun initializeBindingViewModel() {
        binding = DataBindingUtil.setContentView(this@SearchAct, R.layout.search_act)
        viewModel = ViewModelProvider(this, factory).get(SearchViewModel::class.java)
    }

    override fun initializeLayout() {

    }

    override fun initActionBar() {
        setSupportActionBar(binding?.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }


    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, SearchAct::class.java)
        }
    }
}

