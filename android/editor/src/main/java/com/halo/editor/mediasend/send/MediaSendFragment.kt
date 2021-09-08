/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.editor.mediasend.send

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener
import com.halo.common.utils.KeyboardUtils
import com.halo.editor.R
import com.halo.editor.databinding.MediasendFragmentBinding
import com.halo.editor.mediasend.Media
import com.halo.editor.mediasend.MediaSendViewModel
import com.halo.editor.util.Util
import dagger.android.support.DaggerFragment
import javax.inject.Inject

/**
 * Allows the user to edit and caption a set of media items before choosing to send them.
 */
class MediaSendFragment
@Inject constructor() : DaggerFragment() {

    private var fragmentPagerAdapter: MediaSendFragmentPagerAdapter? = null
    private var viewModel: MediaSendViewModel? = null

    var binding: MediasendFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.mediasend_fragment,
            container,
            false
        )
        return binding?.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        initViewModel()
        fragmentPagerAdapter = MediaSendFragmentPagerAdapter(childFragmentManager)
        binding?.mediasendPager?.adapter = fragmentPagerAdapter
        val pageChangeListener = FragmentPageChangeListener()
        binding?.mediasendPager?.addOnPageChangeListener(pageChangeListener)
        binding?.mediasendPager?.post {
            pageChangeListener.onPageSelected(
                binding?.mediasendPager?.currentItem ?: 0
            )
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel?.let {
            fragmentPagerAdapter?.restoreState(it.drawState)
            it.onImageEditorStarted()
        }
    }

    override fun onResume() {
        super.onResume()
        KeyboardUtils.hideSoftInput(context as Activity)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            viewModel?.onImageEditorStarted()
        } else {
            fragmentPagerAdapter?.notifyHidden()
        }
    }

    override fun onPause() {
        super.onPause()
        fragmentPagerAdapter?.notifyHidden()
    }

    override fun onStop() {
        super.onStop()
        fragmentPagerAdapter?.let {
            it.saveAllState()
            viewModel?.saveDrawState(it.getSavedState())
        }
    }

    fun onTouchEventsNeeded(needed: Boolean) {
        binding?.mediasendPager?.isEnabled = !needed
    }

    val allMedia: List<Media>
        get() = fragmentPagerAdapter?.allMedia ?: emptyList()

    val savedState: Map<Uri?, Any>
        get() = fragmentPagerAdapter?.getSavedState() ?: HashMap()

    val currentImagePosition: Int
        get() = binding?.mediasendPager?.currentItem ?: 0

    @SuppressLint("FragmentLiveDataObserve")
    private fun initViewModel() {
        viewModel = ViewModelProvider(requireActivity()).get(MediaSendViewModel::class.java)
        viewModel?.selectedMedia?.observe(
            this,
            Observer { media: List<Media>? ->
                if (Util.isEmpty(media)) {
                    return@Observer
                }
                fragmentPagerAdapter?.setMedia(media ?: emptyList())
            }
        )
        viewModel?.position?.observe(this, Observer { position: Int? ->
            if (position == null || position < 0) return@Observer
            binding?.mediasendPager?.setCurrentItem(position, true)
            val playbackControls = fragmentPagerAdapter?.getPlaybackControls(position)
            if (playbackControls != null) {
                val params = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                playbackControls.layoutParams = params
                binding?.mediasendPlaybackControlsContainer?.removeAllViews()
                binding?.mediasendPlaybackControlsContainer?.addView(playbackControls)
            } else {
                binding?.mediasendPlaybackControlsContainer?.removeAllViews()
            }
        })
    }

    private inner class FragmentPageChangeListener : SimpleOnPageChangeListener() {
        override fun onPageSelected(position: Int) {
            viewModel?.onPageChanged(position)
            fragmentPagerAdapter?.notifyPageChanged(position)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): MediaSendFragment {
            return MediaSendFragment()
        }
    }
}