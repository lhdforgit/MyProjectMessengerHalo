/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.editor.mediasend.picker

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.halo.editor.R
import com.halo.editor.databinding.MediapickerItemFragmentBinding
import com.halo.editor.mediasend.Media
import com.halo.editor.mediasend.MediaSendViewModel
import com.halo.editor.util.Util
import dagger.android.support.DaggerFragment
import java.util.*
import javax.inject.Inject

/**
 * Allows the user to select a set of media items from a specified folder.
 */
class MediaPickerItemFragment
@Inject constructor() : DaggerFragment(),
    MediaPickerItemAdapter.EventListener {

    private var bucketId: String? = null
    private var folderTitle: String? = null
    private var maxSelection = 0
    private var viewModel: MediaSendViewModel? = null
    private var adapter: MediaPickerItemAdapter? = null
    private var controller: Controller? = null
    private var layoutManager: GridLayoutManager? = null

    var binding: MediapickerItemFragmentBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        bucketId = requireArguments().getString(KEY_BUCKET_ID)
        folderTitle = requireArguments().getString(KEY_FOLDER_TITLE)
        maxSelection = requireArguments().getInt(KEY_MAX_SELECTION)
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
            R.layout.mediapicker_item_fragment,
            container, false
        )
        return binding?.root
    }

    @SuppressLint("FragmentLiveDataObserve")
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        adapter = MediaPickerItemAdapter(Glide.with(this), this, maxSelection)
        layoutManager = GridLayoutManager(requireContext(), 4)
        binding?.mediapickerItemList?.layoutManager = layoutManager
        binding?.mediapickerItemList?.adapter = adapter
        initToolbar(binding?.mediapickerToolbar)
        onScreenWidthChanged(screenWidth)

        viewModel = ViewModelProvider(requireActivity()).get(MediaSendViewModel::class.java)

        if (!Util.isEmpty(viewModel?.selectedMedia?.value)) {
            viewModel?.selectedMedia?.value?.let {
                adapter?.setSelected(it)
            }
            viewModel?.selectedMedia?.value?.let {
                onMediaSelectionChanged(ArrayList(it))
            }
        }
        viewModel?.getMediaInBucket(requireContext(), bucketId ?: "")
            ?.observe(this, Observer { media: List<Media>? ->
                media?.apply {
                    adapter?.setMedia(this)
                }
            })
    }

    override fun onResume() {
        super.onResume()
        viewModel?.onItemPickerStarted()
        if (requireArguments().getBoolean(KEY_FORCE_MULTI_SELECT)) {
            adapter?.setForcedMultiSelect(true)
            viewModel?.onMultiSelectStarted()
        }
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

    override fun onMediaChosen(media: Media) {
        controller?.onMediaSelected(media)
    }

    override fun onMediaSelectionStarted() {
        viewModel?.onMultiSelectStarted()
    }

    override fun onMediaSelectionChanged(media: ArrayList<Media>) {
        adapter?.notifyDataSetChanged()
        viewModel?.onSelectedMediaChanged(requireContext(), media)
    }

    override fun onMediaSelectionOverflow(maxSelection: Int) {
        Toast.makeText(
            requireContext(),
            resources.getQuantityString(
                R.plurals.MediaSendActivity_cant_share_more_than_n_items,
                maxSelection,
                maxSelection
            ),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun initToolbar(toolbar: Toolbar?) {
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = folderTitle
        toolbar?.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun onScreenWidthChanged(newWidth: Int) {
        layoutManager?.spanCount =
            newWidth / resources.getDimensionPixelSize(R.dimen.media_picker_item_width)
    }

    private val screenWidth: Int
        get() {
            val size = Point()
            requireActivity().windowManager.defaultDisplay.getSize(size)
            return size.x
        }

    interface Controller {
        fun onMediaSelected(media: Media)
        fun onCameraSelected()
    }

    companion object {
        private const val KEY_BUCKET_ID = "bucket_id"
        private const val KEY_FOLDER_TITLE = "folder_title"
        private const val KEY_MAX_SELECTION = "max_selection"
        private const val KEY_FORCE_MULTI_SELECT = "force_multi_select"

        @JvmStatic
        fun newInstance(
            bucketId: String?,
            folderTitle: String?,
            maxSelection: Int,
            forceMultiSelect: Boolean = true
        ): MediaPickerItemFragment {
            val args = Bundle()
            args.putString(KEY_BUCKET_ID, bucketId)
            args.putString(KEY_FOLDER_TITLE, folderTitle)
            args.putInt(KEY_MAX_SELECTION, maxSelection)
            args.putBoolean(KEY_FORCE_MULTI_SELECT, forceMultiSelect)
            val fragment = MediaPickerItemFragment()
            fragment.arguments = args
            return fragment
        }
    }
}