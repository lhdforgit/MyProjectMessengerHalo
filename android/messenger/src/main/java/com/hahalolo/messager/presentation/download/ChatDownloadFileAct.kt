package com.hahalolo.messager.presentation.download

import android.content.Context
import android.content.Intent
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.hahalolo.messager.presentation.base.AbsMessBackActivity
import com.halo.data.room.table.AttachmentTable
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatDownloadFileActBinding
import com.halo.common.utils.HaloFileUtils
import java.util.*

class ChatDownloadFileAct : AbsMessBackActivity() {

    lateinit var binding: ChatDownloadFileActBinding

    override fun initActionBar() {

    }

    override fun initializeBindingViewModel() {
        binding = DataBindingUtil.setContentView(
            this@ChatDownloadFileAct,
            R.layout.chat_download_file_act
        )
    }

    override fun initializeLayout() {
        intent?.getStringExtra(ATTACHMENT_DOWNLOAD)?.let { attachment ->
            initDownloadMediaPermission(attachment)
        }
    }

    override fun isFullScreen(): Boolean {
        return false
    }

    private fun initDownloadMediaPermission(attachment: String) {
        HaloFileUtils.externalPermision(
            this@ChatDownloadFileAct,
            object : HaloFileUtils.PerListener {
                override fun onGranted() {
                    val intent = Intent(this@ChatDownloadFileAct, ChatDownloadFileService::class.java)
                    intent.putExtra(ChatDownloadFileService.ATTACHMENT_DOWNLOAD, attachment)
                    startService(intent)
                    finish()
                }

                override fun onDeny() {
                }
            })
    }

    companion object {
        const val ATTACHMENT_DOWNLOAD = "ChatDownloadFileAct_AttachmentTable"
        fun startDownload(context: Context, attachment: AttachmentTable): Intent {
            val intent = Intent(context, ChatDownloadFileAct::class.java)
            intent.putExtra(ATTACHMENT_DOWNLOAD, serialize(attachment, AttachmentTable::class.java))
            return intent
        }

        private fun serialize(`object`: Any?, clazz: Class<*>): String {
            return if (`object` == null) {
                ""
            } else Gson().toJson(`object`, clazz)
        }
    }
}