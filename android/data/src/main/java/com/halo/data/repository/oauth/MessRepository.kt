/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.repository.oauth

import com.halo.data.common.resource.Resource
import com.halo.data.entities.mess.StatusResponse
import com.halo.data.entities.mess.VerifyBody
import com.halo.data.entities.mess.VerifyResponse
import com.halo.data.entities.mess.halo.HaloAuthorBody
import com.halo.data.entities.mess.halo.HaloAuthorResponse
import com.halo.data.entities.mess.oauth.MessOauth
import kotlinx.coroutines.flow.Flow

/**
 * @author ngannd
 * Create by ngannd on 24/05/2019
 */
interface MessRepository {

    /**
     * Xác thực tài khoản ẩn danh bằng mã OTP
     */
    suspend fun verify(
        body: VerifyBody
    ): Flow<Resource<MessOauth>>

    /**
     * Đăng nhập bằng tài khoản ẩn danh messenger
     */
    suspend fun authorize(
        body: VerifyBody
    ): Flow<Resource<VerifyResponse>>

    /**
     * Đăng nhập bằng tài khoản Hahalolo
     */
    suspend fun haloAuthorize(
        body: HaloAuthorBody?,
        token: String?
    ): Flow<Resource<MessOauth>>

    /**
     * Trường hợp người dùng mở app từ Hahalolo
     */
    suspend fun hasLoginByHahalolo(
        token: String?,
        userId: String?
    ): Flow<HasLoginHahalolo>

    /**
     * Trường hợp người dùng mở app, kiểm tra xem người dùng đã từng đăng nhập chưa
     */
    suspend fun hasLogin(): Flow<HasLogin>

    /**
     * Câp nhật thông tin người dùng vào cache
     */
    suspend fun updateOauth(oauth: MessOauth?)

    suspend fun emptyOauth()

    suspend fun oauth(): Flow<MessOauth?>
}

/**
 * Người dùng mở App từ Hahalolo
 */
sealed class HasLoginHahalolo {
    /** Người dùng chưa bao giờ đăng nhập bằng Hahalolo */
    object NoLoginByHahalolo : HasLoginHahalolo()
    /** Người dùng đã đăng nhập bằng tài khoản giống tài khoản từ Hahalolo mở app */
    data class LoginByHahaloloSameAccount(var oauth: MessOauth) : HasLoginHahalolo()
    /** Người dùng đã đăng nhập bằng tài khoản không giống tài khoản từ Hahalolo mở app */
    object LoginByHahaloloNotAccount : HasLoginHahalolo()
}

/**
 * Người dùng mở App bình thường
 */
sealed class HasLogin {
    /** Trường hợp người dùng chưa bao giờ đăng nhập */
    object NoLogin : HasLogin()
    /** Người dùng đã đăng nhập bằng Hahalolo */
    data class LoginByHahalolo(var oauth: MessOauth) : HasLogin()
    /** Người dùng đã đăng nhập bằng tài khoản ẩn danh messenger */
    data class LoginByIncognito(var oauth: MessOauth) : HasLogin()
}