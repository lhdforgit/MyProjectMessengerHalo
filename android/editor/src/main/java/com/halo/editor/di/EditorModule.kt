/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.editor.di

import androidx.lifecycle.ViewModel
import com.halo.di.ActivityScoped
import com.halo.di.ViewModelKey
import com.halo.editor.mediasend.*
import com.halo.editor.scribbles.sticker.ImageEditorStickerSelectActivity
import com.halo.editor.scribbles.sticker.ImageEditorStickerSelectViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import javax.inject.Singleton

/**
 * Created by ndngan
 * Created on 4/6/18.
 */
@Module
abstract class EditorModule {

    @ActivityScoped
    @ContributesAndroidInjector(modules = [MediaSendModule::class])
    abstract fun mediaSendAct(): MediaSendActivity?

    @Binds
    @IntoMap
    @ViewModelKey(MediaSendViewModel::class)
    abstract fun mediaSendViewModel(viewModel: MediaSendViewModel?): ViewModel


    @ActivityScoped
    @ContributesAndroidInjector
    abstract fun imageEditorStickerSelectAct(): ImageEditorStickerSelectActivity?

    @Binds
    @IntoMap
    @ViewModelKey(ImageEditorStickerSelectViewModel::class)
    abstract fun imageEditorStickerSelectViewModel(viewModel: ImageEditorStickerSelectViewModel?): ViewModel


    @ActivityScoped
    @ContributesAndroidInjector(modules = [MediaSendModule::class])
    abstract fun avatarSelection(): AvatarSelectionActivity?

    @ActivityScoped
    @ContributesAndroidInjector(modules = [MediaSendModule::class])
    abstract fun coverSelection(): CoverSelectionActivity?
}