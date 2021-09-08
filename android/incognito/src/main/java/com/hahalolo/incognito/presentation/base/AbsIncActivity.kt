/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.incognito.presentation.base

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.Window
import com.bumptech.glide.Glide
import com.hahalolo.incognito.presentation.common.IncognitoProgressDialog
import com.hahalolo.incognito.presentation.controller.IncognitoController
import com.halo.common.glide.transformations.face.GlideFaceDetector
import com.halo.common.network.InternetAvailabilityChecker
import com.halo.common.network.InternetConnectivityListener
import com.halo.common.utils.GlideRequestBuilder
import com.halo.common.utils.KeyboardUtils
import com.hahalolo.playcore.split.DaggerSplitActivity
import com.halo.common.utils.ktx.fullScreen
import com.halo.common.utils.ktx.lightTheme
import timber.log.Timber
import javax.inject.Inject

/**
 * @author ndn
 * Created by ndn
 * Created on 5/15/18.
 */
abstract class AbsIncActivity : DaggerSplitActivity(), BaseIncActivity, InternetConnectivityListener {

    @Inject
    lateinit var appController: IncognitoController

    val glide by lazy { Glide.with(this) }

    private var mInternetAvailabilityChecker: InternetAvailabilityChecker? = null
    var isInternetConnected = true

    var indicatorDialog : IncognitoProgressDialog? = null

    val centerCropRequest by lazy {
        GlideRequestBuilder.getCenterCropRequest(Glide.with(this))
    }
    val centerInsideRequest by lazy {
        GlideRequestBuilder.getCenterInsideRequest(Glide.with(this))
    }
    val circleRequest by lazy {
        GlideRequestBuilder.getCircleCropRequest(Glide.with(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enable extended screen features. This must be called before setContentView().
        // May be called as many times as desired as long as it is before setContentView().
        // If not called, no extended features will be available.
        // You can not turn off a feature once it is requested.
        // You cannot use other title features with FEATURE_CUSTOM_TITLE.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        }
        if (isLightTheme()) {
            lightTheme()
        }
        // Todo init progress
        initIndicatorProgress()

        initializeBindingViewModel()
        initializeLayout()

        if (needInitInternetChecked()) {
            initInternetChecked()
        }
    }

    abstract fun initializeBindingViewModel()

    abstract fun initializeLayout()

    /**
     * Áp dụng xoay màn hình không reload lại dữ liêu của activity
     * Thêm android:configChanges="orientation|screenSize" vào activity trong manifest
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onResume() {
        super.onResume()
        dismissIndicator()
        if (isFullScreen()) {
            fullScreen()
        }
    }

    override fun onStop() {
        dismissIndicator()
        try {
            KeyboardUtils.hideSoftInput(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        super.onStop()
    }

    override fun onDestroy() {
        invalidateLayoutOnDestroy()
        super.onDestroy()
    }

    /* Indicator */

    override fun initIndicatorProgress() {
        indicatorDialog = IncognitoProgressDialog(this).apply {
            setCancelable(cancelableIndicator())
        }
    }

    override fun showIndicator() {
        indicatorDialog?.takeIf { !it.isShowing }?.show()
    }

    override fun dismissIndicator() {
        indicatorDialog?.takeIf { it.isShowing }?.dismiss()
    }

    protected open fun isFullScreen(): Boolean {
        return false
    }

    /* Check internet */

    private fun initInternetChecked() {
        if (needInitInternetChecked()) {
            mInternetAvailabilityChecker = InternetAvailabilityChecker.getInstance()
            mInternetAvailabilityChecker?.addInternetConnectivityListener(this)
        }
    }

    private fun invalidateInternetChecked() {
        mInternetAvailabilityChecker?.removeInternetConnectivityChangeListener(this)
    }

    override fun onInternetConnectivityChanged(isConnected: Boolean) {
        isInternetConnected = isConnected
    }


    override fun invalidateLayoutOnDestroy() {
        try {
            KeyboardUtils.unregisterSoftInputChangedListener(this)
            invalidateInternetChecked()
            GlideFaceDetector.releaseDetector()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
