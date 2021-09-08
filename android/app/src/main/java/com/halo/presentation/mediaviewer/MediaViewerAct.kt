/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.presentation.mediaviewer

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.webkit.URLUtil
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.RequestManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hahalolo.player.core.MemoryMode
import com.hahalolo.player.core.Scope
import com.hahalolo.player.exoplayer.HaloPlayer
import com.halo.R
import com.halo.common.permission.RxPermissions
import com.halo.common.utils.*
import com.halo.common.utils.ktx.notNull
import com.halo.common.utils.ktx.viewModels
import com.halo.data.entities.media.MediaEntity
import com.halo.databinding.MediaViewerActBinding
import com.halo.okdownload.DownloadTask
import com.halo.presentation.base.AbsBackActivity
import com.halo.presentation.mediaviewer.view.HaloGifView
import com.halo.presentation.mediaviewer.view.HaloZoomDrawerView
import com.halo.presentation.mediaviewer.view.VideoView
import com.halo.presentation.search.general.SearchAct
import com.halo.presentation.utils.glide.GlideApp
import com.halo.presentation.utils.upload.DownloadMediaUtils
import com.halo.widget.slidr.Slidr
import com.halo.widget.slidr.model.SlidrConfig
import com.halo.widget.slidr.model.SlidrInterface
import com.halo.widget.slidr.model.SlidrListener
import com.halo.widget.slidr.model.SlidrPosition
import io.reactivex.disposables.Disposable
import java.util.*
import javax.inject.Inject

/**
 * @author ndn
 * Created by ndn
 * Created on 8/14/18
 */
class MediaViewerAct : AbsBackActivity(), SlidrListener {

    private var adapter: MediaViewerAdapter? = null
    private var pageChangeListener: ViewPager.OnPageChangeListener? = null

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val viewModel: MediaViewerViewModel by viewModels { factory }
    private var binding: MediaViewerActBinding by notNull()
    private var menuDialog: BottomSheetDialog? = null

    private var listenerInterface: SlidrInterface? = null

    private var requestManager: RequestManager? = null

    private var stateSlide = -1

    private val player: HaloPlayer by lazy { HaloPlayer[this] }

    override fun isFullScreen(): Boolean {
        return true
    }

    override fun initializeBindingViewModel() {
        binding = DataBindingUtil.setContentView(this, R.layout.media_viewer_act)
        initSlidr()
    }

    override fun initializeLayout() {
        val manager = player
            .register(this, MemoryMode.BALANCED)
            .addBucket(binding.pager)
        viewModel.timelineVolume.observe(this, {
            manager.applyVolumeInfo(it, binding.pager, Scope.BUCKET)
        })

        initIntent()
        requestManager = GlideApp.with(this)
        bindAction()
        initPager()
    }

