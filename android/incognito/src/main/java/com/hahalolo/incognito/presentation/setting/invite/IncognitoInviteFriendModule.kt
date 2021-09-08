package com.hahalolo.incognito.presentation.setting.invite

import com.hahalolo.incognito.presentation.setting.invite.channel.IncognitoInviteChannelFr
import com.hahalolo.incognito.presentation.setting.invite.workspace.IncognitoInviteWorkspaceFr
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class IncognitoInviteFriendModule {

    @com.halo.di.FragmentScoped
    @ContributesAndroidInjector
    abstract fun incognitoInviteChannelFr(): IncognitoInviteChannelFr

    @com.halo.di.FragmentScoped
    @ContributesAndroidInjector
    abstract fun incognitoInviteWorkspaceFr(): IncognitoInviteWorkspaceFr

}