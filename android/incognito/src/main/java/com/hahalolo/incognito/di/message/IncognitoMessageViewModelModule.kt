package com.hahalolo.incognito.di.message

import androidx.lifecycle.ViewModel
import com.hahalolo.incognito.presentation.conversation.IncognitoConversationViewModel
import com.hahalolo.incognito.presentation.conversation.thread.IncognitoMsgThreadViewModel
import com.hahalolo.incognito.presentation.create.forward.container.IncognitoForwardMessageViewModel
import com.hahalolo.incognito.presentation.main.IncognitoMainViewModel
import com.hahalolo.incognito.presentation.main.contact.IncognitoContactFrViewModel
import com.hahalolo.incognito.presentation.main.conversation.IncognitoConversationFrViewModel
import com.hahalolo.incognito.presentation.main.group.IncognitoGroupFrViewModel
import com.hahalolo.incognito.presentation.main.search.IncognitoSearchFrViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class IncognitoMessageViewModelModule{

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(IncognitoMainViewModel::class)
    abstract fun bindIncognitoMainViewModel(viewModel: IncognitoMainViewModel): ViewModel

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(IncognitoContactFrViewModel::class)
    abstract fun bindIncognitoContactFrViewModel(viewModel: IncognitoContactFrViewModel): ViewModel

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(IncognitoConversationFrViewModel::class)
    abstract fun bindIncognitoConversationFrViewModel(viewModel: IncognitoConversationFrViewModel): ViewModel

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(IncognitoGroupFrViewModel::class)
    abstract fun bindIncognitoGroupFrViewModel(viewModel: IncognitoGroupFrViewModel): ViewModel

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(IncognitoSearchFrViewModel::class)
    abstract fun bindIncognitoSearchFrViewModel(viewModel: IncognitoSearchFrViewModel): ViewModel


    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(IncognitoConversationViewModel::class)
    abstract fun bindIncognitoConversationViewModel(viewModel: IncognitoConversationViewModel): ViewModel


    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(IncognitoMsgThreadViewModel::class)
    abstract fun bindIncognitoMsgThreadViewModel(viewModel: IncognitoMsgThreadViewModel): ViewModel

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(IncognitoForwardMessageViewModel::class)
    abstract fun bindIncognitoForwardMessageViewModel(viewModel: IncognitoForwardMessageViewModel): ViewModel
}