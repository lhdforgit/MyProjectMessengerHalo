package com.halo.data.repository.contact

import androidx.paging.PagingData
import com.halo.data.common.resource.Resource
import com.halo.data.entities.contact.Contact
import com.halo.data.entities.contact.ContactAliasBody
import com.halo.data.entities.contact.ContactBody
import com.halo.data.entities.contact.ContactDetail
import kotlinx.coroutines.flow.Flow

/**
 * Create by ndn
 * Create on 6/8/21
 * com.halo.data.repository.user
 */
interface ContactRepository {
    suspend fun contactPaging(
        token: String? = null
    ): Flow<PagingData<Contact>>

    suspend fun contactDetail(
        token: String?,
        contactId: String
    ): Flow<Resource<Contact>>

    suspend fun userMe(
        token: String
    ): Flow<Resource<Contact>>

    suspend fun updateName(
        token: String?,
        contactId: String,
        body: ContactBody?
    ): Flow<Resource<Contact>>

    suspend fun updateAlias(
        token: String?,
        contactId: String,
        body: ContactAliasBody?
    ): Flow<Resource<Contact>>
}