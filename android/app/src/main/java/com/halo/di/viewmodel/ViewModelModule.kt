/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.di.viewmodel

//import com.halo.presentation.common.currency.CurrencyViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.halo.di.HahaloloViewModelFactory
import com.halo.presentation.common.language.LanguageViewModel
import com.halo.presentation.mediaviewer.MediaViewerViewModel
import com.halo.presentation.search.general.SearchViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * @author ndn
 * Created by ndn
 * Created on 6/1/18.
 */
@Module
abstract class ViewModelModule {
    @Binds
    abstract fun bindViewModelFactory(factory: HahaloloViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(LanguageViewModel::class)
    abstract fun bindLanguageViewModel(viewModel: LanguageViewModel): ViewModel

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(MediaViewerViewModel::class)
    abstract fun bindImageViewerViewModel(viewModel: MediaViewerViewModel): ViewModel

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(SearchViewModel::class)
    abstract fun bindSearchViewModel(viewModel: SearchViewModel?): ViewModel?

}