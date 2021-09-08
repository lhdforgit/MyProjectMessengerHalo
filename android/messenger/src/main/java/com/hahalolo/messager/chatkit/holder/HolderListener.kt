/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.messager.chatkit.holder

import android.view.View

interface HolderListener {
    fun onShowTimeWhenClick(id: String?, timeView: View?)
    fun onShowStatusWhenClick(id: String?, statusView: View?)
    fun getWIDTH_SCREEN(): Int
    fun getHEIGHT_SCREEN(): Int
}