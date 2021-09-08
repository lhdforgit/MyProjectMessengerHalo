/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.di

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import androidx.core.content.getSystemService
import com.hahalolo.cache.setting.SettingPref
import com.hahalolo.cache.setting.SettingPrefImpl
import com.hahalolo.call.client.CallController
import com.hahalolo.incognito.presentation.controller.IncognitoController
import com.hahalolo.messager.MessengerController
import com.hahalolo.notification.controller.NotificationController
import com.hahalolo.playcore.controller.PlayCoreController
import com.hahalolo.socket.EventManager
import com.hahalolo.socket.EventManagerImpl
import com.halo.common.utils.ConnectivityChecker
import com.halo.data.cache.pref.login.LoginPref
import com.halo.data.cache.pref.login.LoginPrefImpl
import com.halo.presentation.DeeplinkManager
import com.halo.presentation.FirebaseRemoteConfigManager
import com.halo.presentation.HahaloloAppManager
import com.halo.presentation.controller.module.*
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by ndngan
 * Created on 4/6/18.
 */
@Module
abstract class ApplicationModule {
    @Binds
    @Singleton
    abstract fun bindContext(application: Application): Context

    @Binds
    @Singleton
    abstract fun bindLoginPref(pref: LoginPrefImpl): LoginPref

    @Binds
    @Singleton
    abstract fun setting(pref: SettingPrefImpl): SettingPref

    @Binds
    @Singleton
    abstract fun messengerController(controller: MessengerControllerImpl): MessengerController

    @Binds
    @Singleton
    abstract fun incognitoController(controller: IncognitoControllerImpl): IncognitoController

    @Binds
    @Singleton
    abstract fun notificationController(controller: NotificationControllerImpl): NotificationController

    @Binds
    @Singleton
    abstract fun callController(controller: CallControllerImpl): CallController

    @Binds
    @Singleton
    abstract fun playCoreControllerImpl(controller: PlayCoreControllerImpl): PlayCoreController

    @Binds
    @Singleton
    abstract fun bindEventManager(eventManager: EventManagerImpl): EventManager

    companion object {

        @Singleton
        @Provides
        fun bindDeepLinkManager() = DeeplinkManager()

        @Singleton
        @Provides
        fun bindHahaloloAppManager() = HahaloloAppManager()

        @Singleton
        @Provides
        fun bindFirebaseRemoteConfigManager() = FirebaseRemoteConfigManager()

        @Provides
        fun connectivityChecker(context: Application): ConnectivityChecker? {
            val connectivityManager = context.getSystemService<ConnectivityManager>()
            return if (connectivityManager != null) {
                ConnectivityChecker(connectivityManager)
            } else {
                null
            }
        }
    }
}