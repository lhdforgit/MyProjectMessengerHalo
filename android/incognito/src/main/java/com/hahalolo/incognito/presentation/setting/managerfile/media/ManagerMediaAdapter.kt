package com.hahalolo.incognito.presentation.setting.managerfile.media

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.hahalolo.incognito.databinding.IncognitoManagerMediaItemBinding
import com.hahalolo.incognito.presentation.setting.managerfile.util.AbsIncognitoPreloadAdapter
import com.hahalolo.incognito.presentation.setting.managerfile.util.IncognitoPreloadHolder
import com.hahalolo.incognito.presentation.setting.model.ManagerMediaModel
import com.halo.common.utils.GlideRequestBuilder
import com.halo.common.utils.SizeUtils
import com.halo.common.utils.ThumbImageUtils
import com.halo.widget.ErrorHideEmptyHolder
import java.util.*

class ManagerMediaAdapter(
    diffCallback: ManagerMediaDiffCallback,
    preloadSizeProvider: ViewPreloadSizeProvider<ManagerMediaModel>,
    requestManager: RequestManager,
    private val onClickListener: View.OnClickListener
) : AbsIncognitoPreloadAdapter<ManagerMediaModel>(
    diffCallback,
    preloadSizeProvider,
    requestManager
) {
    companion object {
        const val PHOTO_ITEM_VIEW = 111
        const val DATE_ITEM_VIEW = 112
        const val ERROR_ITEM_VIEW = 113
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val holder: RecyclerView.ViewHolder
        when (viewType) {
            PHOTO_ITEM_VIEW -> holder = ManagerMediaViewHolder.createHolder(
                parent,
                requestManager,
                onClickListener
            )
            else -> holder = ErrorHideEmptyHolder.build(parent)
        }
        checkPreloadHolder(holder)
        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ManagerMediaViewHolder -> {
                holder.bind(getItem(position))
            }
            is ErrorHideEmptyHolder -> {
                holder.bind()
            }
        }
        checkPreloadHolder(holder)
    }

    override fun getCountPreload(): Int {
        return 3
    }

    override fun getItemViewType(position: Int): Int {
        getItem(position)?.let {
            if (TextUtils.isEmpty(it.id) || TextUtils.isEmpty(it.url)) {
                return ERROR_ITEM_VIEW
            }
        }
        return PHOTO_ITEM_VIEW
    }
}

class ManagerMediaViewHolder(
    val binding: IncognitoManagerMediaItemBinding,
    val requestManager: RequestManager,
    private val onClickListener: View.OnClickListener,
    private val withParent: Int
) : RecyclerView.ViewHolder(binding.root), IncognitoPreloadHolder {

    fun bind(mediaModel: ManagerMediaModel?) {
        GlideRequestBuilder
            .getCenterCropRequest(requestManager)
            .load(ThumbImageUtils.thumb(mediaModel?.url))
            .into(binding.mediaImg)
        updateMediaSize()
        binding.root.setOnClickListener(onClickListener)
    }

    private fun updateMediaSize() {
        val with = withParent - SizeUtils.dp2px(16f)
        itemView.layoutParams.width = with / 3
        itemView.layoutParams.height = with / 3
    }

    companion object {
        @JvmStatic
        fun createHolder(
            parent: ViewGroup,
            requestManager: RequestManager,
            onClickListener: View.OnClickListener
        ): ManagerMediaViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = IncognitoManagerMediaItemBinding.inflate(layoutInflater, parent, false)
            return ManagerMediaViewHolder(
                binding,
                requestManager,
                onClickListener,
                parent.measuredWidth
            )
        }
    }

    override fun getTargets(): MutableList<View> {
        val views = ArrayList<View>()
        views.add(binding.mediaImg)
        return views
    }

    override fun invalidateLayout(requestManager: RequestManager?) {
        requestManager?.clear(binding.mediaImg)
        binding.mediaImg.setImageDrawable(null)
    }
}

class ManagerMediaDiffCallback : DiffUtil.ItemCallback<ManagerMediaModel>() {
    override fun areItemsTheSame(oldItem: ManagerMediaModel, newItem: ManagerMediaModel): Boolean {
        return TextUtils.equals(oldItem.id, newItem.id)
    }

    override fun areContentsTheSame(
        oldItem: ManagerMediaModel,
        newItem: ManagerMediaModel
    ): Boolean {
        return TextUtils.equals(oldItem.id, newItem.id) && TextUtils.equals(
            oldItem.url,
            newItem.url
        )
    }
}