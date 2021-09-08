package com.hahalolo.incognito.di.contact

import androidx.lifecycle.ViewModel
import com.hahalolo.incognito.presentation.main.IncognitoMainViewModel
import com.hahalolo.incognito.presentation.main.contact.detail.IncognitoContactDetailViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class IncognitoContactViewModelModule {
    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(IncognitoContactDetailViewModel::class)
    abstract fun bindIncognitoContactDetailViewModel(viewModel: IncognitoContactDetailViewModel): ViewModel

}