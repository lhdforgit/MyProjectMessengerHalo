package com.hahalolo.socket

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Create by ndn
 * Create on 5/29/21
 * com.hahalolo.socket
 */
@Singleton
class EventManagerImpl
@Inject constructor(
    val socketManager: SocketManager
) : EventManager {
    override fun identify() {

    }

}