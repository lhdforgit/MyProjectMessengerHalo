/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.editor.mediasend

import android.os.Bundle
import timber.log.Timber

abstract class PassphraseRequiredActionBarActivity : BaseActionBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("[${javaClass.simpleName}] onCreate()")
        super.onCreate(savedInstanceState)
        if (!isFinishing) {
            onCreate(savedInstanceState, true)
        }
    }

    protected open fun onCreate(savedInstanceState: Bundle?, ready: Boolean) {}

    override fun onResume() {
        Timber.d("[${javaClass.simpleName}] onResume()")
        super.onResume()
    }

    override fun onStart() {
        Timber.d("[${javaClass.simpleName}] onStart()")
        super.onStart()
    }

    override fun onPause() {
        Timber.d("[${javaClass.simpleName}] onPause()")
        super.onPause()
    }

    override fun onStop() {
        Timber.d("[${javaClass.simpleName}] onStop()")
        super.onStop()
    }

    override fun onDestroy() {
        Timber.d("[${javaClass.simpleName}] onDestroy()")
        super.onDestroy()
    }
}