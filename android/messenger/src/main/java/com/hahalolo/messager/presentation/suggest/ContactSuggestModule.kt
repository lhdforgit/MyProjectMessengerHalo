/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.presentation.suggest

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ContactSuggestModule {

    @com.halo.di.FragmentScoped
    @ContributesAndroidInjector
    abstract fun contactSuggestFr(): ContactsSuggestFr
}