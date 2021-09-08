package com.hahalolo.messager.di

import com.hahalolo.messager.presentation.download.ChatDownloadFileAct
import com.hahalolo.messager.presentation.forward_message.ForwardMessageModule
import com.hahalolo.messager.presentation.group.create.ChatGroupCreateAct
import com.hahalolo.messager.presentation.group.detail.ChatGroupDetailAct
import com.hahalolo.messager.presentation.group.gallery.ChatRoomGalleryAct
import com.hahalolo.messager.presentation.group.gallery.ChatRoomGalleryModule
import com.hahalolo.messager.presentation.group.member.ChatGroupMemberAct
import com.hahalolo.messager.presentation.group.name.ChatChangeGroupNameAct
import com.hahalolo.messager.presentation.main.ChatAct
import com.hahalolo.messager.presentation.main.ChatModule
import com.hahalolo.messager.presentation.message.ChatMessageAct
import com.hahalolo.messager.presentation.search.ChatSearchAct
import com.hahalolo.messager.presentation.search.ChatSearchModule
import com.hahalolo.messager.presentation.suggest.ContactSuggestAct
import com.hahalolo.messager.presentation.suggest.ContactSuggestModule
import com.hahalolo.messager.presentation.takeImage.ChatTakeImageAct
import com.hahalolo.messager.presentation.write_message.ChatWriteMessageAct
import com.hahalolo.messager.presentation.main.contacts.owner.ChatOwnerAct
import com.hahalolo.messager.presentation.main.contacts.ChatContactsModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Create by ndn
 * Create on 5/26/21
 * com.hahalolo.incognito.di
 */
@Module(
    includes = [
        ChatContactsModule::class,
        ForwardMessageModule::class
    ]
)
abstract class MessengerActModule {

    /*Activity*/
    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector(modules = [ChatModule::class])
    abstract fun chatAct(): ChatAct?

    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector
    abstract fun chatMessageAct(): ChatMessageAct?

    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector
    abstract fun createGroupAct(): ChatGroupCreateAct?

    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector
    abstract fun chatMessageMemberAct(): ChatGroupMemberAct?

    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector(modules = [ChatSearchModule::class])
    abstract fun chatSearchAct(): ChatSearchAct?

    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector
    abstract fun chatMessageChangeGroupNameAct(): ChatChangeGroupNameAct?

    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector
    abstract fun chatMessageGeneralSettingAct(): ChatGroupDetailAct?

    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector(modules = [ChatRoomGalleryModule::class])
    abstract fun chatRoomGalleryAct(): ChatRoomGalleryAct?

    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector
    abstract fun chatWriteMessageAct(): ChatWriteMessageAct?

    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector(modules = [ContactSuggestModule::class])
    abstract fun chatContactSuggestAct(): ContactSuggestAct?

    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector
    abstract fun chatMessageSettingAct(): ChatOwnerAct?

    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector
    abstract fun chatChatTakeImageAct(): ChatTakeImageAct?

    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector
    abstract fun chatDownloadFileAct(): ChatDownloadFileAct?

}