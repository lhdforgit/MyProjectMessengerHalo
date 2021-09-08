package com.hahalolo.incognito.presentation.create.forward

import com.hahalolo.incognito.presentation.create.forward.container.IncognitoForwardMessageFr
import com.hahalolo.incognito.presentation.create.forward.container.contact.IncognitoForwardMessageContactFr
import com.hahalolo.incognito.presentation.create.forward.container.group.IncognitoForwardMessageGroupFr
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class IncognitoForwardModule {
    @com.halo.di.FragmentScoped
    @ContributesAndroidInjector
    abstract fun incognitoForwardMessageBottomSheet(): IncognitoForwardMessageBottomSheet

    @com.halo.di.FragmentScoped
    @ContributesAndroidInjector
    abstract fun incognitoForwardMessageFr(): IncognitoForwardMessageFr

    @com.halo.di.FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun incognitoForwardMessageContactFr(): IncognitoForwardMessageContactFr

    @com.halo.di.FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun incognitoForwardMessageGroupFr(): IncognitoForwardMessageGroupFr
}