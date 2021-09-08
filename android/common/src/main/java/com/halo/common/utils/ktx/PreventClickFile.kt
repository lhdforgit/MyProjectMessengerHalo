/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.common.utils.ktx

import android.view.View
import com.google.android.material.button.MaterialButton
import com.halo.common.utils.joinJobAndCancelScope
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit


fun MaterialButton.preventFastClick(disposite: CompositeDisposable, action: () -> Unit) {
    setOnClickListener {
        if (isEnabled) {
            action()
            isEnabled = false
            isActivated = false
            isSelected = false
            val observable = Observable
                .timer(1000L, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    isEnabled = true
                    isActivated = true
                    isSelected = true
                }
            disposite.add(observable)
        }
    }
}


fun View.preventFastClick(disposite: CompositeDisposable, action: () -> Unit) {
    setOnClickListener {
        if (isClickable) {
            action()
            isClickable = false
            isEnabled = false
            isActivated = false
            isSelected = false
            val observable = Observable
                .timer(300L, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    isClickable = true
                    isEnabled = true
                    isActivated = true
                    isSelected = true
                }
            disposite.add(observable)
        }
    }
}


fun View.preventFastClickable(disposite: CompositeDisposable, action: () -> Unit) {
    setOnClickListener {
        if (isClickable) {
            action()
            isClickable = false
            val observable = Observable
                .timer(300L, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    isClickable = true
                }
            disposite.add(observable)
        }
    }
}

fun View.preventFastClickable(action: suspend () -> Unit) {
    if (this.hasOnClickListeners()) return
    setOnClickListener {
        if (isClickable) {
            val scope = CoroutineScope(Dispatchers.Default)
            scope
                .joinJobAndCancelScope {
                    action()
                    withContext(Dispatchers.Main) {
                        isClickable = false
                        isSelected = false
                    }
                    try {
                        withTimeout(1000L) {
                            delay(4000L)
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            isClickable = true
                            isSelected = true
                        }
                    } finally {
                        delay(10)
                        cancel()
                    }
                }
        }
    }
}


