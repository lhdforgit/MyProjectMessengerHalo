/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.common.utils.ktx

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * A lazy property that gets cleaned up when the fragment is destroyed.
 *
 * Accessing this variable in a destroyed fragment will throw NPE.
 */
class AutoClearedValue<T : Any>(val fragment: Fragment) : ReadWriteProperty<Fragment, T> {
    private var _value: T? = null

    init {
        fragment.lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                _value = null
            }
        })
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        return _value ?: throw IllegalStateException(
            "should never call auto-cleared-value get when it might not be available"
        )
    }

    override fun setValue(thisRef: Fragment, property: KProperty<*>, value: T) {
        _value = value
    }

    fun setValue(_value : T) : T{
        this._value = _value
        return _value
    }
}

/**
 * Creates an [AutoClearedValue] associated with this fragment.
 */
fun <T : Any> Fragment.autoCleared() =
    AutoClearedValue<T>(this)


/**
 * Check not null value for activity
 */
class ActivityNotNullValue<T : Any>(val activity: AppCompatActivity) :
    ReadWriteProperty<AppCompatActivity, T> {
    private var _value: T? = null

    override fun getValue(thisRef: AppCompatActivity, property: KProperty<*>): T {
        return _value ?: throw IllegalStateException(
            "should never call value get when it might not be available"
        )
    }

    override fun setValue(thisRef: AppCompatActivity, property: KProperty<*>, value: T) {
        _value = value
    }
}

fun <T : Any> AppCompatActivity.notNull() =
    ActivityNotNullValue<T>(this)