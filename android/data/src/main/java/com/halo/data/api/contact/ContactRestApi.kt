/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.api.contact

import com.halo.data.entities.contact.*
import com.halo.data.entities.response.ResponsePaging

/**
 * @author ndn
 * Created by ndn
 * Created on 8/21/18
 */
interface ContactRestApi {
    suspend fun contacts(
        token: String,
        limit: Int,
        page: Int,
        status: String = ContactType.CONTACTED
    ): ResponsePaging<Contact>?

    suspend fun contactsV2(
        token: String?,
        limit: Int,
        since: String?,
        status: String = ContactType.CONTACTED
    ): ResponsePaging<Contact>?

    suspend fun contactsDetail(
        token: String,
        contactId: String
    ): Contact?

    suspend fun contactsRefresh(token: String): ContactRefreshResponse?

    suspend fun profileOwner(token: String): Contact?

    suspend fun updateName(
        token: String,
        contactId: String,
        body: ContactBody?
    ): Contact?

    suspend fun updateAlias(
        token: String,
        contactId: String,
        body: ContactAliasBody?
    ): Contact?
}
