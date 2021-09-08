/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.presentation.group.gallery.doc

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.map
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.google.gson.Gson
import com.hahalolo.messager.presentation.base.AbsMessFragment
import com.hahalolo.messager.presentation.download.ChatDownloadFileService
import com.hahalolo.messager.presentation.group.gallery.ChatRoomGalleryAct
import com.hahalolo.messager.presentation.group.gallery.adapter.ChatRoomGalleryDiffCallback
import com.hahalolo.messager.presentation.group.gallery.adapter.MessengerManagerFileAdapter
import com.hahalolo.messager.presentation.group.gallery.adapter.MessengerManagerFileModel
import com.hahalolo.messager.presentation.adapter.paging.MessengerPreloadHolder
import com.hahalolo.messager.presentation.group.gallery.AttachmentListener
import com.hahalolo.messager.worker.download.DownloadMessageMediaWorker
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatRoomGalleryDocFrBinding
import com.halo.common.utils.HaloFileUtils
import com.halo.common.utils.RecyclerViewUtils
import com.halo.data.room.table.AttachmentTable
import com.halo.widget.HaloLinearLayoutManager
import com.halo.widget.recyclerview.RecyclerViewPreloaderFlexbox
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class ChatRoomGalleryDocFr @Inject
constructor() : AbsMessFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private var binding: ChatRoomGalleryDocFrBinding? = null
    private var viewModel: ChatRoomGalleryDocViewModel? = null

    private val requestManager by lazy { Glide.with(this) }

    private var adapter: MessengerManagerFileAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.chat_room_gallery_doc_fr,
            container,
            false
        )
        return binding?.root
    }

    override fun initializeViewModel() {
        viewModel =
            ViewModelProvider(viewModelStore, factory).get(ChatRoomGalleryDocViewModel::class.java)
        viewModel?.channelId?.value = arguments?.getString(ChatRoomGalleryAct.ROOM_ID)
    }

    override fun initializeLayout() {
        initRec()
        initObserver()
    }

    private fun showPopupMenu(view: View, mediaModel: MessengerManagerFileModel?) {
        context?.apply {
            val wrapper: Context = ContextThemeWrapper(this, R.style.PopupMenu)
            mediaModel?.run {
                val popup = PopupMenu(wrapper, view)
                //Inflating the Popup using xml file
                popup.menuInflater.inflate(R.menu.chat_message_gallery_menu, popup.menu)
                val menu = popup.menu
                val copyBt = menu.findItem(R.id.copy_bt)
                val viewBt = menu.findItem(R.id.view_bt)
                val downloadBt = menu.findItem(R.id.download_bt)
                val deleteBt = menu.findItem(R.id.remove_bt)

                copyBt.isVisible = false
                viewBt.isVisible = false
                deleteBt.isVisible = false
                downloadBt.isVisible = true

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.download_bt -> {
                            onDownloadMediaPer(mediaModel)
                        }
                        R.id.remove_bt -> {

                        }
                    }
                    true
                }
                popup.show() //showing popup menu
            }
        }
    }

    private val attachmentListener = object : AttachmentListener {

        override fun onClickMenu(view: View, mediaModel: MessengerManagerFileModel?) {
            showPopupMenu(view, mediaModel)
        }
    }


    private fun initRec() {
        binding?.apply {
            val preloadSizeProvider = ViewPreloadSizeProvider<MessengerManagerFileModel>()
            documentRec.layoutManager = HaloLinearLayoutManager(requireContext())
            RecyclerViewUtils.optimization(documentRec, activity)
            adapter = MessengerManagerFileAdapter(
                diffCallback = ChatRoomGalleryDiffCallback(),
                preloadSizeProvider = preloadSizeProvider,
                requestManager = requestManager,
                listener = attachmentListener
            )
            adapter?.let {
                val preloader = RecyclerViewPreloaderFlexbox(
                    requestManager,
                    it,
                    preloadSizeProvider,
                    5
                )
                documentRec.addOnScrollListener(preloader)
            }
            documentRec.setRecyclerListener { viewHolder ->
                kotlin.runCatching {
                    if (viewHolder is MessengerPreloadHolder) {
                        (viewHolder as MessengerPreloadHolder).invalidateLayout(requestManager)
                    }
                }
            }
            documentRec.adapter = adapter
        }
    }

    private fun initObserver() {
        lifecycleScope.launch {
            viewModel?.docAttachment?.collect {
                it.map { MessengerManagerFileModel.convertToMediaModel(it) }.let { paging ->
                    adapter?.submitData(paging)
                }
            }
        }
    }

    private fun onDownloadMediaPer(mediaModel: MessengerManagerFileModel) {
        activity?.run {
            HaloFileUtils.externalPermision(this@run, object : HaloFileUtils.PerListener {
                override fun onGranted() {

//                    val intent = Intent(requireContext(), ChatDownloadFileService::class.java)
//                    intent.putExtra(
//                        ChatDownloadFileService.ATTACHMENT_DOWNLOAD,
//                        serialize(attachment, AttachmentTable::class.java)
//                    )
//                    startService(intent)
                }

                override fun onDeny() {
                }
            })
        }
    }

    private fun serialize(`object`: Any?, clazz: Class<*>): String {
        return if (`object` == null) {
            ""
        } else Gson().toJson(`object`, clazz)
    }

    private fun initHandleDownloadMedia(workerId: UUID) {
        context?.run {
            WorkManager.getInstance(this)
                .getWorkInfoByIdLiveData(workerId)
                .observe(this@ChatRoomGalleryDocFr, androidx.lifecycle.Observer { workInfo ->
                    try {
                        when (workInfo?.state) {
                            WorkInfo.State.FAILED -> {
                                //todo failed
                            }
                            WorkInfo.State.SUCCEEDED -> {
                                val path =
                                    workInfo.outputData.getString(DownloadMessageMediaWorker.OUTPUT_DOWNLOAD_PATH)
                                path?.takeIf { it.isNotEmpty() }?.run {
                                    Toast.makeText(context, this, Toast.LENGTH_SHORT).show()
                                }
                            }
                            WorkInfo.State.RUNNING -> {
                                val progress = workInfo.outputData.getInt(
                                    DownloadMessageMediaWorker.OUTPUT_DOWNLOAD_PROGRESS,
                                    0
                                )
                                //todo progress download
                            }
                            else -> {
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                })
        }
    }
}