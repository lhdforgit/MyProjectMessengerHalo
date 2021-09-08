/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.editor.mediasend.send

import android.annotation.SuppressLint
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.halo.editor.mediasend.Media
import com.halo.editor.scribbles.ImageEditorFragment
import com.halo.editor.util.MediaUtil
import java.util.*

internal class MediaSendFragmentPagerAdapter
@SuppressLint("WrongConstant")
constructor(fm: FragmentManager) :
    FragmentStatePagerAdapter(
        fm,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {

    private val media: MutableList<Media> = ArrayList()
    private val fragments: MutableMap<Int, MediaSendPageFragment> = HashMap()
    private val savedState: MutableMap<Uri?, Any> = HashMap()

    init {
        media
        fragments
        savedState
    }

    override fun getItem(i: Int): Fragment {
        return media.takeIf { it.isNotEmpty() }?.let {
            val mediaItem = media[i]
            return mediaItem.uri?.run {
                return when {
                    MediaUtil.isGif(mediaItem.mimeType) -> {
                        MediaSendGifFragment.newInstance(
                            this
                        )
                    }
                    MediaUtil.isImageType(mediaItem.mimeType) -> {
                        ImageEditorFragment.newInstance(this)
                    }
                    MediaUtil.isVideoType(mediaItem.mimeType) -> {
                        MediaSendVideoFragment.newInstance(
                            this
                        )
                    }
                    else -> {
                        throw UnsupportedOperationException("Can only render images and videos. Found mimetype: '" + mediaItem.mimeType + "'")
                    }
                }
            } ?: throw NullPointerException("Uri can not be null")
        } ?: throw NullPointerException("Media can not be empty")
    }

    override fun getItemPosition(obj: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment =
            super.instantiateItem(container, position) as MediaSendPageFragment
        fragments[position] = fragment
        val state = savedState[fragment.uri]
        state?.apply {
            fragment.restoreState(state)
        }
        return fragment
    }

    override fun destroyItem(
        container: ViewGroup,
        position: Int,
        obj: Any
    ) {
        val fragment = obj as MediaSendPageFragment
        val state = fragment.saveState()
        if (state != null) {
            savedState[fragment.uri] = state
        }
        super.destroyItem(container, position, obj)
        fragments.remove(position)
    }

    override fun getCount(): Int {
        return media.size
    }

    val allMedia: List<Media>
        get() = media

    fun setMedia(media: List<Media>) {
        this.media.clear()
        this.media.addAll(media)
        notifyDataSetChanged()
    }

    fun getSavedState(): Map<Uri?, Any> {
        for (fragment in fragments.values) {
            val state = fragment.saveState()
            if (state != null) {
                savedState[fragment.uri] = state
            }
        }
        return HashMap(savedState)
    }

    fun saveAllState() {
        for (fragment in fragments.values) {
            val state = fragment.saveState()
            if (state != null) {
                savedState[fragment.uri] = state
            }
        }
    }

    fun restoreState(state: Map<Uri?, Any>) {
        savedState.clear()
        savedState.putAll(state)
    }

    fun getPlaybackControls(position: Int): View? {
        return if (fragments.containsKey(position)) fragments[position]?.playbackControls else null
    }

    fun notifyHidden() {
        for (f in fragments.values) {
            f.notifyHidden()
        }
    }

    fun notifyPageChanged(currentPage: Int) {
        notifyHiddenIfExists(currentPage - 1)
        notifyHiddenIfExists(currentPage + 1)
    }

    private fun notifyHiddenIfExists(position: Int) {
        val fragment = fragments[position]
        fragment?.notifyHidden()
    }
}