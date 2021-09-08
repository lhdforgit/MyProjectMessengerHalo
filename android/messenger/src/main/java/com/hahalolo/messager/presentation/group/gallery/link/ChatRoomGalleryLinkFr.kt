/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.presentation.group.gallery.link

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.map
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.hahalolo.messager.bubble.conversation.detail.gallery.adapter.ChatAttachmentAdapter
import com.hahalolo.messager.bubble.conversation.detail.gallery.adapter.ChatGalleryDiffCallback
import com.hahalolo.messager.bubble.conversation.detail.gallery.adapter.ChatGalleryListener
import com.hahalolo.messager.bubble.conversation.detail.gallery.adapter.PreloadHolder
import com.hahalolo.messager.presentation.adapter.paging.MessengerPreloadHolder
import com.hahalolo.messager.presentation.base.AbsMessFragment
import com.hahalolo.messager.presentation.group.gallery.AttachmentListener
import com.hahalolo.messager.presentation.group.gallery.ChatRoomGalleryAct
import com.hahalolo.messager.presentation.group.gallery.adapter.ChatRoomGalleryDiffCallback
import com.hahalolo.messager.presentation.group.gallery.adapter.MessengerManagerFileAdapter
import com.hahalolo.messager.presentation.group.gallery.adapter.MessengerManagerFileModel
import com.halo.data.room.table.AttachmentTable
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatRoomGalleryLinkFrBinding
import com.halo.common.utils.ClipbroadUtils
import com.halo.common.utils.RecyclerViewUtils
import com.halo.widget.HaloLinearLayoutManager
import com.halo.widget.customtab.CustomTab
import com.halo.widget.recyclerview.RecyclerViewPreloaderFlexbox
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatRoomGalleryLinkFr @Inject
constructor() : AbsMessFragment() {
    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private var binding: ChatRoomGalleryLinkFrBinding? = null
    private var viewModel: ChatRoomGalleryLinkViewModel? = null

    private val requestManager by lazy { Glide.with(this) }


    private var adapter: MessengerManagerFileAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.chat_room_gallery_link_fr,
            container,
            false
        )
        return binding?.root
    }

    override fun initializeViewModel() {
        viewModel =
            ViewModelProvider(viewModelStore, factory).get(ChatRoomGalleryLinkViewModel::class.java)
        viewModel?.channelId?.value = arguments?.getString(ChatRoomGalleryAct.ROOM_ID)
    }

    override fun initializeLayout() {
        initRec()
        initObserver()
    }

    private fun showPopupMenu(view: View, attachmentTable: MessengerManagerFileModel?) {
        context?.apply {
            val wrapper: Context = ContextThemeWrapper(this, R.style.PopupMenu)
            attachmentTable?.run {
                val popup = PopupMenu(wrapper, view)
                //Inflating the Popup using xml file
                popup.menuInflater.inflate(R.menu.chat_message_gallery_menu, popup.menu)
                val menu = popup.menu
                val copyBt = menu.findItem(R.id.copy_bt)
                val viewBt = menu.findItem(R.id.view_bt)
                val downloadBt = menu.findItem(R.id.download_bt)
                val deleteBt = menu.findItem(R.id.remove_bt)

                copyBt.isVisible = true
                viewBt.isVisible = true
                deleteBt.isVisible = false
                downloadBt.isVisible = false

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.view_bt -> {
                            context?.run {
                                CustomTab.openUrl(this, url)
                            }
                        }
                        R.id.copy_bt -> {
                            context?.run {
                                url?.takeIf { it.isNotEmpty() }?.let { path ->
                                    ClipbroadUtils.copyText(this, path)
                                } ?: kotlin.run {
                                    errorNetwork()
                                }
                            }
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

        override fun onClickAttachment(position: Int, mediaModel: MessengerManagerFileModel?) {
            mediaModel?.run {
                context?.run {
                    CustomTab.openUrl(this, url)
                }
            }
        }

        override fun onClickMenu(view: View, mediaModel: MessengerManagerFileModel?) {
            showPopupMenu(view, mediaModel)
        }
    }


    private fun initRec() {
        binding?.apply {
            val preloadSizeProvider = ViewPreloadSizeProvider<MessengerManagerFileModel>()
            linkRec.layoutManager = HaloLinearLayoutManager(requireContext())
            RecyclerViewUtils.optimization(linkRec, activity)
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
                linkRec.addOnScrollListener(preloader)
            }
            linkRec.setRecyclerListener { viewHolder ->
                kotlin.runCatching {
                    if (viewHolder is MessengerPreloadHolder) {
                        (viewHolder as MessengerPreloadHolder).invalidateLayout(requestManager)
                    }
                }
            }
            linkRec.adapter = adapter
        }
    }

    private fun initObserver() {
        lifecycleScope.launch {
            viewModel?.linkAttachment?.collect {
                it.map { MessengerManagerFileModel.convertToMediaModel(it) }.let { paging ->
                    adapter?.submitData(paging)
                }
            }
        }
    }


    private fun checkEmpty(empty: Boolean) {
        binding?.layoutEmpty?.visibility = if (empty) View.VISIBLE else View.GONE
    }
}