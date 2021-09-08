package com.hahalolo.incognito.di.setting

import androidx.lifecycle.ViewModel
import com.hahalolo.incognito.presentation.setting.channel.IncognitoChannelSettingViewModel
import com.hahalolo.incognito.presentation.setting.general.IncognitoSettingGeneralViewModel
import com.hahalolo.incognito.presentation.setting.invite.IncognitoInviteFriendViewModel
import com.hahalolo.incognito.presentation.setting.managerfile.IncognitoManagerFileViewModel
import com.hahalolo.incognito.presentation.setting.member.IncognitoMemberSettingViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class IncognitoSettingViewModelModule {
    /*START -> ViewModel*/
    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(IncognitoMemberSettingViewModel::class)
    abstract fun bindIncognitoProfileViewModel(viewModel: IncognitoMemberSettingViewModel): ViewModel

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(IncognitoChannelSettingViewModel::class)
    abstract fun bindIncognitoChannelSettingViewModel(viewModel: IncognitoChannelSettingViewModel): ViewModel

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(IncognitoSettingGeneralViewModel::class)
    abstract fun bindIncognitoSettingViewModel(viewModel: IncognitoSettingGeneralViewModel): ViewModel

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(IncognitoManagerFileViewModel::class)
    abstract fun bindIncognitoManagerFileViewModel(viewModel: IncognitoManagerFileViewModel): ViewModel

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(IncognitoInviteFriendViewModel::class)
    abstract fun bindIncognitoInviteFriendViewModel(viewModel: IncognitoInviteFriendViewModel): ViewModel

    /*END -> ViewModel*/
}