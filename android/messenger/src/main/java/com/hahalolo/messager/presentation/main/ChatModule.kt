/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.presentation.main

import com.hahalolo.messager.presentation.main.contacts.ChatContactsFr
import com.hahalolo.messager.presentation.main.conversation.ConversationListFr
import com.hahalolo.messager.presentation.main.group.ChatHomeGroupFr
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * @author BuiSon
 * Created by BuiSon
 * Created on 10/23/2018.
 */
@Module
abstract class ChatModule {


    @com.halo.di.FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun chatHomeFr(): ConversationListFr

    @com.halo.di.FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun chatChatHomeGroupFr(): ChatHomeGroupFr

    @com.halo.di.FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun chatChatContactsFr(): ChatContactsFr


}
