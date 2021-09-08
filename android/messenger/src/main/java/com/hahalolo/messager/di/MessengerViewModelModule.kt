package com.hahalolo.messager.di

import androidx.lifecycle.ViewModel
import com.hahalolo.messager.presentation.forward_message.container.ForwardMessageViewModel
import com.hahalolo.messager.presentation.group.create.ChatGroupCreateViewModel
import com.hahalolo.messager.presentation.group.detail.ChatGroupDetailViewModel
import com.hahalolo.messager.presentation.group.gallery.ChatRoomGalleryViewModel
import com.hahalolo.messager.presentation.group.gallery.doc.ChatRoomGalleryDocViewModel
import com.hahalolo.messager.presentation.group.gallery.link.ChatRoomGalleryLinkViewModel
import com.hahalolo.messager.presentation.group.gallery.photo.ChatRoomGalleryPhotoViewModel
import com.hahalolo.messager.presentation.group.member.ChatGroupMemberViewModel
import com.hahalolo.messager.presentation.group.name.ChatChangeGroupNameViewModel
import com.hahalolo.messager.presentation.main.contacts.owner.ChatOwnerViewModel
import com.hahalolo.messager.presentation.main.ChatViewModel
import com.hahalolo.messager.presentation.main.contacts.ChatContactsViewModel
import com.hahalolo.messager.presentation.main.contacts.detail.ChatContactsDetailViewModel
import com.hahalolo.messager.presentation.main.conversation.ConversationListViewModel
import com.hahalolo.messager.presentation.main.group.ChatHomeGroupViewModel
import com.hahalolo.messager.presentation.message.ChatMessageViewModel
import com.hahalolo.messager.presentation.search.ChatSearchViewModel
import com.hahalolo.messager.presentation.search.friend.ChatSearchFriendViewModel
import com.hahalolo.messager.presentation.search.group.ChatSearchGroupViewModel
import com.hahalolo.messager.presentation.search.user.ChatSearchUserViewModel
import com.hahalolo.messager.presentation.suggest.ContactsSuggestViewModel
import com.hahalolo.messager.presentation.takeImage.ChatTakeImageViewModel
import com.hahalolo.messager.presentation.write_message.ChatWriteMessageViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MessengerViewModelModule {
    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(ChatViewModel::class)
    abstract fun bindChatViewModel(viewModel: ChatViewModel?): ViewModel?

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(ConversationListViewModel::class)
    abstract fun bindChatHomeViewModel(viewModel: ConversationListViewModel?): ViewModel?

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(ChatHomeGroupViewModel::class)
    abstract fun bindChatHomeGroupViewModel(viewModel: ChatHomeGroupViewModel?): ViewModel?

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(ChatContactsViewModel::class)
    abstract fun bindChatContactsViewModel(viewModel: ChatContactsViewModel?): ViewModel?

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(ChatMessageViewModel::class)
    abstract fun bindChatMessageViewModel(viewModel: ChatMessageViewModel?): ViewModel?

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(ChatGroupCreateViewModel::class)
    abstract fun bindCreateGroupViewModel(viewModel: ChatGroupCreateViewModel?): ViewModel?

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(ChatGroupMemberViewModel::class)
    abstract fun bindChatMessageMemberViewModel(viewModel: ChatGroupMemberViewModel?): ViewModel?

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(ChatSearchViewModel::class)
    abstract fun bindChatSearchViewModel(viewModel: ChatSearchViewModel?): ViewModel?

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(ChatChangeGroupNameViewModel::class)
    abstract fun bindChatMessageChangeGroupNameViewModel(viewModel: ChatChangeGroupNameViewModel?): ViewModel?

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(ChatSearchGroupViewModel::class)
    abstract fun bindChatSearchGroupViewModel(viewModel: ChatSearchGroupViewModel?): ViewModel?

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(ChatSearchFriendViewModel::class)
    abstract fun bindChatSearchFriendViewModel(viewModel: ChatSearchFriendViewModel?): ViewModel?

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(ChatGroupDetailViewModel::class)
    abstract fun bindChatMessageGeneralSettingViewModel(viewModel: ChatGroupDetailViewModel?): ViewModel?

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(ChatRoomGalleryViewModel::class)
    abstract fun bindChatRoomGalleryViewModel(viewModel: ChatRoomGalleryViewModel?): ViewModel?

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(ChatRoomGalleryPhotoViewModel::class)
    abstract fun bindChatRoomGalleryPhotoViewModel(viewModel: ChatRoomGalleryPhotoViewModel?): ViewModel?

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(ChatRoomGalleryLinkViewModel::class)
    abstract fun bindChatRoomGalleryLinkViewModel(viewModel: ChatRoomGalleryLinkViewModel?): ViewModel?

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(ChatRoomGalleryDocViewModel::class)
    abstract fun bindChatRoomGalleryDocViewModel(viewModel: ChatRoomGalleryDocViewModel?): ViewModel?

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(ChatWriteMessageViewModel::class)
    abstract fun bindChatWriteMessageViewModel(viewModel: ChatWriteMessageViewModel?): ViewModel?

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(ContactsSuggestViewModel::class)
    abstract fun bindChatContactsSuggestViewModel(viewModel: ContactsSuggestViewModel?): ViewModel?

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(ChatTakeImageViewModel::class)
    abstract fun bindChatTakeImageViewModel(viewModel: ChatTakeImageViewModel?): ViewModel?

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(ChatOwnerViewModel::class)
    abstract fun bindChatMessageSettingViewModel(viewModel: ChatOwnerViewModel?): ViewModel?

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(ChatContactsDetailViewModel::class)
    abstract fun bindChatContactsDetailViewModel(viewModel: ChatContactsDetailViewModel?): ViewModel?

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(ChatSearchUserViewModel::class)
    abstract fun bindChatSearchUserViewModel(viewModel: ChatSearchUserViewModel?): ViewModel?

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(ForwardMessageViewModel::class)
    abstract fun bindForwardMessageViewModel(viewModel: ForwardMessageViewModel?): ViewModel?

}