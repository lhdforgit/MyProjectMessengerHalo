/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.presentation.search

import com.hahalolo.messager.presentation.search.friend.ChatSearchFriendFr
import com.hahalolo.messager.presentation.search.group.ChatSearchGroupFr
import com.hahalolo.messager.presentation.search.user.ChatSearchUserFr
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ChatSearchModule {

    @com.halo.di.FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun chatSearchFriendFr(): ChatSearchFriendFr

    @com.halo.di.FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun chatSearchGroupFr(): ChatSearchGroupFr

    @com.halo.di.FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun chatSearchUserFr(): ChatSearchUserFr
}
