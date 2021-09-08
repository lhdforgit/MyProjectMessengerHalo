package com.hahalolo.messager.presentation.base

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.ViewDataBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hahalolo.messenger.R
import com.halo.widget.bottomdrawer.BottomDrawer
import com.halo.widget.bottomdrawer.BottomDrawerDialog
import dagger.android.support.DaggerAppCompatDialogFragment

abstract class MessengerBottomSheetDagger<VBD : ViewDataBinding> :
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
        kotlin.runCatching {
            dialog.setOnShowListener {
                val bottomSheetDialog = it as BottomSheetDialog
                val parentLayout =
                    bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                parentLayout?.let { it ->
                    val behaviour = BottomSheetBehavior.from(it)
                    setupFullHeight(it)
                    val displayMetrics: DisplayMetrics = Resources.getSystem().displayMetrics
                    val height = (displayMetrics.heightPixels * 0.85).toInt()
                    behaviour.peekHeight = height
                    behaviour.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        }
        return dialog
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
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