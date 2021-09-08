/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.data.repository

import com.halo.data.repository.attachment.AttachmentRepository
import com.halo.data.repository.attachment.AttachmentRepositoryImpl
import com.halo.data.api.reaction.ReactionRestApiImpl
import com.halo.data.repository.contact.ContactRepository
import com.halo.data.repository.contact.ContactRepositoryImpl
import com.halo.data.repository.channel.ChannelRepository
import com.halo.data.repository.channel.ChannelRepositoryImpl
import com.halo.data.repository.invite.InviteRepository
import com.halo.data.repository.invite.InviteRepositoryImpl
import com.halo.data.repository.member.MemberRepository
import com.halo.data.repository.member.MemberRepositoryImpl
import com.halo.data.repository.tet.TetRepository
import com.halo.data.repository.tet.TetRepositoryImpl
import com.halo.data.repository.message.MessageRepository
import com.halo.data.repository.message.MessageRepositoryImpl
import com.halo.data.repository.oauth.MessRepository
import com.halo.data.repository.oauth.MessRepositoryImpl
import com.halo.data.repository.user.UserRepository
import com.halo.data.repository.user.UserRepositoryImpl
import com.halo.data.repository.notification.NotificationRepository
import com.halo.data.repository.notification.NotificationRepositoryImpl
import com.halo.data.repository.reaction.ReactionRepository
import com.halo.data.repository.reaction.ReactionRepositoryImpl
import com.halo.data.repository.role.RoleRepository
import com.halo.data.repository.role.RoleRepositoryImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

/**
 * @author ndn
 * Created by ndn
 * Created on 5/16/18.
 */
@Module
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindsTetRepository(repository: TetRepositoryImpl): TetRepository

    @Singleton
    @Binds
    abstract fun bindsMessRepository(repository: MessRepositoryImpl): MessRepository

    @Singleton
    @Binds
    abstract fun bindsUserRepository(repository: UserRepositoryImpl): UserRepository

    @Singleton
    @Binds
    abstract fun bindsNotificationRepository(repository: NotificationRepositoryImpl): NotificationRepository

    @Singleton
    @Binds
    abstract fun bindsContactRepository(repository: ContactRepositoryImpl): ContactRepository

    @Singleton
    @Binds
    abstract fun bindChannelRepositoryImpl(repository: ChannelRepositoryImpl): ChannelRepository

    @Singleton
    @Binds
    abstract fun bindMessageRepositoryImpl(repository: MessageRepositoryImpl): MessageRepository

    @Singleton
    @Binds
    abstract fun bindMemberRepository(repository: MemberRepositoryImpl): MemberRepository

    @Singleton
    @Binds
    abstract fun bindAttachmentRepository(repository: AttachmentRepositoryImpl): AttachmentRepository

    @Singleton
    @Binds
    abstract fun bindReactionRepository(repository: ReactionRepositoryImpl): ReactionRepository

    @Singleton
    @Binds
    abstract fun bindInviteRepository(repository: InviteRepositoryImpl): InviteRepository

    @Singleton
    @Binds
    abstract fun bindRoleRepository(repository: RoleRepositoryImpl): RoleRepository
}