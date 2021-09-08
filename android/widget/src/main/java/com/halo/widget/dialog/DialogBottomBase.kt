package com.halo.widget.dialog


import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.*
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.halo.common.utils.SizeUtils
import com.halo.widget.R
import kotlinx.android.synthetic.main.dialog_bottom_base.view.*


abstract class DialogBottomBase(var cnt: Context? = null) : BottomSheetDialogFragment() {
    interface CustomDialog {
        fun setTitleDialog(title: String)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, com.halo.themes.R.style.BottomSheetDialogThemeBase)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog?.setOnShowListener {
            Handler().post {
                (dialog as? BottomSheetDialog)?.apply {
                    val bottomSheetDialog =
                        this.findViewById<View>(R.id.design_bottom_sheet) as? FrameLayout
                    bottomSheetDialog?.apply {
                        BottomSheetBehavior.from(this).state = BottomSheetBehavior.STATE_EXPANDED
                    }
                }
            }
        }
        val view = View.inflate(context, R.layout.dialog_bottom_base, null)
        val contentView = initializeCustomView(inflater, container, savedInstanceState)

        contentView?.let { _contentView ->
            view?.content_layout?.removeAllViews()
            view?.content_layout?.addView(_contentView)
        }

        val dialog = object : CustomDialog {
            override fun setTitleDialog(title: String) {
                if (!TextUtils.isEmpty(title)) {
                    view?.title_tv?.text = title
                    view?.title_tv?.visibility = View.VISIBLE
                }
            }
        }
        setUpDialog(dialog)
        return view
    }

    abstract fun setUpDialog(dialog: CustomDialog)
    abstract fun initializeCustomView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?

    fun show() {
        (cnt as? FragmentActivity)?.apply {
            show(supportFragmentManager, "show_dialog")
        }
    }

    //Show toast above content view ...
    fun showToast(content: String) {
        val toast = Toast.makeText(
            context,
            content,
            Toast.LENGTH_SHORT
        )
        toast.view?.apply {
            background = context?.let {
                ContextCompat.getDrawable(it, R.drawable.bg_toast_filter)
            }
            findViewById<TextView>(android.R.id.message)?.apply {
                setTextColor(ContextCompat.getColor(context, R.color.app_background))
                textSize = 15f
            }
        }
        toast.setGravity(
            Gravity.BOTTOM or Gravity.CENTER,
            0,
            ((view?.height ?: 0) + SizeUtils.dp2px(15f))
        )
        toast.view
        toast.show()
    }
}
