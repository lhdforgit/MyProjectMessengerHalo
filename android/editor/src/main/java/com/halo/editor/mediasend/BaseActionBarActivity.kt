/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.editor.mediasend

import android.os.Bundle
import android.view.KeyEvent
import android.view.ViewConfiguration
import com.hahalolo.playcore.split.DaggerSplitActivity
import com.halo.editor.mediasend.BaseActivity.Companion.isMenuWorkaroundRequired
import timber.log.Timber

abstract class BaseActionBarActivity : com.hahalolo.playcore.split.DaggerSplitActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        if (isMenuWorkaroundRequired) {
            forceOverflowMenu()
        }
        super.onCreate(savedInstanceState)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return keyCode == KeyEvent.KEYCODE_MENU && isMenuWorkaroundRequired || super.onKeyDown(
            keyCode,
            event
        )
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_MENU && isMenuWorkaroundRequired) {
            openOptionsMenu()
            return true
        }
        return super.onKeyUp(keyCode, event)
    }

    /**
     * Modified from: http://stackoverflow.com/a/13098824
     */
    private fun forceOverflowMenu() {
        try {
            val config = ViewConfiguration.get(this)
            val menuKeyField =
                ViewConfiguration::class.java.getDeclaredField("sHasPermanentMenuKey")
            menuKeyField.isAccessible = true
            menuKeyField.setBoolean(config, false)
        } catch (e: IllegalAccessException) {
            Timber.w(
                "Failed to force overflow menu."
            )
        } catch (e: NoSuchFieldException) {
            Timber.w(
                "Failed to force overflow menu."
            )
        }
    }
}