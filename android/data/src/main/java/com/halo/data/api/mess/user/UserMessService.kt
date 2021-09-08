/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.api.mess.user

import com.halo.data.common.resource.Resource
import com.halo.data.entities.response.ResponsePaging
import com.halo.data.entities.user.User
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * @author ndn
 * Created by ndn
 * Created on 5/15/18.
 */
interface UserMessService {

    @GET("v2/users/{userId}")
    suspend fun user(
        @Path("userId") id: String?
    ): User?

    @GET("v2/users/me")
    suspend fun user(): User?

    @GET("v2/users/search")
    suspend fun searchUser(
        @Query("keyword") keyword: String,
        @Query("limit") limit : String
    ) : ResponsePaging<User>?
}