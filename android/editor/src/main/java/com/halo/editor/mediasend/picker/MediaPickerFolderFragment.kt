/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.editor.mediasend.picker

import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.halo.editor.R
import com.halo.editor.databinding.MediapickerFolderFragmentBinding
import com.halo.editor.mediasend.MediaFolder
import com.halo.editor.mediasend.MediaSendViewModel
import dagger.android.support.DaggerFragment
import javax.inject.Inject

/**
 * Allows the user to select a media folder to explore.
 */
class MediaPickerFolderFragment
@Inject constructor() :
    DaggerFragment(),
    MediaPickerFolderAdapter.EventListener {

    lateinit var viewModel: MediaSendViewModel
    private var controller: Controller? = null
    private var layoutManager: GridLayoutManager? = null

    var binding: MediapickerFolderFragmentBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = ViewModelProvider(requireActivity()).get(MediaSendViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        check(activity is Controller) { "Parent activity must implement controller class." }
        controller = activity as Controller?
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.mediapicker_folder_fragment,
            container, false
        )
        return binding?.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = MediaPickerFolderAdapter(Glide.with(this), this)
        layoutManager = GridLayoutManager(requireContext(), 2)
        onScreenWidthChanged(screenWidth)
        binding?.mediapickerFolderList?.layoutManager = layoutManager
        binding?.mediapickerFolderList?.adapter = adapter
        viewModel.getFolders(requireContext()).observe(
            viewLifecycleOwner, Observer { folders: List<MediaFolder> ->
                adapter.setFolders(
                    folders
                )
            }
        )
        initToolbar(binding?.mediapickerToolbar)
    }

    override fun onResume() {
        super.onResume()
        viewModel.onFolderPickerStarted()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        requireActivity().menuInflater.inflate(R.menu.mediapicker_default, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.mediapicker_menu_camera) {
            controller?.onCameraSelected()
            return true
        }
        return false
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        onScreenWidthChanged(screenWidth)
    }

    private fun initToolbar(toolbar: Toolbar?) {
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar
            ?.setDisplayHomeAsUpEnabled(true)
        toolbar?.setNavigationOnClickListener { requireActivity().onBackPressed() }
    }

    private fun onScreenWidthChanged(newWidth: Int) {
        layoutManager?.spanCount =
            newWidth / resources.getDimensionPixelSize(R.dimen.media_picker_folder_width)
    }

    private val screenWidth: Int
        get() {
            val size = Point()
            requireActivity().windowManager.defaultDisplay.getSize(size)
            return size.x
        }

    override fun onFolderClicked(mediaFolder: MediaFolder) {
        controller?.onFolderSelected(mediaFolder)
    }

    interface Controller {
        fun onFolderSelected(folder: MediaFolder)
        fun onCameraSelected()
    }

    companion object {
        @JvmStatic
        fun newInstance(): MediaPickerFolderFragment {
            return MediaPickerFolderFragment()
        }
    }
}