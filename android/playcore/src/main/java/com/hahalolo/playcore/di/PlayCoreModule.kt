package com.hahalolo.playcore.di

import androidx.lifecycle.ViewModel
import com.hahalolo.playcore.language.PlayCoreLanguageAct
import com.hahalolo.playcore.language.PlayCoreLanguageViewModel
import com.hahalolo.playcore.update.PlayCoreUpdateAct
import com.hahalolo.playcore.update.PlayCoreUpdateViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class PlayCoreModule {

    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector
    abstract fun languageAct(): PlayCoreLanguageAct

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(PlayCoreLanguageViewModel::class)
    abstract fun bindPlayCoreLanguageViewModel(viewModel: PlayCoreLanguageViewModel): ViewModel

    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector()
    abstract fun update(): PlayCoreUpdateAct

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(PlayCoreUpdateViewModel::class)
    abstract fun bindUpdateViewModel(viewModel: PlayCoreUpdateViewModel?): ViewModel
}