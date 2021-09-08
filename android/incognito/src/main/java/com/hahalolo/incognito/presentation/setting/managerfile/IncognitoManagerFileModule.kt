package com.hahalolo.incognito.presentation.setting.managerfile

import com.hahalolo.incognito.presentation.setting.managerfile.file.IncognitoManagerDocFr
import com.hahalolo.incognito.presentation.setting.managerfile.link.IncognitoManagerLinkFr
import com.hahalolo.incognito.presentation.setting.managerfile.media.IncognitoManagerMediaFr
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class IncognitoManagerFileModule {
    @com.halo.di.FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun incognitoManagerMediaFr(): IncognitoManagerMediaFr

    @com.halo.di.FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun incognitoManagerLinkFr(): IncognitoManagerLinkFr

    @com.halo.di.FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun incognitoManagerDocFr(): IncognitoManagerDocFr
}