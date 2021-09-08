/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.common.utils.ktx

/**
 * @author ndn
 * Created by ndn
 * Created on 2019-11-07
 * com.halo.common.utils
 */
object ValidUtils {

    @JvmStatic
    fun isValidEmail(target: CharSequence?): Boolean {
        return target?.run {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
        } ?: false
    }

    @JvmStatic
    fun isValidPhone(target: CharSequence?): Boolean {
        return target?.run { return android.util.Patterns.PHONE.matcher(this).matches() } ?: false
    }
}

fun CharSequence.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun CharSequence.isValidPhone(): Boolean {
    return android.util.Patterns.PHONE.matcher(this).matches()
}