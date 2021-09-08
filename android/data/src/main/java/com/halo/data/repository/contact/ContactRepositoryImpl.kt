/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.repository.contact

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.halo.data.api.contact.ContactRestApi
import com.halo.data.cache.db.mess.MessDao
import com.halo.data.common.resource.Resource
import com.halo.data.common.utils.Strings
import com.halo.data.entities.contact.Contact
import com.halo.data.entities.contact.ContactAliasBody
import com.halo.data.entities.contact.ContactBody
import com.halo.data.entities.contact.ContactDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author ngannd
 * Create by ngannd on 24/05/2019
 */
@Singleton
class ContactRepositoryImpl
@Inject constructor(
    private val contactRestApi: ContactRestApi,
    val messDao: MessDao
) : ContactRepository {

    override suspend fun contactPaging(
        token: String?
    ): Flow<PagingData<Contact>> =
        Pager(PagingConfig(pageSize = 20)) {
            ContactPagingSource(
                api = contactRestApi,
                token = token ?: "",
            )
        }.flow

    override suspend fun contactDetail(
        token: String?,
        contactId: String
    ): Flow<Resource<Contact>> = flow {
        emit(Resource.loading<Contact>(null))
        val resource = kotlin.runCatching {
            contactRestApi.contactsDetail(
                token = token ?: "",
                contactId = contactId
            )?.run {
                Resource.success(this)
            } ?: kotlin.run {
                Resource.error<Contact>(500, "errors", null)
            }
        }.getOrElse {
            Resource.error(it)
        }
        emit(resource)
    }.catch {
        Resource.error<Contact>(it)
    }.flowOn(Dispatchers.IO)


    override suspend fun userMe(
        token: String
    ): Flow<Resource<Contact>> = flow {
        emit(Resource.loading<Contact>(null))
        val resource = kotlin.runCatching {
            contactRestApi.profileOwner(
                token = token ?: ""
            )?.run {
                Resource.success(this)
            } ?: kotlin.run {
                Resource.error<Contact>(500, "errors", null)
            }
        }.getOrElse {
            Resource.error(it)
        }
        emit(resource)
    }.catch {
        Resource.error<Contact>(it)
    }.flowOn(Dispatchers.IO)


    override suspend fun updateName(
        token: String?,
        contactId: String,
        body: ContactBody?
    ): Flow<Resource<Contact>> = flow {
        emit(Resource.loading<Contact>(null))
        val resource = kotlin.runCatching {
            contactRestApi.updateName(
                token = token ?: "",
                contactId = contactId,
                body
            )?.run {
                Resource.success(this)
            } ?: kotlin.run {
                Resource.error<Contact>(500, "errors", null)
            }
        }.getOrElse {
            Resource.error(it)
        }
        emit(resource)
    }.catch {
        Resource.error<Contact>(it)
    }.flowOn(Dispatchers.IO)


    override suspend fun updateAlias(
        token: String?,
        contactId: String,
        body: ContactAliasBody?
    ): Flow<Resource<Contact>> = flow{
        emit(Resource.loading<Contact>(null))
        val resource = kotlin.runCatching {
            contactRestApi.updateAlias(
                token = token ?: "",
                contactId = contactId,
                body
            )?.run {
                Resource.success(this)
            } ?: kotlin.run {
                Resource.error<Contact>(500, "errors", null)
            }
        }.getOrElse {
            Resource.error(it)
        }
        emit(resource)
    }.catch {
        Resource.error<Contact>(it)
    }.flowOn(Dispatchers.IO)

}