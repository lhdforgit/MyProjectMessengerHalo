/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.bubble.conversation.view.react_detail

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hahalolo.messager.bubble.conversation.view.react_detail.adapter.ReactionDetailAdapter
import com.hahalolo.messager.bubble.conversation.view.react_detail.listener.ChatReactionDetailListener
import com.hahalolo.messager.bubble.conversation.view.react_detail.listener.ChatReactionNavigationListener
import com.hahalolo.messager.bubble.conversation.view.react_detail.listener.Constant
import com.hahalolo.messager.bubble.getScreenSize
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatReactionDetailBottomSheetBinding
import com.halo.data.entities.reaction.Reactions
import com.halo.data.room.entity.MemberEntity
import com.halo.widget.HaloLinearLayoutManager

class ChatReactionDetailBottomSheet : BottomSheetDialogFragment() {

    private var binding: ChatReactionDetailBottomSheetBinding?= null

    private var listReaction = mutableListOf<Pair<String,MutableList<MemberEntity>>>()
    private var listener: ChatReactionDetailListener? = null

    private var adapter: ReactionDetailAdapter? = null

    fun newInstance(): ChatReactionDetailBottomSheet? {
        return ChatReactionDetailBottomSheet()
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
            R.layout.chat_reaction_detail_bottom_sheet,
            null,
            false
        )
        try {
            var ratio = 0.5
            val height = getScreenSize().heightPixels * ratio
            val layoutParams = binding?.contentLayout?.layoutParams
            layoutParams?.height = height.toInt()
        } catch (e: Exception) {

        }
        binding?.root?.let {
            dialog.setContentView(it)
        }
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

    fun setReaction(listReaction:  MutableList<Pair<String, MutableList<MemberEntity>>>) {
        this.listReaction = listReaction
        requestLayout()
    }

    fun setListener(listener: ChatReactionDetailListener?) {
        this.listener = listener
    }

    private fun requestLayout() {
        binding?.initRec()
        binding?.initNav()
    }

    private fun ChatReactionDetailBottomSheetBinding.initNav() {
        reactNav.setListReaction(listReaction)
        reactNav.setLifecycleObserver(this@ChatReactionDetailBottomSheet)
        reactNav.setListener(object :
            ChatReactionNavigationListener {
            override fun onReactionTypeClick(type: String) {
                listReaction.forEach {
                    if (TextUtils.equals(type,it.first)){
                        it.second.let {newList ->
                            adapter?.updateData(newList, type)
                        }
                    }
                }
            }
        })
    }

    private fun ChatReactionDetailBottomSheetBinding.initRec() {
        reactRec.layoutManager = HaloLinearLayoutManager(context)
        reactRec.setHasFixedSize(false)
        adapter =
            ReactionDetailAdapter(
                listReaction.firstOrNull()?.second,
                Glide.with(this@ChatReactionDetailBottomSheet)
            )
        reactRec.adapter = adapter
        adapter?.setListener(listener)
        adapter?.notifyDataSetChanged()
    }

    private fun bindAction(dialog: Dialog) {
        binding?.closeBt?.setOnClickListener {
            onCancel(dialog)
            dialog.dismiss()
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        listener?.onDismissListener()
    }
}