/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.playcore.split

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.common.wrappers.InstantApps
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.hahalolo.playcore.language.LanguageApp
import com.halo.common.utils.ActivityUtils
import com.halo.common.utils.stringLog
import timber.log.Timber
import java.lang.ref.WeakReference
import java.util.*

/**
 * @author Nguyen
 * */
object SplitLanguageUtils {

    class Split {

        lateinit var activity: WeakReference<Activity>
        lateinit var splitInstallManager: SplitInstallManager
        lateinit var callbackHandler: SplitLanguageCallbackHandle
        lateinit var callback: SplitLanguageAction

        val getActivity
            get() = activity.get()

        fun loadAndSwitchLanguage(lang: String) {
            runCatching {
                splitInstallManager.apply {
                    this.apply {
                        if (installedLanguages.contains(lang)) {
                            LanguageApp.switchLanguage(lang)
                            callback.reUpdateConfigResource()
                            return
                        }
                        val request = SplitInstallRequest
                            .newBuilder()
                            .addLanguage(Locale.forLanguageTag(lang))
                            .build()
                        startInstall(request)
                        registerListener(callbackHandler)
                    }
                }
            }.getOrElse {
                it.printStackTrace()
            }
        }

        companion object {
            @JvmStatic
            fun create(activity: Activity, callback: SplitLanguageAction): Split {
                return Split().apply {
                    this.activity = WeakReference(activity)
                    splitInstallManager = SplitInstallManagerFactory.create(activity)
                    callbackHandler = SplitLanguageCallbackHandle(object : SplitLanguageAction {

                        override fun installFailure(message: String) {
                            Timber.i("Update language failure: $message")
                            callback.installFailure(message)
                        }

                        override fun reUpdateConfigResource() {
                            Timber.i("Update Config Resource")
                            getActivity?.let {
                                ActivityUtils.finishAllActivities()
                                val i = it.baseContext.packageManager.getLaunchIntentForPackage(
                                    it.baseContext.packageName
                                )
                                i?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                i?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                it.startActivity(i)
                            }
                        }

                        override fun showProcessUpdateLanguage(
                            max: Int,
                            currValue: Int,
                            message: Int
                        ) {
                            Timber.i("Update Language Progress: $max - $currValue")
                            callback.showProcessUpdateLanguage(max, currValue, message)
                        }
                    })
                }
            }
        }
    }

    private lateinit var splitInstallManager: SplitInstallManager

    private val callbackHandler: SplitLanguageCallbackHandle by lazy {
        SplitLanguageCallbackHandle(null)
    }

    /**
     * Start change language
     * */
    fun changeLanguage(
        split: Split,
        code: String,
        finally: (() -> Unit)? = null
    ) {
        try {
            Timber.i("Change language: $code")
            if (LanguageApp.currentLocaleCode() != code) {
                split.loadAndSwitchLanguage(code)
            } else {
                finally?.invoke()
            }
        } finally {
            finally?.invoke()
        }
    }

    private fun LifecycleOwner?.onChange(
        lifecycle: Lifecycle,
        event: Lifecycle.Event
    ) {
        this?.lifecycleScope?.launchWhenResumed {
            try {
                if (lifecycle.currentState == Lifecycle.State.RESUMED
                    && event == Lifecycle.Event.ON_RESUME
                ) {
                    registerChangeLanguageListener()
                } else unregisterChangeLanguage()
            } finally {
                unregisterChangeLanguage()
            }
        }
    }

    private fun registerChangeLanguageListener() {
        splitInstallManager.registerListener(
            callbackHandler
        )
    }

    private fun unregisterChangeLanguage() {
        splitInstallManager.unregisterListener(
            callbackHandler
        )
    }

    fun observableChangeLanguage(
        lifecycleOwner: LifecycleOwner?,
        activity: Activity,
        callback: SplitLanguageAction
    ) {
        val lifecycle = lifecycleOwner?.lifecycle
        callbackHandler.callback = callback
        lifecycle?.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                try {
                    when (event) {
                        Lifecycle.Event.ON_CREATE -> splitInstallManager =
                            SplitInstallManagerFactory.create(activity)
                        Lifecycle.Event.ON_START -> lifecycleOwner.onChange(lifecycle, event)
                        else -> unregisterChangeLanguage()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    fun stopDownloadLanguage(context: Context) {
        val isVi = LanguageApp.isCurrentVietnameseLanguage(context)
        val codeLocale = if (isVi) LanguageApp.VIETNAMESE else LanguageApp.ENGLISH
        val locale = Locale.forLanguageTag(codeLocale)
        splitInstallManager.deferredLanguageUninstall(mutableListOf(locale))
    }

    /** Config Language */

    @JvmStatic
    fun installConfigurationChangeLanguage(newContext: Context): Context {
        val newLocal = Locale.forLanguageTag(SharePreferenceImp.getCurrLanguageApp())
        Locale.setDefault(newLocal)
        val res = newContext.resources
        val configuration = Configuration(res.configuration)
        configuration.setLocale(newLocal)
        configuration.setLayoutDirection(newLocal)
        newContext.applicationContext?.resources?.updateConfiguration(
            configuration,
            res.displayMetrics
        )
        return newContext.createConfigurationContext(configuration)
    }

    @JvmStatic
    fun overrideConfiguration(context: Context, overrideConfiguration: Configuration?) {
        overrideConfiguration?.apply {
            val uiMode = overrideConfiguration.uiMode
            overrideConfiguration.setTo(context.resources.configuration)
            overrideConfiguration.uiMode = uiMode
        }
    }

    @JvmStatic
    fun installSplit(context: Context) {
        try {
            if (!InstantApps.isInstantApp(context)) {
                SplitCompat.install(context)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun installSplitActivity(context: Context) {
        try {
            if (!InstantApps.isInstantApp(context)) {
                SplitCompat.installActivity(context)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}