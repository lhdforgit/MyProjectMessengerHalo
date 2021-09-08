/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.common.utils.ktx

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

/**
 * @author ndn
 * Created by ndn
 * Created on 2019-12-03
 * com.halo.presentation.utils
 */

/**
 * Kotlin Extensions for simpler, easier and funw way
 * of launching of Activities
 *
 * // Simple Activities
 *  launchActivity<MainActivity>()
 *
 *  // Add Intent extras
 *  launchActivity<MainActivity> {
 *  putExtra(INTENT_USER_ID, user.id)
 *  }
 *
 *  // Add custom flags
 *  launchActivity<MainActivity> {
 *  putExtra(INTENT_USER_ID, user.id)
 *  addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
 *  }
 *
 *  // Add Shared Transistions
 *  val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, avatar, "avatar")
 *  launchActivity<MainActivity>(options = options) {
 *  putExtra(INTENT_USER_ID, user.id)
 *  }
 *
 *  // Add requestCode for startActivityForResult() call
 *  launchActivity<MainActivity>(requestCode = 1234) {
 *  putExtra(INTENT_USER_ID, user.id)
 *  }
 */

inline fun <reified T : Any> Activity.launchActivity(
    requestCode: Int = -1,
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {}
) {
    val intent = newIntent<T>(this)
    intent.init()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        startActivityForResult(intent, requestCode, options)
    } else {
        startActivityForResult(intent, requestCode)
    }
}

inline fun <reified T : Any> Context.launchActivity(
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {}
) {
    val intent = newIntent<T>(this)
    intent.init()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        startActivity(intent, options)
    } else {
        startActivity(intent)
    }
}

inline fun <reified T : Any> newIntent(context: Context): Intent =
    Intent(context, T::class.java)


fun AppCompatActivity.addFragment(fragment: Fragment, frameId: Int) {
    supportFragmentManager.inTransaction { add(frameId, fragment) }
}

fun AppCompatActivity.addFragment(fragment: Fragment, frameId: Int, tag: String) {
    supportFragmentManager.inTransaction { add(frameId, fragment, tag) }
}

fun AppCompatActivity.replaceFragment(fragment: Fragment, frameId: Int) {
    supportFragmentManager.inTransaction { replace(frameId, fragment) }
}

fun AppCompatActivity.replaceFragment(
    fragment: Fragment, frameId: Int,
    tag: String? = null,
    @AnimatorRes @AnimRes enter: Int = 0,
    @AnimatorRes @AnimRes exit: Int = 0,
    @AnimatorRes @AnimRes popEnter: Int = 0,
    @AnimatorRes @AnimRes popExit: Int = 0
) {
    supportFragmentManager.inTransaction {
        if (enter != 0 && exit != 0 && popEnter != 0 && popExit != 0) {
            setCustomAnimations(enter, exit, popEnter, popExit)
        }
        replace(frameId, fragment, tag)
    }
}

fun AppCompatActivity.replaceFragmentToBackStack(fragment: Fragment, frameId: Int) {
    supportFragmentManager.inTransaction {
        replace(frameId, fragment)
        addToBackStack(fragment::class.simpleName)
    }
}

fun AppCompatActivity.replaceFragmentToBackStack(
    fragment: Fragment,
    frameId: Int,
    backStack: String,
    @AnimatorRes @AnimRes enter: Int = 0,
    @AnimatorRes @AnimRes exit: Int = 0,
    @AnimatorRes @AnimRes popEnter: Int = 0,
    @AnimatorRes @AnimRes popExit: Int = 0
) {
    supportFragmentManager.inTransaction {
        if (enter != 0 && exit != 0 && popEnter != 0 && popExit != 0) {
            setCustomAnimations(enter, exit, popEnter, popExit)
        }
        replace(frameId, fragment)
        addToBackStack(backStack)
    }
}

fun <T : Fragment> AppCompatActivity.findFragmentByTag(tag: String): T? {
    return supportFragmentManager.findFragmentByTag(tag) as? T
}

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> Unit) {
    val fragmentTransaction = beginTransaction()
    fragmentTransaction.func()
    fragmentTransaction.commit()
}