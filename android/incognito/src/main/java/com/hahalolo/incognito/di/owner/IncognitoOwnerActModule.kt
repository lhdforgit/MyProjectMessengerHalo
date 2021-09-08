package com.hahalolo.incognito.di.owner

import com.hahalolo.incognito.presentation.main.owner.IncognitoOwnerProfileAct
import com.hahalolo.incognito.presentation.main.owner.message.spam.IncognitoOwnerMessageSpamAct
import com.hahalolo.incognito.presentation.main.owner.message.mailbox.IncognitoOwnerMailBoxAct
import com.hahalolo.incognito.presentation.main.owner.message.tick.IncognitoOwnerMessageTickAct
import com.hahalolo.incognito.presentation.main.owner.message.waiting.IncognitoOwnerMessageWaitingAct
import com.hahalolo.incognito.presentation.main.owner.update.IncognitoOwnerUpdateProfileAct
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class IncognitoOwnerActModule {
    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector
    abstract fun incognitoOwnerProfileAct(): IncognitoOwnerProfileAct

    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector
    abstract fun incognitoOwnerMessageWaitingAct(): IncognitoOwnerMessageWaitingAct

    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector
    abstract fun incognitoOwnerMessageSpamAct(): IncognitoOwnerMessageSpamAct

    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector
    abstract fun incognitoOwnerMessageTickAct(): IncognitoOwnerMessageTickAct

    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector
    abstract fun incognitoOwnerMailBoxAct(): IncognitoOwnerMailBoxAct

    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector
    abstract fun incognitoOwnerUpdateProfileAct(): IncognitoOwnerUpdateProfileAct

}