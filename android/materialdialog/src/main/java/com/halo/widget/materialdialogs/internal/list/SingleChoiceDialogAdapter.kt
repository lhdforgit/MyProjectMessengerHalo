/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.widget.materialdialogs.internal.list

import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.halo.widget.materialdialogs.MaterialDialog
import com.halo.widget.materialdialogs.R
import com.halo.widget.materialdialogs.WhichButton.POSITIVE
import com.halo.widget.materialdialogs.actions.hasActionButtons
import com.halo.widget.materialdialogs.actions.setActionButtonEnabled
import com.halo.widget.materialdialogs.list.SingleChoiceListener
import com.halo.widget.materialdialogs.list.getItemSelector
import com.halo.widget.materialdialogs.utils.inflate
import com.halo.widget.materialdialogs.utils.maybeSetTextColor

/**
 * @author ndn
 * Create by ndn on 03/11/2018
 */
internal class SingleChoiceViewHolder(
        itemView: View,
        private val adapter: SingleChoiceDialogAdapter
) : RecyclerView.ViewHolder(itemView), OnClickListener {

    init {
        itemView.setOnClickListener(this)
    }

    val controlView: AppCompatRadioButton = itemView.findViewById(R.id.md_control)
    val titleView: AppCompatTextView = itemView.findViewById(R.id.md_title)

    var isEnabled: Boolean
        get() = itemView.isEnabled
        set(value) {
            itemView.isEnabled = value
            controlView.isEnabled = value
            titleView.isEnabled = value
        }

    override fun onClick(view: View) = adapter.itemClicked(adapterPosition)
}

/**
 * The default list adapter for single choice (radio button) list dialogs.
 *
 * @author Aidan Follestad (afollestad)
 */
internal class SingleChoiceDialogAdapter(
        private var dialog: MaterialDialog,
        internal var items: List<String>,
        disabledItems: IntArray?,
        initialSelection: Int,
        private val waitForActionButton: Boolean,
        internal var selection: SingleChoiceListener
) : RecyclerView.Adapter<SingleChoiceViewHolder>(), DialogAdapter<String, SingleChoiceListener> {

    private var currentSelection: Int = initialSelection
        set(value) {
            val previousSelection = field
            field = value
            notifyItemChanged(previousSelection)
            notifyItemChanged(value)
        }
    private var disabledIndices: IntArray = disabledItems ?: IntArray(0)

    internal fun itemClicked(index: Int) {
        this.currentSelection = index
        if (waitForActionButton && dialog.hasActionButtons()) {
            // Wait for action button, don't call listener
            // so that positive action button press can do so later.
            dialog.setActionButtonEnabled(POSITIVE, true)
        } else {
            // Don't wait for action button, call listener and dismiss if auto dismiss is applicable
            this.selection?.invoke(dialog, index, this.items[index])
            if (dialog.autoDismissEnabled && !dialog.hasActionButtons()) {
                dialog.dismiss()
            }
        }
    }

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): SingleChoiceViewHolder {
        val listItemView: View = parent.inflate(dialog.windowContext, R.layout.md_listitem_singlechoice)
        val viewHolder = SingleChoiceViewHolder(
                itemView = listItemView,
                adapter = this
        )
        viewHolder.titleView.maybeSetTextColor(dialog.windowContext, R.attr.md_color_content)
        return viewHolder
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(
            holder: SingleChoiceViewHolder,
            position: Int
    ) {
        holder.isEnabled = !disabledIndices.contains(position)

        holder.controlView.isChecked = currentSelection == position
        holder.titleView.text = items[position]
        holder.itemView.background = dialog.getItemSelector()

        if (dialog.bodyFont != null) {
            holder.titleView.typeface = dialog.bodyFont
        }
    }

    override fun positiveButtonClicked() {
        if (currentSelection > -1) {
            selection?.invoke(dialog, currentSelection, items[currentSelection])
        }
    }

    override fun replaceItems(
            items: List<String>,
            listener: SingleChoiceListener
    ) {
        this.items = items
        this.selection = listener
        this.notifyDataSetChanged()
    }

    override fun disableItems(indices: IntArray) {
        this.disabledIndices = indices
        notifyDataSetChanged()
    }

    override fun checkItems(indices: IntArray) {
        val targetIndex = if (indices.isNotEmpty()) indices[0] else -1
        if (this.disabledIndices.contains(targetIndex)) return
        this.currentSelection = targetIndex
    }

    override fun uncheckItems(indices: IntArray) {
        val targetIndex = if (indices.isNotEmpty()) indices[0] else -1
        if (this.disabledIndices.contains(targetIndex)) return
        this.currentSelection = -1
    }

    override fun toggleItems(indices: IntArray) {
        val targetIndex = if (indices.isNotEmpty()) indices[0] else -1
        if (this.disabledIndices.contains(targetIndex)) return
        if (indices.isEmpty() || this.currentSelection == targetIndex) {
            this.currentSelection = -1
        } else {
            this.currentSelection = targetIndex
        }
    }

    override fun checkAllItems() = Unit

    override fun uncheckAllItems() = Unit

    override fun toggleAllChecked() = Unit

    override fun isItemChecked(index: Int) = this.currentSelection == index
}
