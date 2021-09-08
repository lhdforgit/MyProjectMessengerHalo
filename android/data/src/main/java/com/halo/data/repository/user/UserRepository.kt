package com.halo.data.repository.user

import android.app.DownloadManager
import com.halo.data.common.resource.Resource
import com.halo.data.entities.user.User
import kotlinx.coroutines.flow.Flow

/**
 * Create by ndn
 * Create on 6/8/21
 * com.halo.data.repository.user
 */
interface UserRepository {
    suspend fun user(
        id: String? = null,
        token: String? = null
    ): Flow<Resource<User>>

    suspend fun searchUser(
        token: String,
        keyword: String,
        limit : String = "20"
    ): Flow<Resource<MutableList<User>>>
}