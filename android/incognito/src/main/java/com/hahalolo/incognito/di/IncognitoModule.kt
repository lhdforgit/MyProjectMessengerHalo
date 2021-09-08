package com.hahalolo.incognito.di

import androidx.lifecycle.ViewModel
import com.hahalolo.incognito.di.message.IncognitoMessageActModule
import com.hahalolo.incognito.di.message.IncognitoMessageViewModelModule
import com.hahalolo.incognito.di.owner.IncognitoOwnerActModule
import com.hahalolo.incognito.di.setting.IncognitoSettingActModule
import com.hahalolo.incognito.di.setting.IncognitoSettingViewModelModule
import com.hahalolo.incognito.di.contact.IncognitoContactActModule
import com.hahalolo.incognito.di.contact.IncognitoContactViewModelModule
import com.hahalolo.incognito.di.owner.IncognitoOwnerViewModelModule
import com.hahalolo.incognito.presentation.create.conversation.IncognitoCreateConversationAct
import com.hahalolo.incognito.presentation.create.conversation.IncognitoCreateConversationViewModel
import com.hahalolo.incognito.presentation.create.channel.IncognitoCreateChannelAct
import com.hahalolo.incognito.presentation.create.channel.IncognitoCreateChannelViewModel
import com.hahalolo.incognito.presentation.login.IncognitoLoginAct
import com.hahalolo.incognito.presentation.login.IncognitoLoginViewModel
import com.hahalolo.incognito.presentation.login.confirm.IncognitoConfirmLoginAct
import com.hahalolo.incognito.presentation.login.confirm.IncognitoConfirmLoginViewModel
import com.hahalolo.incognito.presentation.search.IncognitoSearchAct
import com.hahalolo.incognito.presentation.search.IncognitoSearchViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Create by ndn
 * Create on 5/26/21
 * com.hahalolo.incognito.di
 */
@Module(includes = [

    /*MESSAGE*/
    IncognitoMessageActModule::class,
    IncognitoMessageViewModelModule::class,

    /*SETTING*/
    IncognitoSettingActModule::class,
    IncognitoSettingViewModelModule::class,

    /*CONTACT AND USER*/
    IncognitoContactActModule::class,
    IncognitoContactViewModelModule::class,

    /*OWNER*/
    IncognitoOwnerActModule::class,
    IncognitoOwnerViewModelModule::class,
] )
abstract class IncognitoModule {

    /*Activity*/

    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector
    abstract fun incognitoLoginAct(): IncognitoLoginAct

    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector
    abstract fun incognitoConfirmLoginAct(): IncognitoConfirmLoginAct


    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector
    abstract fun incognitoCreateGroupAct(): IncognitoCreateChannelAct

    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector
    abstract fun incognitoCreateConversationAct(): IncognitoCreateConversationAct

    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector
    abstract fun incognitoSearchAct(): IncognitoSearchAct


    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(IncognitoLoginViewModel::class)
    abstract fun bindIncognitoLoginViewModel(viewModel: IncognitoLoginViewModel): ViewModel

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(IncognitoConfirmLoginViewModel::class)
    abstract fun bindIncognitoConfirmLoginViewModel(viewModel: IncognitoConfirmLoginViewModel): ViewModel


    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(IncognitoCreateChannelViewModel::class)
    abstract fun bindIncognitoCreateGroupViewModel(viewModel: IncognitoCreateChannelViewModel): ViewModel

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(IncognitoCreateConversationViewModel::class)
    abstract fun bindIncognitoCreateConversationViewModel(viewModel: IncognitoCreateConversationViewModel): ViewModel

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(IncognitoSearchViewModel::class)
    abstract fun bindIncognitoSearchViewModel(viewModel: IncognitoSearchViewModel): ViewModel

}