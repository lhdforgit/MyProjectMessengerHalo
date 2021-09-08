/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.workmanager.user

import android.content.Context
import androidx.work.*
import com.google.gson.Gson
import com.halo.data.entities.mess.oauth.OauthInfo
import com.halo.data.repository.oauth.MessRepository
import com.halo.data.repository.user.UserRepository
import com.halo.di.ChildWorkerFactory
import com.halo.presentation.MessApplication
import com.halo.presentation.messApplication
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

/**
 * @author hahalolo
 * Create by hahalolo on 2019-07-30
 */
class UserWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val repository: UserRepository,
    private val messRepository: MessRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Cập nhật thông tin mới nhất của người dùng lưu vào app
            repository.user(token = MessApplication.instance.oauth?.token).collect {
                it.data?.apply {
                    val info = OauthInfo.mapperOauthInfo(this)
                    MessApplication.instance.oauth?.apply {
                        this.info = Gson().toJson(info)
                        messRepository.updateOauth(this)
                        messApplication.oauth = this
                    }
                }
            }
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    class Factory @Inject
    constructor(
        private val repository: UserRepository,
        private val messRepository: MessRepository
    ) : ChildWorkerFactory {
        override fun create(
            appContext: Context,
            params: WorkerParameters
        ): CoroutineWorker {
            return UserWorker(appContext, params, repository, messRepository)
        }
    }

    companion object {
        fun updateUserOauth(context: Context) {
            val workBuilder = OneTimeWorkRequest.Builder(UserWorker::class.java)
            val data = Data.Builder()
            workBuilder.setInputData(data.build())
            val oneTimeWorkRequest = workBuilder.build()
            WorkManager.getInstance(context).enqueue(oneTimeWorkRequest)
        }
    }
}