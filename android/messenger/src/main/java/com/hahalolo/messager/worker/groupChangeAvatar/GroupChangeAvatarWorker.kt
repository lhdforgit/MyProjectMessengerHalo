/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.messager.worker.groupChangeAvatar

import android.content.Context
//import android.content.UriMatcher
//import android.net.Uri
import android.text.TextUtils
import androidx.work.*
//import com.hahalolo.messager.api.room.RoomRestApi
//import com.hahalolo.messager.mqtt.model.type.GroupSettingType
//import com.hahalolo.messager.utils.FileUtils
import com.hahalolo.messager.worker.Serializer
//import com.halo.common.utils.FilenameUtils
import com.halo.di.ChildWorkerFactory
//import com.halo.editor.providers.BlobProvider
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import okhttp3.MultipartBody
//import okhttp3.RequestBody.Companion.asRequestBody
//import okhttp3.RequestBody.Companion.toRequestBody
import java.util.*
import javax.inject.Inject

class GroupChangeAvatarWorker(
    val context: Context,
    workerParams: WorkerParameters,
    private var serializer: Serializer,
//    private val roomRestApi: RoomRestApi
) : Worker(context, workerParams) {

    private var userId: String? = null
    private var roomId: String? = null
    private var pathFile: String? = null

    override fun doWork(): Result {

        return try {
            if (runAttemptCount > 2) {
                Result.failure()
            }
            userId = inputData.getString(GROUP_CHANGE_AVATAR_USER_ID_PARAMS)
            roomId = inputData.getString(GROUP_CHANGE_AVATAR_GROUP_ID_PARAMS)
            pathFile = inputData.getString(GROUP_CHANGE_AVATAR_PATH_PARAMS)

            if (!TextUtils.isEmpty(userId)
                && !TextUtils.isEmpty(pathFile)
                && !TextUtils.isEmpty(roomId)
            ) {
                onUploadAvatar()
            } else Result.failure()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    private fun onUploadAvatar(): Result {

//        val bodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
//            .addFormDataPart("clientId", this.userId ?: "")
//            .addFormDataPart("type", GroupSettingType.CHANGE_AVATAR)

//        pathFile?.takeIf { it.isNotEmpty() }?.run {
//            val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
//            uriMatcher.addURI(
//                BlobProvider.AUTHORITY,
//                BlobProvider.PATH,
//                BlobProvider.MATCH
//            )
//            val uri = Uri.parse(this)
//            val match = uriMatcher.match(uri)
//            if (match == BlobProvider.MATCH) {
//                val bytes: ByteArray = BlobProvider.getInstance().getStream(context, uri).readBytes()
//                bytes.run {
//                    bodyBuilder.addFormDataPart(
//                        "avatar",
//                        UUID.randomUUID().toString()+".jpg",
//                        bytes.toRequestBody(contentType = ("image/jpeg").toMediaTypeOrNull())
//                    )
//                }
//            } else {
//                val file = FileUtils.getFileWithUri(context, uri)
//                val mineType = FilenameUtils.getMimeType(context, uri)
//                val mediaType = (mineType ?: "image/jpeg").toMediaTypeOrNull()
//
//                file?.run {
//                    bodyBuilder.addFormDataPart(
//                        "avatar",
//                        this.name,
//                        this.asRequestBody(mediaType)
//                    )
//                }
//            }
//        }


//        val body2 = bodyBuilder.build()
//        try {
//            val response = roomRestApi.updateAvatar(roomId ?: "", body2).execute()
//            response.body()?.takeIf { response.isSuccessful }?.run {
//                return Result.success()
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
        return Result.failure()
    }

    class Factory @Inject constructor(
        private val serializer: Serializer,
//        private val roomRestApi: RoomRestApi
    ) : ChildWorkerFactory {

        override fun create(appContext: Context, params: WorkerParameters): Worker {
            return GroupChangeAvatarWorker(
                appContext,
                params,
                serializer,
//                roomRestApi
            )
        }
    }

    companion object {

        val GROUP_CHANGE_AVATAR_USER_ID_PARAMS = "GROUP_CHANGE_AVATAR_USER_ID_PARAMS"
        val GROUP_CHANGE_AVATAR_GROUP_ID_PARAMS = "GROUP_CHANGE_AVATAR_GROUP_ID_PARAMS"
        val GROUP_CHANGE_AVATAR_PATH_PARAMS = "GROUP_CHANGE_AVATAR_PATH_PARAMS"

        fun uploadAvatar(
            context: Context,
            groupId: String,
            userId: String,
            pathFile: String
        ): UUID {

            val groupChangeAvatar = OneTimeWorkRequest.Builder(GroupChangeAvatarWorker::class.java)
            val data = Data.Builder()
            data.putString(GROUP_CHANGE_AVATAR_GROUP_ID_PARAMS, groupId)
            data.putString(GROUP_CHANGE_AVATAR_USER_ID_PARAMS, userId)
            data.putString(GROUP_CHANGE_AVATAR_PATH_PARAMS, pathFile)

            groupChangeAvatar.setInputData(data.build())
            val request = groupChangeAvatar.build()
            WorkManager.getInstance(context).enqueue(request)
            return request.id
        }
    }
}