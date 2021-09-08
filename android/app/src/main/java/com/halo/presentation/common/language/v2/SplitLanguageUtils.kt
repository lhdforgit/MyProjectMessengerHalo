/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.presentation.common.language.v2


/*
object SplitLanguageUtils {

    private var action: (() -> Unit)? = null
    private lateinit var splitInstallManager: SplitInstallManager
    private val callbackHandler: SplitLanguageCallbackHandle by lazy { SplitLanguageCallbackHandle(null) }

    */
/**----------COUROUTINES---------------*//*


    fun changeEnglish(context: Context?, action: () -> Unit) {
        this.action = action
        context?.run {
            LanguageApp.apply {
                if (TextUtils.equals(currentLocaleCode(), VIETNAMESE)) {
                    if (::splitInstallManager.isInitialized) {
                        splitInstallManager.loadAndSwitchLanguage(ENGLISH)
                    }
                }
            }
        }
    }

    fun changeVietnamese(context: Context?, action: () -> Unit) {
        this.action = action
        context?.run {
            LanguageApp.apply {
                if (TextUtils.equals(currentLocaleCode(), ENGLISH)) {
                    if (::splitInstallManager.isInitialized) {
                        splitInstallManager.loadAndSwitchLanguage(VIETNAMESE)
                    }
                }
            }
        }
    }

    fun observableChangeLanguage(lifecycleOwner: LifecycleOwner?, activity: Activity, callback: SplitLanguageAction) {
        val lifecycle = lifecycleOwner?.lifecycle
        callbackHandler.action = callback
        lifecycle?.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                try {
                    when (event) {
                        Lifecycle.Event.ON_CREATE -> {
                            splitInstallManager = attachSplitLanguage(activity)
                        }
                        Lifecycle.Event.ON_START -> {
                            updateCurrentConfigLanguage(activity)
                            lifecycleOwner.onChange(activity,lifecycle, event)
                        }
                        else -> {
                            unregisterChangeLanguage()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    private fun attachSplitLanguage(context: Context): SplitInstallManager {
        return SplitInstallManagerFactory.create(context)
    }

    private fun LifecycleOwner?.onChange(context: Context,lifecycle: Lifecycle, event: Lifecycle.Event) {
        this?.lifecycleScope?.launchWhenResumed {
            try {
                if (lifecycle.currentState == Lifecycle.State.RESUMED && event == Lifecycle.Event.ON_RESUME) {
                    registerChangeLanguageListener()
                    doCallbackChangeLanguage(context)
                } else {
                    unregisterChangeLanguage()
                }
            } finally {
                unregisterChangeLanguage()
            }
        }
    }


    private fun doCallbackChangeLanguage(context: Context) {

    }

    private fun registerChangeLanguageListener() {
        splitInstallManager.registerListener(callbackHandler)
    }

    private fun unregisterChangeLanguage() {
        splitInstallManager.unregisterListener(callbackHandler)
    }


    fun updateCurrentConfigLanguage(context: Context?) {
        */
/*Tạo lại act*//*

        val recreateAct = fun() {
            if (context is Activity) {
                val intent = context.intent
                context.finish()
                context.startActivity(intent)
                context.overridePendingTransition(0, 0)
            }
        }
        */
/*Kiểm tra act có cần update lại resource*//*

        val checkLocaleConfigOfRes = fun(): Locale? {
            val res = context?.resources
            val configuration = res?.configuration
            return if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
                configuration?.locale
            } else {
                if (configuration?.locales?.isEmpty == false) {
                    configuration.locales.get(0)
                } else {
                    null
                }
            }
        }
        */
/*if code locale diff in confi*//*

        if (LanguageApp.currentLocaleCode() != checkLocaleConfigOfRes()?.language) {
            recreateAct()
        }
    }


    private fun SplitInstallManager?.loadAndSwitchLanguage(lang: String) {
        this?.apply {
            if (installedLanguages?.contains(lang) == true) {
                LanguageApp.switchLanguage(lang)
                action?.invoke()
                return
            }
            val request = SplitInstallRequest.newBuilder()
                    .addLanguage(Locale.forLanguageTag(lang))
                    .build()
            startInstall(request)
            registerListener(callbackHandler)
        }
    }

    fun stopDownloadLanguage(context: Context) {
        val isVi = LanguageApp.isCurentVietnamesLanguage(context)
        val codeLocale = if (isVi) LanguageApp.VIETNAMESE else LanguageApp.ENGLISH
        val locale = Locale.forLanguageTag(codeLocale)
        splitInstallManager.deferredLanguageUninstall(mutableListOf(locale))
    }

    */
/** config Language ================================================================================================*//*


    @JvmStatic
    fun installConfigurationChangeLanguage(newContext: Context): Context {
        val newLocal = Locale.forLanguageTag(SharePreferenceImp.getCurrLanguageApp())
        Locale.setDefault(newLocal)
        val res = newContext.resources
        val configuration = Configuration(res.configuration)
        configuration.setLocale(newLocal)
        configuration.setLayoutDirection(newLocal)
        return if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) {
            newContext.createConfigurationContext(configuration)
            newContext.applicationContext?.resources?.updateConfiguration(configuration, res.displayMetrics)
            newContext
        } else {
            newContext.createConfigurationContext(configuration)
        }
    }

    @JvmStatic
    fun overrideConfiguration(context: Context, overrideConfiguration: Configuration?) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) {
            overrideConfiguration?.apply {
                val uiMode = overrideConfiguration.uiMode
                overrideConfiguration.setTo(context.resources.configuration)
                overrideConfiguration.uiMode = uiMode
            }
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
}*/
