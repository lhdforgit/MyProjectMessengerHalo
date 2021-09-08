package com.hahalolo.progress.custom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.hahalolo.progress.util.generateSimpleProgressView

internal class ProgressAdapter(
    @LayoutRes private val layoutResId: Int,
    private val itemCount: Int,
    private val attributes: Attributes
) : RecyclerView.Adapter<ProgressAdapter.ProgressViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgressViewHolder {
        val originView = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
        val skeleton = originView.generateSimpleProgressView(attributes)
        return ProgressViewHolder(skeleton.also { it.showSkeleton() })
    }

    override fun onBindViewHolder(holder: ProgressViewHolder, position: Int) = Unit

    override fun getItemCount() = itemCount

    internal class ProgressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
