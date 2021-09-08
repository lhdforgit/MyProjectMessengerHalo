package com.hahalolo.messager.presentation.forward_message

import com.hahalolo.messager.presentation.forward_message.container.ForwardMessageFr
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ForwardMessageModule {

    @com.halo.di.FragmentScoped
    @ContributesAndroidInjector
    abstract fun forwardMessageBottomSheet(): ForwardMessageBottomSheet

    @com.halo.di.FragmentScoped
    @ContributesAndroidInjector
    abstract fun forwardMessageFr(): ForwardMessageFr
}