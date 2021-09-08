package com.hahalolo.notification.di

import com.hahalolo.notification.fms.FcmReceiveService
import com.hahalolo.notification.service.FetchMessageService
import com.hahalolo.notification.worker.FcmReceiveWorker
import com.hahalolo.notification.worker.FcmReceiveWorkerFactory
import com.hahalolo.notification.worker.FcmTokenWorkerFactory
import com.hahalolo.notification.worker.RegisterFcmTokenWorker
import com.halo.di.ChildWorkerFactory
import com.halo.di.WorkerKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Create by ndn
 * Create on 6/11/21
 * com.hahalolo.notification.di
 */
@Module
abstract class NotificationModule {

    @ContributesAndroidInjector
    abstract fun fcmReceiveService(): FcmReceiveService

    @ContributesAndroidInjector
    abstract fun fetchMessageService(): FetchMessageService

    @Binds
    @IntoMap
    @WorkerKey(FcmReceiveWorker::class)
    abstract fun bindFcmReceiveWorkerFactory(worker: FcmReceiveWorkerFactory?): ChildWorkerFactory?

    @Binds
    @IntoMap
    @WorkerKey(RegisterFcmTokenWorker::class)
    abstract fun bindFcmTokenWorkerFactory(worker: FcmTokenWorkerFactory?): ChildWorkerFactory?
}