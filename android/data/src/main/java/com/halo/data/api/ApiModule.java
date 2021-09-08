/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.api;

import com.halo.data.api.attachment.AttachmentRestApi;
import com.halo.data.api.attachment.AttachmentRestApiImpl;
import com.halo.data.api.channel.ChannelRestApi;
import com.halo.data.api.channel.ChannelRestApiImpl;
import com.halo.data.api.contact.ContactRestApi;
import com.halo.data.api.contact.ContactRestApiImpl;
import com.halo.data.api.invite.InviteResApi;
import com.halo.data.api.invite.InviteResApiImpl;
import com.halo.data.api.gateway.GateWayRestApi;
import com.halo.data.api.gateway.GateWayRestApiImpl;
import com.halo.data.api.member.MemberRestApi;
import com.halo.data.api.member.MemberRestApiImpl;
import com.halo.data.api.mess.oauth.MessOauthRestApi;
import com.halo.data.api.mess.oauth.MessOauthRestApiImpl;
import com.halo.data.api.mess.user.UserMessRestApi;
import com.halo.data.api.mess.user.UserMessRestApiImpl;
import com.halo.data.api.message.MessageRestApi;
import com.halo.data.api.message.MessageRestApiImpl;
import com.halo.data.api.notification.NotificationRestApi;
import com.halo.data.api.notification.NotificationRestApiImpl;
import com.halo.data.api.reaction.ReactionRestApi;
import com.halo.data.api.reaction.ReactionRestApiImpl;
import com.halo.data.api.readstate.ReadStateRestApi;
import com.halo.data.api.readstate.ReadStateRestApiImpl;
import com.halo.data.api.role.RoleRestApi;
import com.halo.data.api.role.RoleRestApiImpl;
import com.halo.data.api.tet.TetMongoRestApi;
import com.halo.data.api.tet.TetMongoRestApiImpl;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

/**
 * @author ndn
 * Created by ndn
 * Created on 5/16/18.
 */
@Module
public abstract class ApiModule {

    @Singleton
    @Binds
    abstract GateWayRestApi bindsGateWayRestApi(GateWayRestApiImpl restApi);

    @Singleton
    @Binds
    abstract TetMongoRestApi bindsTet(TetMongoRestApiImpl restApi);

    @Singleton
    @Binds
    abstract MessOauthRestApi bindsMessOauthRestApi(MessOauthRestApiImpl restApi);

    @Singleton
    @Binds
    abstract UserMessRestApi bindsUserMessRestApi(UserMessRestApiImpl restApi);

    @Singleton
    @Binds
    abstract NotificationRestApi bindsNotificationRestApi(NotificationRestApiImpl restApi);

    @Singleton
    @Binds
    abstract ContactRestApi bindsContactRestApi(ContactRestApiImpl restApi);

    @Singleton
    @Binds
    abstract ChannelRestApi bindsChannelRestApi(ChannelRestApiImpl restApi);

    @Singleton
    @Binds
    abstract MessageRestApi bindsMessageRestApi(MessageRestApiImpl restApi);

    @Singleton
    @Binds
    abstract MemberRestApi bindsMemberRestApi(MemberRestApiImpl restApi);

    @Singleton
    @Binds
    abstract AttachmentRestApi bindsAttachmentRestApi(AttachmentRestApiImpl restApi);

    @Singleton
    @Binds
    abstract ReactionRestApi bindsReactionRestApi(ReactionRestApiImpl restApi);

    @Singleton
    @Binds
    abstract InviteResApi bindsInviteResApi(InviteResApiImpl restApi);

    @Singleton
    @Binds
    abstract RoleRestApi bindsRoleRestApi(RoleRestApiImpl restApi);

    @Singleton
    @Binds
    abstract ReadStateRestApi bindsReadStateRestApi(ReadStateRestApiImpl restApi);
}
