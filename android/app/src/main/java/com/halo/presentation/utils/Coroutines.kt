/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.presentation.utils

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel

/**
 * Module Coroutines Scope
 *
 * */
@Module
class CoroutinesScopeModule {
    @Provides
    fun coroutinesApp(): CoroutinesApp {
        return CoroutinesAppImp()
    }
}

interface CoroutinesApp {
    fun getMain(): CoroutineScope
    fun getIo(): CoroutineScope
    fun getDefault(): CoroutineScope
    fun cancel()
    fun addJobMain(job: Job) : CoroutineScope
    fun addJobDefault(job: Job) : CoroutineScope
    fun addJobIo(job: Job) : CoroutineScope
    fun cancelChild()
}

class CoroutinesAppImp : CoroutinesApp {

    override fun cancelChild() {

    }

    override fun addJobDefault(job: Job) : CoroutineScope {
       return CoroutineScope(Dispatchers.Default + job)
    }

    override fun addJobIo(job: Job) : CoroutineScope{
        return CoroutineScope(Dispatchers.IO + job)
    }

    override fun addJobMain(job: Job) : CoroutineScope{
        return CoroutineScope(Dispatchers.Main + job)
    }

    override fun cancel() {
        main.coroutineContext.cancel()
        io.coroutineContext.cancel()
        default.coroutineContext.cancel()
    }

    private val main = CoroutineScope(Dispatchers.Main)
    private val io = CoroutineScope(Dispatchers.IO)
    private val default = CoroutineScope(Dispatchers.Default)


    override fun getMain(): CoroutineScope {
        return main
    }

    override fun getIo(): CoroutineScope {
        return io
    }

    override fun getDefault(): CoroutineScope {
        return default
    }
}
