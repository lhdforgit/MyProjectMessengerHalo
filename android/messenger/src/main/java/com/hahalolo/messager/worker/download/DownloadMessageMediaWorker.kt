/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.messager.worker.download

import android.content.Context
import android.text.TextUtils
import android.util.Log
import androidx.work.*
import com.google.gson.Gson
//import com.hahalolo.messager.mqtt.model.type.AttachmentType
//import com.hahalolo.messager.mqtt.model.type.MessageStatus
//import com.hahalolo.messager.respository.chat.ChatRepository
//import com.hahalolo.messager.respository.message.ChatMessageRepository
//import com.hahalolo.messager.respository.update.ChatDatabaseRepository
import com.halo.data.room.table.AttachmentTable
import com.hahalolo.messager.worker.Serializer
import com.halo.common.utils.HaloFileUtils
import com.halo.di.ChildWorkerFactory
import java.io.DataInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import javax.inject.Inject

class DownloadMessageMediaWorker(
    val context: Context,
    workerParams: WorkerParameters,
    private var serializer: Serializer,
//    private var chatDatabaseRepository: ChatDatabaseRepository,
//    private var chatMessageRepository: ChatMessageRepository,
//    private var chatRepository: ChatRepository
) : Worker(context, workerParams) {

    private var attachment: AttachmentTable? = null

    override fun doWork(): Result {
        return try {
            if (runAttemptCount > 1) {
                resultFailure()
            }
            attachment = serializer.deserialize(
                inputData.getString(ATTACHMENT_TABLE_PARAMS),
                AttachmentTable::class.java
            )
            if (attachment == null) {
                resultFailure()
            } else onDownloadMedia()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            resultFailure()
        }
    }

    private fun onDownloadMedia(): Result {
        attachment?.run {
//            chatDatabaseRepository.onUpdateMessageStatus(this.msgId, MessageStatus.DOWNLOADING)
//            when (this.type) {
//                AttachmentType.DOCUMENT,
//                AttachmentType.MEDIA -> {
//                    return onDownloadUrl(this.fileUrl ?: "", this.fileName ?: "")
//                }
//                else -> {
//                    chatDatabaseRepository.onUpdateMessageStatus(this.msgId, MessageStatus.SENDED)
//                }
//            }
        }
        return resultFailure()
    }

    private fun onDownloadUrl(url: String, fileName: String): Result {
        val localFile = onDownloadMedia(url, fileName)
        localFile?.takeIf { it.isNotEmpty() }?.run {
//            if (TextUtils.equals(attachment?.type, AttachmentType.MEDIA)){
//                HaloFileUtils.addImageToGallery(HaloFileUtils.getPathLocalMedia(this), context)
//            }
            return resultSuscess(this, fileName)
        }
        return resultFailure()
    }

    private fun onDownloadMedia(linkUrl: String, fileName: String): String? {
        try {
            val u = URL(linkUrl)
            val inputStream = u.openStream()
            val dataInputStream = DataInputStream(inputStream)

            val buffer = ByteArray(1024)
            var length = 0
            val folder = HaloFileUtils.createFolderDownload()
            val localFile = File(folder.path + File.separator + fileName)

            try {
                folder.mkdirs()
            } catch (e: Exception) {
            }

            var progress = 0
            val fos = FileOutputStream(localFile)
            length = dataInputStream.read(buffer)
            while (length > 0) {
                progress += length
                progressDownload(progress)
                fos.write(buffer, 0, length)
                length = dataInputStream.read(buffer)
            }
            fos.close()
            inputStream.close()
            dataInputStream.close()
            return localFile.path
        } catch (mue: MalformedURLException) {
            Log.e("SYNC getUpdate", "malformed url error", mue)
        } catch (ioe: IOException) {
            Log.e("SYNC getUpdate", "io error", ioe)
        } catch (se: SecurityException) {
            Log.e("SYNC getUpdate", "security error", se)
        }

        return null
    }

    private fun progressDownload(progress: Int) {
        attachment?.fileSize?.takeIf { it > 0 }?.run {

            val builder = Data.Builder()
            builder.putInt(OUTPUT_DOWNLOAD_PROGRESS, (progress * 100 / this))
            setProgressAsync(builder.build())
        }
    }


//    private fun onDownloadFile(message: ChatMessageModel?) {
//        if (message != null) {
//            val contentFileEntity = message.chatFileModel
//            contentFileEntity?.run {
//                val download = HaloFileUtils.Download.create(
//                    this@ChatMessageAct,
//                    message.id,
//                    this.name,
//                    this.size,
//                    this.path
//                )
//                val key = download.key
//
//                val task = DownloadTask.Builder(
//                    download.pathDownload,
//                    HaloFileUtils.createFolderDownload()
//                )
//                    .setFilename(download.name)
//                    // the minimal interval millisecond for callback progress
//                    .setMinIntervalMillisCallbackProcess(30)
//                    // do re-download even if the task has already been completed in the past.
//                    .setPassIfAlreadyCompleted(false)
//                    .build()
//                task.tag = download
//                viewModel?.putCacheDownload(key, task)
//                viewModel?.putCacheMsgDownload(key, message)
//                task.enqueue(object : CustomDownloadListener {
//                    override fun taskStart(task: DownloadTask) {
//                        val ob = task.tag
//                        if (ob is HaloFileUtils.Download) {
//
//
//                            viewModel?.loadCacheMsgDownload(ob.key.toString())
//                                ?.progressListener?.onStart()
//                        }
//                    }
//
//                    override fun fetchProgress(
//                        task: DownloadTask,
//                        blockIndex: Int,
//                        increaseBytes: Long
//                    ) {
//                        val ob = task.tag
//                        if (ob is HaloFileUtils.Download) {
//                            val downloadKey = ob.key ?: ""
//                            viewModel?.setProgress(
//                                downloadKey, (viewModel?.getProgress(downloadKey)
//                                    ?: 0
//                                    + increaseBytes) as Int
//                            )
//
//                            viewModel?.loadCacheMsgDownload(downloadKey)
//                                ?.progressListener
//                                ?.loadPregress(
//                                    (viewModel?.getProgress(downloadKey)
//                                        ?: 0 * 100 / ob.size) as Int
//                                )
//                        }
//                    }
//
//                    override fun taskEnd(
//                        task: DownloadTask,
//                        cause: EndCause,
//                        realCause: Exception?
//                    ) {
//                        val ob = task.tag
//                        if (ob is HaloFileUtils.Download) {
//                            if (realCause == null) {
//
//                                viewModel?.loadCacheMsgDownload(ob.key)?.run {
//
//                                    val fileEntity = this.chatFileModel
//                                    if (fileEntity?.name != null) {
//                                        Toast.makeText(
//                                            this@ChatMessageAct,
//                                            getString(
//                                                R.string.chat_message_save_to,
//                                                ob.localPath
//                                            ),
//                                            Toast.LENGTH_SHORT
//                                        ).show()
//                                        HaloFileUtils.addFileToGallery(
//                                            this@ChatMessageAct,
//                                            ob
//                                        )
//                                    }
//                                    this.progressListener?.onComplete()
//                                }
//                            } else {
//                                Toast.makeText(
//                                    this@ChatMessageAct,
//                                    getString(R.string.chat_message_download_failed),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                viewModel?.loadCacheMsgDownload(key)?.progressListener?.onError()
//                            }
//                            viewModel?.removeProgress(key)
//                            viewModel?.removeTaskDownload(key)
//                            viewModel?.removeCacheMsgDownload(key)
//                        }
//                    }
//                })
//            }
//        }
//    }

    private fun resultSuscess(pathFile: String, fileName: String): Result {
//        attachment?.msgId?.takeIf { it.isNotEmpty() }?.run {
//            chatDatabaseRepository.onUpdateMessageStatus(this, MessageStatus.SENDED)
//        }
        val builder = Data.Builder()
        builder.putString(OUTPUT_DOWNLOAD_PATH, pathFile)
        builder.putString(OUTPUT_FILE_NAME, fileName)
        return Result.success(builder.build())
    }

    private fun resultFailure(): Result {
//        attachment?.msgId?.takeIf { it.isNotEmpty() }?.run {
//            chatDatabaseRepository.onUpdateMessageStatus(this, MessageStatus.SENDED)
//        }
        return Result.failure()
    }

    class Factory @Inject constructor(
        private val serializer: Serializer,
//        private val chatDatabaseRepository: ChatDatabaseRepository,
//        private val chatMessageRepository: ChatMessageRepository,
//        private val chatRepository: ChatRepository
    ) : ChildWorkerFactory {

        override fun create(appContext: Context, params: WorkerParameters): Worker {
            return DownloadMessageMediaWorker(
                appContext,
                params,
                serializer,
//                chatDatabaseRepository,
//                chatMessageRepository,
//                chatRepository
            )
        }
    }

    companion object {

        val ATTACHMENT_TABLE_PARAMS = "DownloadMessageMediaWorker-Message-Socket-Param"
        const val OUTPUT_DOWNLOAD_PATH = "DownloadMessageMediaWorker-DOWNLOAD_PATH"
        const val OUTPUT_FILE_NAME = "DownloadMessageMediaWorker-OUTPUT_FILE_NAME"
        const val OUTPUT_DOWNLOAD_PROGRESS = "DownloadMessageMediaWorker-OUTPUT_DOWNLOAD_PROGRESS"

        fun downloadMedia(context: Context, attachment: AttachmentTable): UUID {
            val sendMessageWokerRequest =
                OneTimeWorkRequest.Builder(DownloadMessageMediaWorker::class.java)
            val data = Data.Builder()
            data.putString(
                ATTACHMENT_TABLE_PARAMS,
                serialize(attachment, AttachmentTable::class.java)
            )
            sendMessageWokerRequest.setInputData(data.build())
            val oneTimeWorkRequest = sendMessageWokerRequest.build()
            WorkManager.getInstance(context).enqueue(oneTimeWorkRequest)
            return oneTimeWorkRequest.id
        }

        fun downloadMedia(context: Context, attachment: String): UUID {
            val sendMessageWokerRequest =
                OneTimeWorkRequest.Builder(DownloadMessageMediaWorker::class.java)
            val data = Data.Builder()
            data.putString(
                ATTACHMENT_TABLE_PARAMS,
                attachment
            )
            sendMessageWokerRequest.setInputData(data.build())
            val oneTimeWorkRequest = sendMessageWokerRequest.build()
            WorkManager.getInstance(context).enqueue(oneTimeWorkRequest)
            return oneTimeWorkRequest.id
        }

        private fun serialize(`object`: Any?, clazz: Class<*>): String {
            return if (`object` == null) {
                ""
            } else Gson().toJson(`object`, clazz)
        }
    }
}