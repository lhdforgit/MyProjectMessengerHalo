/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.editor.mediasend.send

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.halo.editor.R

class MediaSendGifFragment : Fragment(),
    MediaSendPageFragment {

    override var uri: Uri? = null

    override var playbackControls: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.mediasend_image_fragment, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        uri = arguments?.getParcelable(KEY_URI)
        Glide.with(this).load(uri).into((view as ImageView))
    }

    override fun saveState(): Any? {
        return null
    }

    override fun restoreState(state: Any?) {}

    override fun notifyHidden() {}

    companion object {
        private const val KEY_URI = "uri"
        @JvmStatic
        fun newInstance(uri: Uri): MediaSendGifFragment {
            val args = Bundle()
            args.putParcelable(KEY_URI, uri)
            val fragment = MediaSendGifFragment()
            fragment.arguments = args
            fragment.uri = uri
            return fragment
        }
    }
}