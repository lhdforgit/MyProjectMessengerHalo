package com.hahalolo.incognito.di.message

import com.hahalolo.incognito.presentation.conversation.IncognitoConversationAct
import com.hahalolo.incognito.presentation.conversation.thread.IncognitoMsgThreadAct
import com.hahalolo.incognito.presentation.create.forward.IncognitoForwardModule
import com.hahalolo.incognito.presentation.main.IncognitoMainAct
import com.hahalolo.incognito.presentation.main.IncognitoMainModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class IncognitoMessageActModule {
    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector(modules = [IncognitoMainModule::class])
    abstract fun incognitoMainAct(): IncognitoMainAct

    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector(modules = [IncognitoForwardModule::class])
    abstract fun incognitoConversationAct(): IncognitoConversationAct

    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector
    abstract fun incognitoMsgThreadAct(): IncognitoMsgThreadAct
}