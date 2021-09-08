/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.presentation.startapp.deeplink

import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import com.halo.common.utils.ActivityUtils
import com.halo.common.utils.KeyboardUtils
import com.halo.common.utils.ktx.transparentStatusNavigationBar
import com.halo.presentation.base.AbsActivity
import com.halo.presentation.messApplication
import com.halo.presentation.startapp.start.StartAct.Companion.getIntent
import javax.inject.Inject

class DeepLinkHandlerAct : AbsActivity() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    override fun initializeLayout() {
        setupTheme()
        val data: Uri? = intent?.data
        data?.apply {
            when {
                path?.contains("tour/") == true -> {
                    messApplication.deepLink.idTour = uriIdParamParser(data)
                }
                path?.contains("/u/") == true -> {
                    messApplication.deepLink.idPersonalWall = uriIdParamParser(data)
                }
                path?.contains("/p/") == true -> {
                    messApplication.deepLink.idPageWall = uriIdParamParser(data)
                }
                path?.contains("/post/") == true -> {
                    messApplication.deepLink.idPost = uriIdParamParser(data)
                }
            }
        }

        analytics.logEventDeepLink(
            name = data?.toString() ?: ""
        )

        ActivityUtils.startActivity(getIntent(this))
        ActivityUtils.finishAllActivities()
    }

    private fun uriIdParamParser(uri: Uri?): String? {
        return try {
            uri?.run {
                val indexOf: Int = uri.path?.lastIndexOf("/") ?: -1
                return if (indexOf > -1) uri.path?.substring(indexOf + 1) else ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }

    /**
     * Set statusNetwork bar transparent
     * Hide statusNetwork bar and navigation bar
     */
    private fun setupTheme() {
        transparentStatusNavigationBar()
        KeyboardUtils.hideSoftInput(this)
    }

    companion object {
        const val TAG = "DeepLinkHandlerAct"
    }
}