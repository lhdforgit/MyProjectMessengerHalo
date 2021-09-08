/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.api.contact

import com.halo.data.api.channel.ChannelService
import com.halo.data.api.mess.user.UserMessService
import com.halo.data.api.utils.ServiceGenerator
import com.halo.data.entities.contact.*
import com.halo.data.entities.mongo.tet.GifCard
import com.halo.data.entities.response.ResponsePaging
import okhttp3.MultipartBody
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author ndn
 * Created by ndn
 * Created on 9/6/18
 */
@Singleton
class ContactRestApiImpl @Inject
internal constructor() : ContactRestApi {

    override suspend fun contacts(
        token: String,
        limit: Int,
        page: Int,
        status: String
    ) = ServiceGenerator.createMessService(
        serviceClass = ContactService::class.java,
        authorization = token
    ).contacts(limit, page, status)

    override suspend fun contactsV2(
        token: String?,
        limit: Int,
        since: String?,
        status: String
    ): ResponsePaging<Contact>? = ServiceGenerator.createMessService(
        serviceClass = ContactService::class.java,
        authorization = token
    ).contactsV2(limit, since, status)

    override suspend fun contactsDetail(
        token: String,
        contactId: String
    ) = ServiceGenerator.createMessService(
        serviceClass = ContactService::class.java,
        authorization = token
    ).contactsDetail(contactId)

    override suspend fun contactsRefresh(token: String): ContactRefreshResponse? =
        ServiceGenerator.createMessService(
            serviceClass = ContactService::class.java,
            authorization = token
        ).contactsRefresh()

    override suspend fun profileOwner(
        token: String
    ) = ServiceGenerator.createMessService(
        serviceClass = ContactService::class.java,
        authorization = token
    ).profileOwner()

    override suspend fun updateName(
        token: String,
        contactId: String,
        body: ContactBody?
    ) = ServiceGenerator.createMessService(
        serviceClass = ContactService::class.java,
        authorization = token
    ).updateName(contactId, body)

    override suspend fun updateAlias(
        token: String,
        contactId: String,
        body: ContactAliasBody?
    ) = ServiceGenerator.createMessService(
        serviceClass = ContactService::class.java,
        authorization = token
    ).updateAlias(contactId, body)

}