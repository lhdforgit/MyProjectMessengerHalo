/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.workmanager

import com.halo.di.ChildWorkerFactory
import com.halo.di.WorkerFactory
import com.halo.di.WorkerKey
import com.halo.workmanager.user.UserWorker
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class WorkerModule {
    @Binds
    abstract fun bindWorkerFactory(workerFactory: WorkerFactory?): androidx.work.WorkerFactory?

    @Binds
    @IntoMap
    @WorkerKey(UserWorker::class)
    abstract fun bindFactoryUserWorker(worker: UserWorker.Factory?): ChildWorkerFactory?
}