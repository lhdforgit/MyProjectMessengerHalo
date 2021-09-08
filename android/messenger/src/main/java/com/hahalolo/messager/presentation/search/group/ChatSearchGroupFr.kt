/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.presentation.search.group

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestListener
import com.hahalolo.messager.chatkit.commons.ImageLoader
import com.hahalolo.messager.presentation.adapter.preload.PreloadHolder
import com.hahalolo.messager.presentation.base.AbsMessFragment
import com.hahalolo.messager.presentation.message.ChatMessageAct
import com.hahalolo.messager.presentation.search.group.adapter.ChatSearchGroupAdapter
import com.hahalolo.messager.presentation.search.group.adapter.ChatSearchGroupListener
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatSearchGroupFrBinding
import com.halo.common.utils.KeyboardUtils
import com.halo.common.utils.ThumbImageUtils
import com.halo.widget.HaloLinearLayoutManager
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ChatSearchGroupFr @Inject
internal constructor() : AbsMessFragment() {
    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private var binding: ChatSearchGroupFrBinding? = null
    private var viewModel: ChatSearchGroupViewModel? = null

    private var requestManager: RequestManager? = null

    private var groupAdapter: ChatSearchGroupAdapter? = null

    private fun getRequestManager(): RequestManager {
        if (requestManager == null) requestManager = Glide.with(this)
        return requestManager!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.chat_search_group_fr, container, false)
        return binding?.root
    }

    override fun initializeViewModel() {
        viewModel = ViewModelProvider(this, factory).get(ChatSearchGroupViewModel::class.java)
    }

    override fun initializeLayout() {
        initAdapter()
        initHandleRoom()
    }

    private fun initHandleRoom() {
//        viewModel?.roomSocketList?.observe(this, Observer { roomSocketList ->
//            binding?.empty?.visibility = if (roomSocketList.isNullOrEmpty()) View.VISIBLE else View.GONE
//            roomSocketList?.run {
//                groupAdapter?.submitList(this)
//                onDelayScroll()
//            }
//        })
    }

    private fun onDelayScroll() {
        observeDelay(100, object : DisposableObserver<Long>() {
            override fun onNext(t: Long) {

            }

            override fun onError(e: Throwable) {

            }

            override fun onComplete() {
                binding?.resultGroupRec?.scrollToPosition(0)
            }
        })
    }

    private fun initAdapter() {

        groupAdapter =
            ChatSearchGroupAdapter(
                object : ImageLoader {
                    override fun loadImage(
                        imageView: ImageView?,
                        url: String?,
                        payload: Any?,
                        requestListener: RequestListener<Drawable>?
                    ) {
                        imageView?.run {
                            this@ChatSearchGroupFr.getRequestManager()
                                .load(
                                    ThumbImageUtils.thumb(
                                        ThumbImageUtils.Size.AVATAR_NORMAL,
                                        url,
                                        ThumbImageUtils.TypeSize._1_1
                                    )
                                )
                                .placeholder(R.color.img_holder)
                                .error(R.color.img_holder)
                                .listener(requestListener)
                                .into(this)
                        }
                    }

                    override fun getRequestManager(): RequestManager {
                        return this@ChatSearchGroupFr.getRequestManager()
                    }

                },
                object : ChatSearchGroupListener {
                    override fun detailFriend(id: String) {

                    }

                    override fun messageFriend(id: String) {

                    }

                    override fun messageRoomChat(roomId: String) {
                        navigateRoomChat(roomId)
                    }
                })

        binding?.resultGroupRec?.layoutManager = HaloLinearLayoutManager(context,
                LinearLayoutManager.VERTICAL,
                false)
        binding?.resultGroupRec?.adapter = groupAdapter

        binding?.resultGroupRec?.setRecyclerListener { viewHolder ->
            if (viewHolder is PreloadHolder) {
                (viewHolder as PreloadHolder).invalidateLayout(requestManager)
            }
        }

        binding?.resultGroupRec?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (recyclerView.isInTouchMode && newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    activity?.takeIf { KeyboardUtils.isSoftInputVisible(it) }
                            ?.run {
                                KeyboardUtils.hideSoftInput(this)
                            }
                }
            }
        })
    }

    fun updateOnQuery(s: String) {
        binding?.searching = s.isNotEmpty()
        viewModel?.updateQueryUser(s)
    }

    private fun navigateRoomChat(id: String) {
        if (!TextUtils.isEmpty(id) && !TextUtils.equals(userIdToken, id)) {
            context?.run {
                startActivity(ChatMessageAct.getIntentChannel(this, id))
            }
            activity?.finish()
        }
    }

    override fun onDestroy() {
        clearCache()
        super.onDestroy()
    }

    private fun clearCache() {
        disposables?.clear()

        if (binding?.resultGroupRec != null) {
            val child1 = binding?.resultGroupRec?.childCount ?: 0
            for (i in 0 until child1) {
                val view = binding?.resultGroupRec?.getChildAt(i)
                if (view != null) {
                    val viewHolder = binding?.resultGroupRec?.getChildViewHolder(view)
                    if (viewHolder != null && viewHolder is PreloadHolder) {
                        (viewHolder as PreloadHolder).invalidateLayout(requestManager)
                    }
                }
            }
            binding?.resultGroupRec?.adapter = null
        }
        if (groupAdapter != null) groupAdapter = null
        if (requestManager != null) requestManager = null
    }

    private var disposables: CompositeDisposable? = null

    private fun observeDelay(time: Long, longDisposableObserver: DisposableObserver<Long>) {
        if (disposables == null) disposables = CompositeDisposable()
        disposables?.add(Observable.timer(time, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(longDisposableObserver))
    }

}