    private fun initIntent() {
        try {
            if (intent != null) {

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val listenerPager = object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {

        }

        override fun onPageSelected(position: Int) {
            // Reset view when selected others image
            updateView(null)
            viewModel.updatePosition(position)
        }

        override fun onPageScrollStateChanged(state: Int) {
            when (state) {
                ViewPager.SCROLL_STATE_DRAGGING -> listenerInterface?.lock()
                ViewPager.SCROLL_STATE_IDLE -> listenerInterface?.unlock()
                ViewPager.SCROLL_STATE_SETTLING -> {
                }
            }
        }
    }

    private fun initPager() {

        adapter = MediaViewerAdapter(
            this@MediaViewerAct,
            null,
            null,
            null,
            requestManager,
            true,
            player
        )

        binding.pager.visibility = VISIBLE
        binding.pager.adapter = adapter

        adapter?.setListener { show, showComment ->
            if (show) {
                showMenuOption()
            } else {
                hideMenuOption()
            }
        }

        viewModel.listDataSet.observe(this, {
            adapter?.setDataSet(it)
            it.takeIf { it.isNullOrEmpty() }?.run {
                finish()
            } ?: kotlin.run {
                viewModel.position.value?.run {
                    binding.pager.currentItem = this
                }
                setPageChangeListener(listenerPager)
            }
        })
    }

    protected fun initSlidr() {
        // Get the status bar colors to interpolate between
        val primary = resources.getColor(R.color.primary_dark)
        val secondary = resources.getColor(R.color.primary)

        // Build the slidr config
        val mConfig = SlidrConfig.Builder()
            .primaryColor(primary)
            .secondaryColor(secondary)
            .position(SlidrPosition.VERTICAL)
            .velocityThreshold(2400f)
            .listener(this)
            .touchSize(SizeUtils.dp2px(32f).toFloat())
            .build()

        // Attach the Slidr Mechanism to this activity
        listenerInterface = Slidr.attach(this, mConfig)
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun bindAction() {

        binding.pager.setOnClickListener { v ->
            showMenuOption()
        }

        adapter?.setListener { show, showComment ->
            showMenuOption()
        }

        binding.menuBt.setOnClickListener { initDialogMenu().show() }

        binding.searchBt.setOnClickListener { startActivity(SearchAct.getIntent(this@MediaViewerAct)) }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_VIEW_LIKE_DETAIL || requestCode == REQUEST_COMMENT) {
            viewModel.position.value?.run {
                viewModel.updatePosition(this)
            }
        }
    }

    private fun showMenuOption() {
        val path = viewModel.getCurrentMediaEntity()?.path?:""
        val isUrlPath  = !URLUtil.isNetworkUrl(path)
        binding.menuBt.visibility = if (isUrlPath) VISIBLE else GONE
        binding.toolbar.visibility = VISIBLE
    }

    private fun hideMenuOption() {
        binding.toolbar.visibility = GONE
    }

    private fun initDialogMenu(): BottomSheetDialog {
        if (menuDialog == null) {
            menuDialog = BottomSheetDialog(
                this@MediaViewerAct,
                R.style.Theme_MaterialComponents_BottomSheetDialog
            )
            val bottomSheetView =
                layoutInflater.inflate(R.layout.bottom_sheet_menu_media_viewer, null)
            val saveBt = bottomSheetView.findViewById<View>(R.id.save_media_bt)
            val coppyPath = bottomSheetView.findViewById<View>(R.id.coppy_media_url)
            bottomSheetView.findViewById<View>(R.id.close_iv).setOnClickListener { v ->
                menuDialog?.dismiss()
            }
            saveBt.setOnClickListener { v ->
                val rxPermissions = RxPermissions(this@MediaViewerAct)
                rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(object : io.reactivex.Observer<Boolean> {
                        override fun onNext(aBoolean: Boolean) {
                            if (aBoolean) {
                                val data = viewModel.getCurrentMediaEntity()
                                if (data != null) {
                                    onDownloadMedia(data)
                                } else {
                                    Toast.makeText(
                                        this@MediaViewerAct,
                                        R.string.media_viewer_media_error,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    this@MediaViewerAct,
                                    R.string.editor_permission_media_request_denied,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                        override fun onSubscribe(d: Disposable) {
                            // Do nothing
                        }

                        override fun onError(e: Throwable) {
                            // Do nothing
                        }

                        override fun onComplete() {
                            // Do nothing
                        }
                    })
                menuDialog?.dismiss()
            }

            coppyPath.setOnClickListener { view ->
                val data = viewModel.getCurrentMediaEntity()
                if (data != null) {
                    ClipbroadUtils.copyText(this@MediaViewerAct, data.getPath())
                    menuDialog?.dismiss()
                    return@setOnClickListener
                }
                Toast.makeText(
                    this@MediaViewerAct,
                    getString(R.string.media_viewer_coppy_failed),
                    Toast.LENGTH_SHORT
                ).show()
                menuDialog?.dismiss()
            }
            menuDialog?.setContentView(bottomSheetView)
        }
        return menuDialog!!
    }

    private fun onDownloadMedia(mediaEntity: MediaEntity) {
        DownloadMediaUtils.downloadUrl(object : DownloadMediaUtils.DownloadUrlListener {
            override fun getPath(): String {
                return mediaEntity.getPath()
            }

            override fun getSize(): Long {
                var size: Long = 0
                try {
                    size = java.lang.Long.parseLong(mediaEntity.size ?: "")
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                return size
            }

            override fun getName(): String {
                return FilenameUtils.getName(mediaEntity.getPath())
            }

            override fun onStart(task: DownloadTask) {}

            override fun onProgress(task: DownloadTask, progress: Long) {}

            override fun onError(realCause: Exception) {

            }

            override fun onComplete(task: DownloadTask, localMedia: String) {
                Toast.makeText(
                    this@MediaViewerAct,
                    getString(R.string.media_viewer_save_to) + localMedia,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    override fun initActionBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    fun setPageChangeListener(pageChangeListener: ViewPager.OnPageChangeListener) {
        this.pageChangeListener?.run {
            binding.pager.removeOnPageChangeListener(this)
        }
        this.pageChangeListener = pageChangeListener
        binding.pager.addOnPageChangeListener(pageChangeListener)
        pageChangeListener.onPageSelected(binding.pager.currentItem)
    }

    /**
     * UpdateChat all view in social view when data change
     */
    fun updateView(entity: MediaEntity?) {
        entity?.run {
            viewModel.updateMediaEntity(this)
        }
    }

    override fun finish() {
        setResult()
        super.finish()
    }

    override fun invalidateLayoutOnDestroy() {
        clearCache()
        super.invalidateLayoutOnDestroy()
    }

    private fun clearCache() {
        val child = binding.pager.childCount
        for (i in 0 until child) {
            val view = binding.pager.getChildAt(i)
            if (view != null) {
                if (view is HaloZoomDrawerView) {
                    view.invalidateView()
                } else if (view is HaloGifView) {
                    view.invalidateView()
                } else if (view is VideoView) {
                    view.invalidateView()
                }
            }
        }
        if (adapter != null) {
            /*adapter?.onDestroy()*/
            adapter = null
        }
        if (menuDialog != null) menuDialog = null
        if (pageChangeListener != null) pageChangeListener = null
    }

    private fun setResult() {
        setResult(RESULT_CANCELED)
    }

    override fun onSlideStateChanged(state: Int) {
        stateSlide = state
        if (adapter != null && state == 1) {
            /*val viewCurrent = adapter?.getHaloPlayViewPosition(binding.pager.currentItem)
            viewCurrent?.hideController()*/
        }
    }

    override fun onSlideChange(percent: Float) {
        binding.toolbar.alpha = percent.toInt().toFloat()
        binding.container.setBackgroundColor(
            Color.argb(
                if (percent == 1f) 255 else (percent * 205).toInt(),
                0,
                0,
                0
            )
        )
    }

    override fun onSlideOpened() {

    }

    override fun onSlideClosed(): Boolean {
        return false
    }

    companion object {

        private val DATA_SET_ARGS = "ImageViewerAct-Data-Set"
        private val POSITION_ARGS = "ImageViewerAct-position"
        private val REQUEST_COMMENT = 111
        private val REQUEST_VIEW_LIKE_DETAIL = 112

        fun getIntent(
            context: Context,
            dataSet: ArrayList<MediaEntity>,
            startPosition: Int): Intent {
            val intent = Intent(context, MediaViewerAct::class.java)

            return intent
        }
    }
}
