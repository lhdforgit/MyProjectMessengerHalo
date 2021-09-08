/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.repository.user

import com.halo.data.api.contact.ContactRestApi
import com.halo.data.api.mess.user.UserMessRestApi
import com.halo.data.cache.db.mess.MessDao
import com.halo.data.common.resource.Resource
import com.halo.data.entities.user.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author ngannd
 * Create by ngannd on 24/05/2019
 */
@Singleton
class UserRepositoryImpl
@Inject constructor(
    private val userMessRestApi: UserMessRestApi,
    private val contactRestApi: ContactRestApi,
    val messDao: MessDao
) : UserRepository {

    override suspend fun user(
        id: String?,
        token: String?
    ) = flow {
        emit(Resource.loading<User>(null))
        val resource = kotlin.runCatching {
            //val contacts =contactRestApi.contacts(token?:"", 20, 1)
            userMessRestApi.user(
                id = id,
                token = token
            )?.run {
                Resource.success(this)
            }?: run {
                Resource.error<User>(500, "errors", null)
            }
        }.getOrElse {
            Resource.error<User>(it)
        }
        emit(resource)
    }.catch {
        Resource.error<User>(it)
    }.flowOn(Dispatchers.IO)

    override suspend fun searchUser(
        token: String,
        keyword: String,
        limit: String
    ) : Flow<Resource<MutableList<User>>> = flow {
        emit(Resource.loading<MutableList<User>>(null))
        val resource = kotlin.runCatching {
            userMessRestApi.searchUser(token, keyword, limit)?.run {
                Resource.success(this.elements)
            } ?: kotlin.run {
                Resource.error<MutableList<User>>(500, "errors", null)
            }
        }.getOrElse {
            Resource.error<MutableList<User>>(it)
        }
        emit(resource)
    }.catch {
        Resource.error<MutableList<User>>(it)
    }.flowOn(Dispatchers.IO)
}