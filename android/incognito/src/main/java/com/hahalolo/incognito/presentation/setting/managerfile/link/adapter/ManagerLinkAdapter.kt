package com.hahalolo.incognito.presentation.setting.managerfile.link.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.hahalolo.incognito.databinding.IncognitoManagerLinkItemBinding
import com.hahalolo.incognito.presentation.setting.managerfile.util.AbsIncognitoPreloadAdapter
import com.hahalolo.incognito.presentation.setting.model.ManagerLinkModel
import com.halo.common.utils.GlideRequestBuilder
import com.halo.common.utils.ThumbImageUtils
import com.halo.widget.ErrorHideEmptyHolder

class ManagerLinkAdapter(
    diffCallback: ManagerLinkDiffCallback,
    preloadSizeProvider: ViewPreloadSizeProvider<ManagerLinkModel>,
    requestManager: RequestManager,
    private val onClickListener: View.OnClickListener
) :
    AbsIncognitoPreloadAdapter<ManagerLinkModel>(
        diffCallback,
        preloadSizeProvider,
        requestManager
    ) {

    companion object {
        const val LINK_ITEM_VIEW = 111
        const val DATE_ITEM_VIEW = 112
        const val ERROR_ITEM_VIEW = 113
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            LINK_ITEM_VIEW -> {
                ManagerLinkViewHolder.createHolder(parent, requestManager, onClickListener)
            }
            ERROR_ITEM_VIEW -> {
                ErrorHideEmptyHolder.build(parent)
            }
            else -> {
                ErrorHideEmptyHolder.build(parent)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ManagerLinkViewHolder -> {
                holder.bind(getItem(position))
            }
            is ErrorHideEmptyHolder -> {
                holder.bind()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
       getItem(position)?.let { item ->
            if (TextUtils.isEmpty(item.id) || TextUtils.isEmpty(item.url) || TextUtils.isEmpty(item.title)) {
                return ERROR_ITEM_VIEW
            }
        }
        return LINK_ITEM_VIEW
    }

    override fun getCountPreload(): Int {
        return 3
    }

}

class ManagerLinkViewHolder(
    val binding: IncognitoManagerLinkItemBinding,
    val requestManager: RequestManager,
    val onClickListener: View.OnClickListener
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(linkModel: ManagerLinkModel?) {
        linkModel?.let { item ->
            binding.apply {
                linkTv.text = item.url ?: ""
                titleTv.text = item.title ?: ""
                desTv.text = item.description ?: ""
                GlideRequestBuilder
                    .getCenterCropRequest(requestManager)
                    .load(ThumbImageUtils.thumb(item.thumb))
                    .into(avatarImg)

                root.setOnClickListener(onClickListener)
            }
        }
    }

    companion object {
        fun createHolder(
            viewGroup: ViewGroup,
            requestManager: RequestManager,
            onClickListener: View.OnClickListener
        ): ManagerLinkViewHolder {
            val layoutInflater = LayoutInflater.from(viewGroup.context)
            val binding = IncognitoManagerLinkItemBinding.inflate(layoutInflater, viewGroup, false)
            return ManagerLinkViewHolder(binding, requestManager, onClickListener)
        }
    }

}


class ManagerLinkDiffCallback : DiffUtil.ItemCallback<ManagerLinkModel>() {
    override fun areItemsTheSame(oldItem: ManagerLinkModel, newItem: ManagerLinkModel): Boolean {
        return TextUtils.equals(oldItem.id, newItem.id)
    }

    override fun areContentsTheSame(oldItem: ManagerLinkModel, newItem: ManagerLinkModel): Boolean {
        return TextUtils.equals(oldItem.id, newItem.id)
                && TextUtils.equals(oldItem.url, newItem.url)

    }

}
