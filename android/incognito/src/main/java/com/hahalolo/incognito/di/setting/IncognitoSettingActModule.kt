package com.hahalolo.incognito.di.setting

import com.hahalolo.incognito.presentation.setting.channel.IncognitoChannelSettingAct
import com.hahalolo.incognito.presentation.setting.invite.IncognitoInviteFriendAct
import com.hahalolo.incognito.presentation.setting.invite.IncognitoInviteFriendModule
import com.hahalolo.incognito.presentation.setting.general.IncognitoSettingGeneralAct
import com.hahalolo.incognito.presentation.setting.managerfile.IncognitoManagerFileAct
import com.hahalolo.incognito.presentation.setting.managerfile.IncognitoManagerFileModule
import com.hahalolo.incognito.presentation.setting.member.IncognitoMemberSettingAct
import com.hahalolo.incognito.presentation.setting.member.list.IncognitoMemberListAct
import com.hahalolo.incognito.presentation.setting.notification.IncognitoSettingNotificationAct
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class IncognitoSettingActModule {
    /* START -> Activity*/
    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector
    abstract fun incognitoMemberSettingAct(): IncognitoMemberSettingAct

    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector
    abstract fun incognitoChannelSettingAct(): IncognitoChannelSettingAct

    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector(modules = [IncognitoManagerFileModule::class])
    abstract fun incognitoManagerFileAct(): IncognitoManagerFileAct

    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector
    abstract fun incognitoMemberListAct(): IncognitoMemberListAct

    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector
    abstract fun incognitoSettingGeneralAct(): IncognitoSettingGeneralAct

    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector
    abstract fun incognitoSettingNotificationAct(): IncognitoSettingNotificationAct


    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector(modules = [IncognitoInviteFriendModule::class])
    abstract fun incognitoInviteFriendAct(): IncognitoInviteFriendAct

    /*END ->Activity*/

}