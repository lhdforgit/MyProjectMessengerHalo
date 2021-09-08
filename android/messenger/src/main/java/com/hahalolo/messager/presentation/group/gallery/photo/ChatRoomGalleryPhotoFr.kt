/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.presentation.group.gallery.photo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.map
import com.bumptech.glide.Glide
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.hahalolo.messager.presentation.base.AbsMessFragment
import com.hahalolo.messager.presentation.group.gallery.ChatRoomGalleryAct
import com.hahalolo.messager.presentation.group.gallery.adapter.ChatRoomGalleryDiffCallback
import com.hahalolo.messager.presentation.group.gallery.adapter.MessengerManagerFileAdapter
import com.hahalolo.messager.presentation.group.gallery.adapter.MessengerManagerFileModel
import com.hahalolo.messager.presentation.adapter.paging.MessengerPreloadHolder
import com.hahalolo.messager.presentation.group.gallery.AttachmentListener
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatRoomGalleryPhotoFrBinding
import com.halo.common.utils.RecyclerViewUtils
import com.halo.widget.HaloGridLayoutManager
import com.halo.widget.recyclerview.RecyclerViewPreloaderFlexbox
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatRoomGalleryPhotoFr @Inject
constructor() : AbsMessFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private var binding: ChatRoomGalleryPhotoFrBinding? = null
    private var viewModel: ChatRoomGalleryPhotoViewModel? = null
    private val requestManager by lazy { Glide.with(this) }
    private var adapter: MessengerManagerFileAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.chat_room_gallery_photo_fr,
            container,
            false
        )
        return binding?.root
    }

    override fun initializeViewModel() {
        viewModel = ViewModelProvider(
            viewModelStore,
            factory
        ).get(ChatRoomGalleryPhotoViewModel::class.java)
        viewModel?.channelId?.value = arguments?.getString(ChatRoomGalleryAct.ROOM_ID)
    }

    override fun initializeLayout() {
        initRec()
        initObserver()
    }

    private fun initObserver() {
        lifecycleScope.launch {
            viewModel?.imageAttachment?.collect {
                it.map { MessengerManagerFileModel.convertToMediaModel(it) }.let { paging ->
                    adapter?.submitData(paging)
                }
            }
        }
    }

    private fun checkEmpty(empty: Boolean) {
        binding?.layoutEmpty?.visibility = if (empty) View.VISIBLE else View.GONE
    }

//    private fun openMedia(attachmentTable: AttachmentTable?) {
//        adapter?.currentList()?.let { listAttachment ->
//            val index = Iterators.indexOf(listAttachment.iterator()) { input ->
//                TextUtils.equals(
//                    input?.id,
//                    attachmentTable?.id
//                )
//            }
//            val listData = convertListMediaEntity(listAttachment.toMutableList())
//            context?.run {
//                appController.navigateOpenMediaEntity(listData as ArrayList<MediaEntity>,  if (index < listData.size && index > 0) index else 0)
//            }
//        }
//    }
//
//    private fun convertListMediaEntity(listImage: MutableList<AttachmentTable>?): List<MediaEntity> {
//        val listData = ArrayList<MediaEntity>()
//        listImage?.takeIf { !it.isNullOrEmpty() }?.forEach {
//            val mediaEntity = MediaEntity()
//            mediaEntity.id = UUID.randomUUID().toString()
//            mediaEntity.thumb = it.thumbnail
//            mediaEntity.path = it.getAttachmentUrl()
//            mediaEntity.type = if (it.isVideo())
//                TypeMedia.VID
//            else
//                TypeMedia.IMG
//            listData.add(mediaEntity)
//        }
//        return listData
//    }

    private val attachmentListener = object : AttachmentListener {

        override fun onClickAttachment(position: Int, mediaModel: MessengerManagerFileModel?) {

        }
    }

    private fun initRec() {
        binding?.apply {
            val preloadSizeProvider = ViewPreloadSizeProvider<MessengerManagerFileModel>()
            galleryRec.layoutManager = HaloGridLayoutManager(requireContext(), 3)
            RecyclerViewUtils.optimization(galleryRec, activity)
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
                galleryRec.addOnScrollListener(preloader)
            }
            galleryRec.setRecyclerListener { viewHolder ->
                kotlin.runCatching {
                    if (viewHolder is MessengerPreloadHolder) {
                        (viewHolder as MessengerPreloadHolder).invalidateLayout(requestManager)
                    }
                }
            }
            galleryRec.adapter = adapter
        }
    }
}