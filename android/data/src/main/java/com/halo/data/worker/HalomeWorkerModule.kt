/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.data.worker

import com.halo.data.worker.channel.CreateChannelWorker
import com.halo.data.worker.channel.InviteJoinChannelWorker
import com.halo.data.worker.sendMessage.SendMessageWorker
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
abstract class HalomeWorkerModule {

    @Binds
    @IntoMap
    @WorkerKey(SendMessageWorker::class)
    abstract fun bindSendMessageWorkerFactory(worker: SendMessageWorker.Factory): ChildWorkerFactory?

    @Binds
    @IntoMap
    @WorkerKey(CreateChannelWorker::class)
    abstract fun bindCreateChannelWorkerFactory(worker: CreateChannelWorker.Factory): ChildWorkerFactory?

    @Binds
    @IntoMap
    @WorkerKey(InviteJoinChannelWorker::class)
    abstract fun bindInviteJoinChannelWorkerFactory(worker: InviteJoinChannelWorker.Factory): ChildWorkerFactory?
}