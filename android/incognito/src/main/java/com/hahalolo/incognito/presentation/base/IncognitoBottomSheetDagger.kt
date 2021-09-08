package com.hahalolo.incognito.presentation.base

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hahalolo.incognito.R
import dagger.android.support.DaggerAppCompatDialogFragment

abstract class IncognitoBottomSheetDagger<VBD : ViewDataBinding> :
    DaggerAppCompatDialogFragment() {
    var binding: VBD? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = initializeCustomView(inflater, container, savedInstanceState)
        initAddFragment()
        return binding?.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext())
        binding?.apply {
            dialog.setContentView(root)
        }
        dialog.apply {
            val displayMetrics: DisplayMetrics = Resources.getSystem().displayMetrics
            val height = (displayMetrics.heightPixels * 0.85).toInt()
            behavior.peekHeight = height
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        return dialog
    }

    override fun setupDialog(dialog: Dialog, style: Int) {

    }

    abstract fun initAddFragment()

    abstract fun initializeCustomView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): VBD?
}