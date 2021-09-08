/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.presentation

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.provider.FontRequest
import androidx.emoji.text.EmojiCompat
import androidx.emoji.text.EmojiCompat.InitCallback
import androidx.emoji.text.FontRequestEmojiCompatConfig
import androidx.multidex.MultiDex
import androidx.work.Configuration
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.gson.Gson
import com.hahalolo.cache.setting.SettingPref
import com.hahalolo.call.client.CallController
import com.hahalolo.call.webrtc.QBRTCSession
import com.hahalolo.messager.MessengerController
import com.hahalolo.socket.SocketManager
import com.halo.BuildConfig
import com.halo.R
import com.halo.common.glide.transformations.face.GlideFaceDetector.initialize
import com.halo.common.network.InternetAvailabilityChecker
import com.halo.common.utils.ActivityUtils
import com.halo.common.utils.Utils
import com.hahalolo.playcore.split.SplitLanguageUtils.installConfigurationChangeLanguage
import com.hahalolo.playcore.split.SplitLanguageUtils.installSplit
import com.halo.data.cache.pref.login.LoginPref
import com.halo.data.entities.mess.oauth.MessOauth
import com.halo.data.entities.mess.oauth.OauthInfo
import com.halo.data.entities.user.User
import com.halo.di.DaggerAppComponent
import com.halo.di.WorkerFactory
import com.halo.presentation.startapp.start.LauncherAct
import com.halo.themes.ThemeHelper
import com.halo.widget.emoji.EmojiManager
import com.halo.widget.emoji.google.GoogleEmojiProvider
import com.halo.widget.repository.sticker.StickerRepository
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import timber.log.Timber
import timber.log.Timber.DebugTree
import javax.inject.Inject

/**
 * @author ndn
 * Create by ndn
 * Create on 4/6/18.
 *
 *
 * This application uses EmojiCompat.
 */
class MessApplication : DaggerApplication(), Configuration.Provider {

    @JvmField
    var WIDTH_SCREEN = 0
    var HEIGHT_SCREEN = 0

    var oauth: MessOauth? = null
        set(value) {
            field = value
            socketManager.initSocket(value)
        }
        get() = kotlin.runCatching {
            field?.run {
                this
            } ?: kotlin.run {
                restartApp()
                null
            }
        }.getOrElse {
            it.printStackTrace()
            restartApp()
            null
        }
    val oauthInfo: OauthInfo?
        get() = kotlin.runCatching {
            oauth?.run {
                Gson().fromJson(this.info, OauthInfo::class.java)
            }
        }.getOrElse {
            it.printStackTrace()
            null
        }

    @Inject
    lateinit var workerFactory: WorkerFactory

    @Inject
    lateinit var settingPref: SettingPref

    @Inject
    lateinit var deepLink: DeeplinkManager

    @Inject
    lateinit var messengerController: MessengerController

    @Inject
    lateinit var loginPref: LoginPref

    @Inject
    lateinit var socketManager: SocketManager

    @Inject
    lateinit var firebaseConfigManager: FirebaseRemoteConfigManager

    @Inject
    lateinit var stickerRepository: StickerRepository

    @Inject
    lateinit var callController: CallController

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Set Theme is Light mode
        ThemeHelper.applyTheme(ThemeHelper.LIGHT_MODE)

        // Init Internet Availability Checker
        InternetAvailabilityChecker.init(this)

        // Init Glide Face Detector
        initialize(this)

        // Init Width Height Screen
        WIDTH_SCREEN = resources.displayMetrics.widthPixels
        HEIGHT_SCREEN = resources.displayMetrics.heightPixels

        // Init Fresco load image
        Fresco.initialize(this)

        // Init emoji
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        configEmoji()

        // Init Utils Common Module
        Utils.init(this)

        // Init Timber
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }

        EmojiManager.install(GoogleEmojiProvider())
    }

    override fun attachBaseContext(base: Context) {
        com.hahalolo.playcore.language.LanguageApp.init(base)
        try {
            // Change language update all application
            val ctx = installConfigurationChangeLanguage(base)
            super.attachBaseContext(ctx)
            SplitCompat.install(this)
            installSplit(this)
        } catch (e: Exception) {
            e.printStackTrace()
            super.attachBaseContext(base)
        }
        // Init multi dex
        MultiDex.install(this)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        InternetAvailabilityChecker.getInstance().removeAllInternetConnectivityChangeListeners()
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().application(this).build()
    }

    /** ==== TOKEN APP ==== */


    /**
     * Khi xảy lỗi token, thì chạy lại app để khởi tạo lại token
     */
    fun restartApp() {
        val intent = Intent(this, LauncherAct::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        ActivityUtils.finishAllActivities()
    }

    val isOpenBubble : Boolean
        get() = settingPref.isBubbleOpen()

    /** ==== END TOKEN APP ==== */

    /** ==== EMOJI APP ==== */
    private fun configEmoji() {
        try {
            // Use a downloadable font for EmojiCompat
            val config: EmojiCompat.Config = FontRequestEmojiCompatConfig(
                applicationContext, FontRequest(
                    "com.google.android.gms.fonts",
                    "com.google.android.gms",
                    "Noto Color Emoji Compat",
                    R.array.com_google_android_gms_fonts_certs
                )
            ).setReplaceAll(true)
                .registerInitCallback(object : InitCallback() {
                    override fun onInitialized() {}
                    override fun onFailed(throwable: Throwable?) {
                        throwable?.printStackTrace()
                    }
                })
            EmojiCompat.init(config)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    /** ==== END EMOJI APP ==== */

    /** ==== CALL ==== */

    val callWithUser: MutableList<User>? = null

    var currentSession: QBRTCSession? = null

    /** ==== END CALL ==== */

    /* End Language */
    companion object {
        @JvmStatic
        @get:Synchronized
        lateinit var instance: MessApplication
    }

    override fun getWorkManagerConfiguration() = Configuration.Builder()
        .setMinimumLoggingLevel(Log.DEBUG)
        .setWorkerFactory(workerFactory)
        .build()
}

private class CrashReportingTree : Timber.Tree() {
    override fun log(priority: Int,
                     tag: String?,
                     message: String,
                     throwable: Throwable?) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG
            || priority == Log.ASSERT || priority == Log.INFO
            || priority ==Log.WARN  || priority == Log.ERROR) {
            return
        }
    }
}