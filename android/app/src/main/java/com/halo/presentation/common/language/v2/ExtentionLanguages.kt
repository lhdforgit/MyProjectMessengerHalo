/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.presentation.common.language.v2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.halo.R
import com.halo.databinding.DialogProgressDownloadLanguageBinding

class DialogShowPercentDowloadLanguage(val context: Context) {
    val binding: DialogProgressDownloadLanguageBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.dialog_progress_download_language,
        null,
        false
    )
    val builder = context.createDialogProgress(binding.root)
    val dialog = builder.create()
    val isShowing: Boolean = dialog.isShowing
    private var canceledAction: (() -> Unit)? = null
    private var completeAction: (() -> Unit)? = null

    fun updateProgress(message: String?, currValue: Int) {
        show()
        binding.progress.progress = currValue
        if (currValue == 100) {
            binding.percent = message
        } else {
            binding.percent =
                StringBuilder().append(message).append("...").append(currValue).toString()
        }
    }

    private fun Context.createDialogProgress(view: View): AlertDialog.Builder {
        val dialog = AlertDialog.Builder(this)
        dialog.setCancelable(false)
        dialog.setView(view)
        dialog.setCancelable(false)
//        dialog.setNegativeButton(R.string.cancel) { _, _ ->
//            canceledAction?.invoke()
//            hide()
//        }
        return dialog
    }


    fun show() {
        if (!isShowing) {
            dialog.create()
            dialog.show()
        }
    }

    fun hide() {
        if (isShowing) {
            dialog.hide()
        }
    }

    fun cancelCallback(action: () -> Unit) {
        this.canceledAction = action
        dialog.dismiss()
    }

    fun completeCallback(action: () -> Unit) {
        this.completeAction = action
        dialog.dismiss()
    }

    companion object {
        @JvmStatic
        fun instance(context: Context): DialogShowPercentDowloadLanguage {
            return DialogShowPercentDowloadLanguage(context)
        }
    }
}