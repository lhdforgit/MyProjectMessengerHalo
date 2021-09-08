/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.incognito.presentation.main

import com.hahalolo.incognito.presentation.main.contact.IncognitoContactFr
import com.hahalolo.incognito.presentation.main.conversation.IncognitoConversationFr
import com.hahalolo.incognito.presentation.main.group.IncognitoGroupFr
import com.hahalolo.incognito.presentation.main.search.IncognitoSearchFr
import com.hahalolo.incognito.presentation.main.search.IncognitoSearchModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * @author BuiSon
 * Created by BuiSon
 * Created on 10/23/2018.
 */
@Module
abstract class IncognitoMainModule {

    @com.halo.di.FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun incognitoContactFr(): IncognitoContactFr

    @com.halo.di.FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun incognitoConversationFr(): IncognitoConversationFr

    @com.halo.di.FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun incognitoGroupFr(): IncognitoGroupFr

    @com.halo.di.FragmentScoped
    @ContributesAndroidInjector(modules = [IncognitoSearchModule::class ])
    internal abstract fun incognitoSearchFr(): IncognitoSearchFr
}