package com.hahalolo.socket

import com.hahalolo.socket.perf.SocketPref
import com.hahalolo.socket.perf.SocketPrefImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class SocketModule {

    @Binds
    @Singleton
    abstract fun bindSocketPref(socketPrefImpl: SocketPrefImpl): SocketPref

    @Binds
    @Singleton
    abstract fun bindSocketManager(socketManager: SocketManagerImpl): SocketManager
}