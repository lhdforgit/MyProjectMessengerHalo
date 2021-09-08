package com.hahalolo.incognito.di.owner

import androidx.lifecycle.ViewModel
import com.hahalolo.incognito.presentation.main.owner.IncognitoOwnerProfileViewModel
import com.hahalolo.incognito.presentation.main.owner.message.spam.IncognitoOwnerMessageSpamModel
import com.hahalolo.incognito.presentation.main.owner.message.mailbox.IncognitoOwnerMailBoxViewModel
import com.hahalolo.incognito.presentation.main.owner.message.tick.IncognitoOwnerMessageTickModel
import com.hahalolo.incognito.presentation.main.owner.message.waiting.IncognitoOwnerMessageWaitingModel
import com.hahalolo.incognito.presentation.main.owner.update.IncognitoOwnerUpdateProfileViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class IncognitoOwnerViewModelModule{
    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(IncognitoOwnerProfileViewModel::class)
    abstract fun bindIncognitoOwnerProfileViewModel(viewModel: IncognitoOwnerProfileViewModel): ViewModel

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(IncognitoOwnerMessageWaitingModel::class)
    abstract fun bindIncognitoOwnerMessageWaitingModel(viewModel: IncognitoOwnerMessageWaitingModel): ViewModel

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(IncognitoOwnerMessageSpamModel::class)
    abstract fun bindIncognitoOwnerMessageSpamModel(viewModel: IncognitoOwnerMessageSpamModel): ViewModel

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(IncognitoOwnerMessageTickModel::class)
    abstract fun bindIncognitoOwnerMessageTickModel(viewModel: IncognitoOwnerMessageTickModel): ViewModel

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(IncognitoOwnerMailBoxViewModel::class)
    abstract fun bindIncognitoMailBoxViewModel(viewModel: IncognitoOwnerMailBoxViewModel): ViewModel

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(IncognitoOwnerUpdateProfileViewModel::class)
    abstract fun bindIncognitoOwnerUpdateProfileViewModel(viewModel: IncognitoOwnerUpdateProfileViewModel): ViewModel
}