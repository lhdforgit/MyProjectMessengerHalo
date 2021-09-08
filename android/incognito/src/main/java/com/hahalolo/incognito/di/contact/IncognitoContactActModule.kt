package com.hahalolo.incognito.di.contact

import com.hahalolo.incognito.presentation.main.contact.detail.IncognitoContactDetailAct
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class IncognitoContactActModule {
    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector
    abstract fun incognitoContactDetailAct(): IncognitoContactDetailAct



}