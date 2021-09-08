package com.hahalolo.messager.bubble.conversation.view.reader_detail

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hahalolo.messager.bubble.conversation.view.reader_detail.adapter.ReaderDetailAdapter
import com.hahalolo.messager.bubble.getScreenSize
import com.halo.data.room.entity.MemberEntity
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatReaderDetailBottomSheetBinding
import com.halo.widget.HaloLinearLayoutManager

class ChatReaderDetailBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: ChatReaderDetailBottomSheetBinding
    private var listReader: MutableList<MemberEntity>? = null
    private var adapter: ReaderDetailAdapter? = null
    private var listener: ChatReaderDetailListener? = null

    fun newInstance(): ChatReaderDetailBottomSheet? {
        return ChatReaderDetailBottomSheet()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetStyle)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreate(savedInstanceState)
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        val inflater = LayoutInflater.from(context)
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.chat_reader_detail_bottom_sheet,
            null,
            false
        )
        try {
            var ratio = 0.5
            listReader?.takeIf { it.isNotEmpty() }?.run {
                if (size >= 5) ratio = 0.7
            }
            val height = getScreenSize().heightPixels * ratio
            val layoutParams = binding.contentLayout.layoutParams
            layoutParams.height = height.toInt()
        } catch (e: Exception) {
        }
        dialog.setContentView(binding.root)
        return dialog
    }

    override fun setupDialog(dialog: Dialog, style: Int) {
        val bottomSheetDialog = dialog as BottomSheetDialog
        try {
            dialog.setOnShowListener {
                bottomSheetDialog.run {
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                    behavior.addBottomSheetCallback(object :
                        BottomSheetBehavior.BottomSheetCallback() {
                        override fun onSlide(bottomSheet: View, slideOffset: Float) {

                        }

                        override fun onStateChanged(bottomSheet: View, newState: Int) {
                            if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                            }
                        }
                    })
                }
            }

        } catch (e: Exception) {

        }
        bindAction(dialog)
        requestLayout()
    }

    private fun requestLayout() {
        binding.apply {
            readerRec.layoutManager =
                HaloLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            readerRec.setHasFixedSize(false)
            adapter = ReaderDetailAdapter(listener, Glide.with(this@ChatReaderDetailBottomSheet))
            readerRec.adapter = adapter
            adapter?.updateData(listReader)
        }
    }

    fun setListReader(listReader: MutableList<MemberEntity>?) {
        this.listReader = listReader
    }

    fun setListener(listener: ChatReaderDetailListener?) {
        this.listener = listener
    }

    private fun bindAction(dialog: Dialog) {
        binding.closeBt.setOnClickListener {
            onCancel(dialog)
            dialog.dismiss()
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        listener?.onDismissListener()
    }
}