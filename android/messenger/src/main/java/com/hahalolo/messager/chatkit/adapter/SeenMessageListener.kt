/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.messager.chatkit.adapter

interface SeenMessageListener {
    fun onSeenMessage(messageId: String?){}
    fun onSeenMessage(messageId: String?, time:Long?)
}