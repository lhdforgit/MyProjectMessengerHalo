/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.repository.oauth

import com.google.gson.Gson
import com.halo.data.api.mess.oauth.MessOauthRestApi
import com.halo.data.api.mess.user.UserMessRestApi
import com.halo.data.cache.db.mess.MessDao
import com.halo.data.common.resource.Resource
import com.halo.data.entities.mess.VerifyBody
import com.halo.data.entities.mess.VerifyResponse
import com.halo.data.entities.mess.halo.HaloAuthorBody
import com.halo.data.entities.mess.oauth.MessOauth
import com.halo.data.entities.mess.oauth.OauthInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author ngannd
 * Create by ngannd on 24/05/2019
 */
@Singleton
class MessRepositoryImpl
@Inject constructor(
    private val messOauthRestApi: MessOauthRestApi,
    private val userRestApi: UserMessRestApi,
    val messDao: MessDao
) : MessRepository {

    override suspend fun verify(body: VerifyBody): Flow<Resource<MessOauth>> = flow {
        emit(Resource.loading<MessOauth>(null))

        val resource = kotlin.runCatching {
            Timber.i("Verify Body: ${Gson().toJson(body)}")

            messOauthRestApi.verify(body)?.run {
                Timber.i("Verify Response: $this")
                val userResponse = userRestApi.user(id = null, token = this.token)?.apply {
                    Timber.i("User Response: $this")
                }
                val info = userResponse?.run {
                    OauthInfo.mapperOauthInfo(userResponse)
                }

                val oauth = MessOauth(
                    token = this.token ?: "",
                    refreshToken = this.refreshToken,
                    info = Gson().toJson(info)
                )
                Timber.i("Oauth insert DB: $oauth")
                emptyOauth()
                messDao.insert(oauth)
                Resource.success<MessOauth>(oauth)
            } ?: kotlin.run {
                Resource.notFound<MessOauth>()
            }
        }.getOrElse {
            Resource.throwableError<MessOauth>(it)
        }
        emit(resource)
    }.catch {
        Resource.throwableError<MessOauth>(it)
    }.flowOn(Dispatchers.IO)

    override suspend fun authorize(body: VerifyBody): Flow<Resource<VerifyResponse>> = flow {
        emit(Resource.loading<VerifyResponse>(null))
        val resource = kotlin.runCatching {
            messOauthRestApi.authorize(body)?.run {
                Timber.i("Authorize Response: $this")
                Resource.success(this)
            } ?: run {
                Resource.notFound()
            }
        }.getOrElse {
            Resource.throwableError<VerifyResponse>(it)
        }
        emit(resource)
    }.catch {
        Resource.throwableError<VerifyResponse>(it)
    }.flowOn(Dispatchers.IO)

    override suspend fun haloAuthorize(
        body: HaloAuthorBody?,
        token: String?
    ) = flow {
        emit(Resource.loading<MessOauth>(null))
        val resource = kotlin.runCatching {
            Timber.i("Hahalolo Body: $body")
            messOauthRestApi.haloAuthorize(body, token)?.run {
                Timber.i("Hahalolo Authorize: $this")
                emptyOauth()
                messDao.insert(
                    MessOauth(
                        token = this.token ?: "",
                        refreshToken = this.refreshToken
                    )
                )
                val userResponse = userRestApi.user(id = null, token = this.token)?.apply {
                    Timber.i("User Response: $this")
                }
                val info = userResponse?.run {
                    OauthInfo.mapperOauthInfo(userResponse)
                }

                val oauth = MessOauth(
                    token = this.token ?: "",
                    refreshToken = this.refreshToken,
                    info = Gson().toJson(info)
                )
                Timber.i("Oauth insert DB: $oauth")
                emptyOauth()
                messDao.insert(oauth)
                Resource.success<MessOauth>(oauth)
            } ?: kotlin.run {
                Resource.notFound<MessOauth>()
            }
        }.getOrElse {
            Resource.throwableError<MessOauth>(it)
        }
        emit(resource)
    }.catch {
        Resource.throwableError<MessOauth>(it)
    }.flowOn(Dispatchers.IO)

    override suspend fun hasLoginByHahalolo(
        token: String?,
        userId: String?
    ): Flow<HasLoginHahalolo> = flow {
        messDao.messOauthFlow().collect { oauth ->
            Timber.i("Load Oauth From DB: $oauth")
            when {
                token.isNullOrEmpty() || userId.isNullOrEmpty() -> {
                    Timber.i("LoginByHahalolo No Login: Token, UserId is Null")
                    emit(HasLoginHahalolo.NoLoginByHahalolo)
                }
                oauth == null || oauth.oauthInfo?.providerId.isNullOrEmpty() -> {
                    Timber.i("LoginByHahalolo No Login: Oauth, Provider ID is Null")
                    emit(HasLoginHahalolo.NoLoginByHahalolo)
                }
                oauth.oauthInfo?.providerId == userId -> {
                    Timber.i("LoginByHahalolo Login Same Account: $userId, ${oauth.oauthInfo?.providerId}")
                    emit(HasLoginHahalolo.LoginByHahaloloSameAccount(oauth))
                }
                else -> {
                    Timber.i("LoginByHahalolo Login Not Account: $userId, ${oauth.oauthInfo?.providerId}")
                    emit(HasLoginHahalolo.LoginByHahaloloNotAccount)
                }
            }
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun hasLogin(): Flow<HasLogin> = flow {
        messDao.messOauthFlow().collect { oauth ->
            Timber.i("Load Oauth From DB: $oauth")
            if (oauth == null) {
                emit(HasLogin.NoLogin)
            } else {
                kotlin.runCatching {
                    oauth.oauthInfo?.apply {
                        when {
                            oauth.token.isNotEmpty() && idUser?.isNotEmpty() == true && isIncognito -> {
                                Timber.i("Has Login Incognito: $idUser")
                                emit(HasLogin.LoginByIncognito(oauth))
                            }
                            oauth.token.isNotEmpty() && idUser?.isNotEmpty() == true && isHahalolo -> {
                                Timber.i("Has Login Hahalolo: $idUser")
                                emit(HasLogin.LoginByHahalolo(oauth))
                            }
                            else -> {
                                Timber.i("No Login")
                                emit(HasLogin.NoLogin)
                            }
                        }
                    } ?: kotlin.run {
                        Timber.i("No Login: Oauth is Null")
                        emit(HasLogin.NoLogin)
                    }
                }.getOrElse {
                    Timber.i("No Login: $it")
                    emit(HasLogin.NoLogin)
                }
            }
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun updateOauth(oauth: MessOauth?) {
        withContext(Dispatchers.IO) {
            oauth?.apply {
                Timber.i("Update Oauth to DB: $oauth")
                messDao.update(this)
            }
        }
    }

    override suspend fun emptyOauth() {
        withContext(Dispatchers.IO) {
            Timber.i("Delete all oauth in DB")
            messDao.empty()
        }
    }

    override suspend fun oauth(): Flow<MessOauth?> = messDao.messOauthFlow()
}