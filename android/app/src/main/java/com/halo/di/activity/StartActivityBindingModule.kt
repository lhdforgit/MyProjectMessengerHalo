/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.di.activity

import com.halo.presentation.setting.SettingAct
import com.halo.presentation.startapp.deeplink.DeepLinkHandlerAct
import com.halo.presentation.startapp.haslogin.SignInHasLoginAct
import com.halo.presentation.startapp.share.ShareHandlerAct
import com.halo.presentation.startapp.signin.SignInAct
import com.halo.presentation.startapp.start.LauncherAct
import com.halo.presentation.startapp.start.StartAct
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * @author ngannd
 * Create by ngannd on 22/11/2018
 */
@Module
abstract class StartActivityBindingModule {
    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector
    abstract fun launcherAct(): LauncherAct?

    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector
    abstract fun startAct(): StartAct?

    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector
    abstract fun signInAct(): SignInAct?

    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector
    abstract fun signInHasLoginAct(): SignInHasLoginAct?

    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector
    abstract fun shareHandlerAct(): ShareHandlerAct?

    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector
    abstract fun deepLinkHandlerAct(): DeepLinkHandlerAct?

    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector()
    abstract fun setting(): SettingAct?

}