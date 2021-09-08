/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.incognito.presentation.base

import android.os.Bundle
import android.view.MenuItem

/**
 * @author ndn
 * Created by ndn
 * Created on 10/1/18
 */
abstract class AbsIncBackActivity : AbsIncActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initActionBar()
        supportActionBar?.setDisplayShowTitleEnabled(isShowTitleToolbar())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    protected abstract fun initActionBar()
}