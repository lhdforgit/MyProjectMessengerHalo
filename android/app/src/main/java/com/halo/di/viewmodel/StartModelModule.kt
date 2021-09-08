/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.di.viewmodel

import androidx.lifecycle.ViewModel
import com.halo.presentation.setting.SettingViewModel
import com.halo.presentation.startapp.haslogin.SignInHasLoginViewModel
import com.halo.presentation.startapp.share.ShareHandlerViewModel
import com.halo.presentation.startapp.signin.SignInViewModel
import com.halo.presentation.startapp.start.LaunchViewModel
import com.halo.presentation.startapp.start.StartViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * @author ngannd
 * Create by ngannd on 22/11/2018
 */
@Module
abstract class StartModelModule {
    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(LaunchViewModel::class)
    abstract fun bindLaunchViewModel(viewModel: LaunchViewModel?): ViewModel

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(StartViewModel::class)
    abstract fun bindStartViewModel(viewModel: StartViewModel?): ViewModel

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(SignInViewModel::class)
    abstract fun bindSignInViewModel(viewModel: SignInViewModel?): ViewModel

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(SignInHasLoginViewModel::class)
    abstract fun bindSignInHasLoginViewModel(viewModel: SignInHasLoginViewModel?): ViewModel

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(ShareHandlerViewModel::class)
    abstract fun bindShareHandlerViewModel(viewModel: ShareHandlerViewModel?): ViewModel

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(SettingViewModel::class)
    abstract fun bindSettingViewModel(viewModel: SettingViewModel?): ViewModel
}