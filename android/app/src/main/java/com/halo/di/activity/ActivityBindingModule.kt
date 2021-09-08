/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.di.activity

import com.halo.presentation.common.language.LanguageAct
import com.halo.presentation.mediaviewer.MediaViewerAct
import com.halo.presentation.search.general.SearchAct
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by ndngan
 * Created on 4/6/18.
 */
@Module
abstract class ActivityBindingModule {

    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector
    abstract fun languageAct(): LanguageAct

    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector
    abstract fun searchAct(): SearchAct

    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector
    abstract fun imageViewerAct(): MediaViewerAct

}