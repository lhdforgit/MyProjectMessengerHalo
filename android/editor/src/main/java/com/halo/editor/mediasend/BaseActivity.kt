/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.editor.mediasend

import android.os.Build
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.view.KeyEvent
import androidx.fragment.app.FragmentActivity

abstract class BaseActivity : FragmentActivity() {

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

    companion object {
        val isMenuWorkaroundRequired: Boolean
            get() = VERSION.SDK_INT < VERSION_CODES.KITKAT && VERSION.SDK_INT > VERSION_CODES.GINGERBREAD_MR1 &&
                    ("LGE".equals(
                        Build.MANUFACTURER,
                        ignoreCase = true
                    ) || "E6710".equals(Build.DEVICE, ignoreCase = true))
    }
}