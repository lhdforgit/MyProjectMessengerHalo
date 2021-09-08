/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.presentation.base

import android.content.Context
import com.halo.common.utils.ActivityUtils
import com.halo.presentation.startapp.signin.SignInAct
import com.halo.widget.materialdialogs.MaterialDialog

/**
 * @author admin
 * Created by admin
 * Created on 6/11/20.
 * Package com.halo.presentation.base
 */
class NotifyManager(
    val context: Context
) {
    var dialog: MaterialDialog? = null

    fun dismiss() {
        if (dialog?.isShowing == true) {
            dialog?.dismiss()
        }
    }

    private fun navigateLogin() {
        context.startActivity(SignInAct.getIntent(context))
        ActivityUtils.finishAllActivities()
    }

    fun onDestroy() {
        dialog?.clearNegativeListeners()
        dialog?.clearPositiveListeners()
        dialog?.clearNeutralListeners()
        dialog = null
    }
}