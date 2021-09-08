/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.playcore.split

import androidx.annotation.StringRes
import com.google.android.play.core.splitinstall.SplitInstallSessionState
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import com.hahalolo.playcore.R
import com.hahalolo.playcore.language.LanguageApp
import com.halo.common.utils.joinJobAndCancelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import timber.log.Timber
import kotlin.random.Random

interface SplitLanguageAction {
    fun installFailure(message: String)
    fun reUpdateConfigResource() {}
    fun showProcessUpdateLanguage(max: Int, currValue: Int, @StringRes message: Int)
}

/**
 * @author Nguyên
 */
class SplitLanguageCallbackHandle(
    var callback: SplitLanguageAction?
) : SplitInstallStateUpdatedListener {

    var first = true
    val isTest = false

    override fun onStateUpdate(
        state: SplitInstallSessionState
    ) {
        kotlin.runCatching {
            state.apply {
                val langInstall = state.languages().isNotEmpty()
                Timber.i("Split Language App State: ${state.status()}")
                when (state.status()) {
                    SplitInstallSessionStatus.INSTALLED -> {
                        if (isTest) {
                            CoroutineScope(Dispatchers.Main)
                                .joinJobAndCancelScope {
                                    (0..100)
                                        .toList()
                                        .forEach {
                                            val timeDelay = Random.nextLong(20, 100)
                                            delay(timeDelay)
                                            callback?.showProcessUpdateLanguage(
                                                100, it,
                                                R.string.percent_downdloading_language
                                            )
                                            if (it == 100) {
                                                if (langInstall) {
                                                    LanguageApp.switchLanguage(
                                                        state.languages().firstOrNull()
                                                            ?: SharePreferenceImp.getCurrLanguageApp()
                                                    )
                                                }
                                                LanguageApp.updateLocaled()
                                                callback?.reUpdateConfigResource()
                                            }
                                        }
                                }
                        } else {
                            if (langInstall) {
                                LanguageApp.switchLanguage(
                                    state.languages().firstOrNull()
                                        ?: SharePreferenceImp.getCurrLanguageApp()
                                )
                                LanguageApp.updateLocaled()
                                callback?.reUpdateConfigResource()
                            }
                        }
                    }
                    SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
                        /**
                        This may occur when attempting to download a sufficiently large module.

                        In order to see this, the application has to be uploaded to the Play Store.
                        Then features can be requested until the confirmation path is triggered.
                         */
                    }
                    SplitInstallSessionStatus.INSTALLING ->
                        disLoadState(state, R.string.installing_language)
                    SplitInstallSessionStatus.FAILED ->
                        callback?.installFailure("Error: ${state.errorCode()} for module ${state.moduleNames()}")
                    SplitInstallSessionStatus.DOWNLOADING ->
                        disLoadState(state, R.string.percent_downdloading_language)
                    else -> {
                    }
                }
            }
        }.getOrElse {
            it.printStackTrace()
        }
    }

    private fun disLoadState(
        state: SplitInstallSessionState,
        @StringRes message: Int
    ) {
        val total = state.totalBytesToDownload().toInt()
        val current = state.bytesDownloaded().toInt()
        val percent = current.div(total) * 100
        callback?.showProcessUpdateLanguage(
            100,
            percent,
            message
        )
    }
}