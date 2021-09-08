package com.hahalolo.incognito.presentation.setting.managerfile.util

import android.view.View
import com.bumptech.glide.RequestManager

interface IncognitoPreloadHolder {
    fun getTargets(): MutableList<View>
    fun invalidateLayout(requestManager: RequestManager?)
}