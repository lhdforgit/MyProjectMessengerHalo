package com.hahalolo.messager.presentation.adapter.paging

import android.view.View
import com.bumptech.glide.RequestManager

interface MessengerPreloadHolder {
    fun getTargets(): MutableList<View>
    fun invalidateLayout(requestManager: RequestManager?)
}