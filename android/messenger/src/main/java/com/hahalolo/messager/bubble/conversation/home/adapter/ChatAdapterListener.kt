/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.bubble.conversation.home.adapter

interface ChatAdapterListener<T> {
    fun itemClick(t: T)

    @JvmDefault
    fun search() {
    }

    @JvmDefault
    fun menuMore(t: T) {
    }

    @JvmDefault
    fun remove(t: T) {
    }

    @JvmDefault
    fun add(t: T) {
    }

    @JvmDefault
    fun chat(t: T) {
    }

    @JvmDefault
    fun onClickMenuMore(room: T) {

    }

    @JvmDefault
    fun onClickMenuRemove(room: T) {

    }
}