/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.playcore.split

import android.content.Context
import android.content.res.Configuration
import com.google.android.play.core.splitcompat.SplitCompat
import dagger.android.support.DaggerAppCompatActivity

abstract class DaggerSplitActivity : DaggerAppCompatActivity(){

    override fun attachBaseContext(newBase: Context) {
        try {
            //Change language update all application
            val ctx = SplitLanguageUtils.installConfigurationChangeLanguage(newBase)
            super.attachBaseContext(ctx)
            SplitCompat.install(this)
            SplitLanguageUtils.installSplitActivity(this)
        } catch (e: Exception) {
            e.printStackTrace()
            super.attachBaseContext(newBase)
        }
    }

    override fun applyOverrideConfiguration(overrideConfiguration: Configuration?) {
        SplitLanguageUtils.overrideConfiguration(baseContext, overrideConfiguration)
        super.applyOverrideConfiguration(overrideConfiguration)
    }
}