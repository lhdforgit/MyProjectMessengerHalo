/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.api.contact

import androidx.lifecycle.LiveData

import com.halo.data.api.utils.ApiResponse
import com.halo.data.entities.contact.*
import com.halo.data.entities.mongo.tet.GifCard
import com.halo.data.entities.response.ResponsePaging
import okhttp3.MultipartBody
import retrofit2.http.*

/**
 * @author ngannd
 * Create by ngannd on 24/05/2019
 */
interface ContactService {
    @GET("/v2/contacts?")
    suspend fun contacts(
        @Query("limit") limit: Int,
        @Query("page") page: Int,
        @Query("status") status: String
    ): ResponsePaging<Contact>?

    @GET("/v2/contacts?")
    suspend fun contactsV2(
        @Query("limit") limit: Int,
        @Query("since") since: String?,
        @Query("status") status: String
    ): ResponsePaging<Contact>?

    @GET("/v2/contacts/{contacts}")
    suspend fun contactsDetail(
        @Path("contacts") contactId: String
    ): Contact?

    @POST("/v2/contacts/refresh")
    suspend fun contactsRefresh(): ContactRefreshResponse?

    @GET("/v2/users/me")
    suspend fun profileOwner(): Contact?

    @POST("/v2/contacts/{contactId}/update")
    suspend fun updateName(
        @Path("contactId") contactId: String?,
        @Body body: ContactBody?
    ): Contact?

    @POST("/v2/contacts/{contactId}/alias")
    suspend fun updateAlias(
        @Path("contactId") contactId: String?,
        @Body body: ContactAliasBody?
    ): Contact?
}
