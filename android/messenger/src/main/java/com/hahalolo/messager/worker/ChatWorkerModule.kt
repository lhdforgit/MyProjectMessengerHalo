/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.messager.worker

import com.hahalolo.messager.worker.download.DownloadMessageMediaWorker
import com.hahalolo.messager.worker.groupChangeAvatar.GroupChangeAvatarWorker
import com.halo.di.ChildWorkerFactory
import com.halo.di.WorkerKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * @author ngannd
 * Create by ngannd on 04/06/2019
 */
@Module
abstract class ChatWorkerModule {

    @Binds
    @IntoMap
    @WorkerKey(DownloadMessageMediaWorker::class)
    abstract fun bindDownloadMessageMediaWorkerFactory(worker: DownloadMessageMediaWorker.Factory): ChildWorkerFactory?

    @Binds
    @IntoMap
    @WorkerKey(GroupChangeAvatarWorker::class)
    abstract fun bindGroupChangeAvatarWorkerFactory(worker: GroupChangeAvatarWorker.Factory): ChildWorkerFactory?
}