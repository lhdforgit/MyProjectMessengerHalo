package com.hahalolo.messager.bubble

import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class ChatBubbleModule{

    @ContributesAndroidInjector
    abstract fun bubbleService(): BubbleService
}