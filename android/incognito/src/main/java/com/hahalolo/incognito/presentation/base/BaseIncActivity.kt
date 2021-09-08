/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.incognito.presentation.base

/**
 * @author ngannd
 * Create by ngannd on 24/11/2018
 */
interface BaseIncActivity {

    /**
     * Clear cache, Leak memory when destroy view
     */
    @JvmDefault
    fun invalidateLayoutOnDestroy() {
    }

    /**
     * activity need check internet state
     *
     * @return true if need
     */
    @JvmDefault
    fun needInitInternetChecked(): Boolean {
        return false
    }

    @JvmDefault
    fun initIndicatorProgress() {
    }

    @JvmDefault
    fun cancelableIndicator(): Boolean {
        return true
    }

    @JvmDefault
    fun showIndicator() {
    }

    @JvmDefault
    fun dismissIndicator() {
    }

    @JvmDefault
    fun isLightTheme(): Boolean {
        return true
    }

    @JvmDefault
    fun isShowTitleToolbar(): Boolean {
        return true
    }
}