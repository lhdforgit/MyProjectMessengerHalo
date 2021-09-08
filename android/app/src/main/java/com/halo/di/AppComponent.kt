/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.di

import android.app.Application
import com.hahalolo.call.client.CallModule
import com.hahalolo.incognito.di.IncognitoModule
import com.hahalolo.messager.bubble.ChatBubbleModule
import com.hahalolo.messager.di.MessengerActModule
import com.hahalolo.messager.di.MessengerViewModelModule
import com.halo.data.ChatRoomModule
import com.hahalolo.messager.worker.ChatWorkerModule
import com.hahalolo.notification.di.NotificationModule
import com.hahalolo.playcore.di.PlayCoreModule
import com.hahalolo.qrcode.QRCodeModule
import com.hahalolo.socket.SocketModule
import com.halo.data.api.ApiModule
import com.halo.data.cache.CacheModule
import com.halo.data.repository.RepositoryModule
import com.halo.data.worker.HalomeWorkerModule
import com.halo.di.activity.ActivityBindingModule
import com.halo.di.activity.StartActivityBindingModule
import com.halo.di.viewmodel.StartModelModule
import com.halo.di.viewmodel.ViewModelModule
import com.halo.editor.di.EditorModule
import com.halo.presentation.MessApplication
import com.halo.presentation.utils.CoroutinesScopeModule
import com.halo.widget.api.StickerApiModule
import com.halo.widget.repository.StickerRepositoryModule
import com.halo.widget.room.StickerRoomModule
import com.halo.workmanager.WorkerModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

/**
 * Created by ndngan
 * Created on 4/6/18.
 */
@Singleton
@Component(
    modules = [
        ApplicationModule::class,
        ViewModelModule::class,
        StartModelModule::class,
        MessengerViewModelModule::class,
        ApiModule::class,
        CacheModule::class,
        RepositoryModule::class,

        ActivityBindingModule::class,
        StartActivityBindingModule::class,
        MessengerActModule::class,
        AndroidSupportInjectionModule::class,
        WorkerModule::class,
        CoroutinesScopeModule::class,

        //module halo messenger
        ChatWorkerModule::class,
        ChatRoomModule::class,
        ChatBubbleModule::class,
        HalomeWorkerModule::class,

        //module incognito
        IncognitoModule::class,

        //socket
        SocketModule::class,
        QRCodeModule::class,

        NotificationModule::class,
        //sticker
        StickerApiModule::class,
        StickerRepositoryModule::class,
        StickerRoomModule::class,
        CallModule::class,
        QRCodeModule::class,
        EditorModule::class,

        PlayCoreModule::class
    ]
)
interface AppComponent : AndroidInjector<MessApplication> {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
}