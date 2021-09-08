/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.presentation.common.language.v2


/*
interface SplitLanguageAction {

    fun recreateAct()

    fun showProcessUpdateLanguage(max: Int, currValue: Int, @StringRes message: Int)

}

class SplitLanguageCallbackHandle(var action: SplitLanguageAction?) : SplitInstallStateUpdatedListener {

    override fun onStateUpdate(state: SplitInstallSessionState?) {
        try {
            state?.apply {
                val langInstall = state.languages().isNotEmpty()
                when (state.status()) {
                    SplitInstallSessionStatus.INSTALLED -> {
                        if (langInstall) {
                            LanguageApp.switchLanguage(state.languages().firstOrNull()
                                    ?: SharePreferenceImp.getCurrLanguageApp())
                            LanguageApp.updateLocaled()
                            action?.recreateAct()
                        }
                    }
                    SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
                        */
/**
                        This may occur when attempting to download a sufficiently large module.

                        In order to see this, the application has to be uploaded to the Play Store.
                        Then features can be requested until the confirmation path is triggered.
                         *//*

                        startIntentSender(state.resolutionIntent()?.intentSender, null, 0, 0, 0)
                    }
                    SplitInstallSessionStatus.INSTALLING -> {
                        disLoadState(state, com.halo.R.string.installing_language)
                    }
                    SplitInstallSessionStatus.FAILED -> {
                        toastAndLog("Error: ${state.errorCode()} for module ${state.moduleNames()}")
                    }
                    SplitInstallSessionStatus.DOWNLOADING -> {
                        disLoadState(state ,com.halo.R.string.percent_downdloading_language)
                    }
                    else -> {

                    }
                }
            } ?: emptyState()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun toastAndLog(s: String) {

    }


    private fun startIntentSender(intentSender: IntentSender?, nothing: Nothing?, i: Int, i1: Int, i2: Int) {

    }

    private fun disLoadState(state: SplitInstallSessionState, @StringRes message: Int) {
        action?.showProcessUpdateLanguage(state.totalBytesToDownload().toInt(), state.bytesDownloaded().toInt(), message)
    }

    private fun emptyState() {

    }
}*/
