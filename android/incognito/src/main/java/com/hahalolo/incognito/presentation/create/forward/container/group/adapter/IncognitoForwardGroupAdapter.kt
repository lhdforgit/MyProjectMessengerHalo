package com.hahalolo.incognito.presentation.create.forward.container.group.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.hahalolo.incognito.databinding.IncognitoForwardMessageGroupItemBinding
import com.hahalolo.incognito.presentation.create.forward.view.BlankViewHolder
import com.hahalolo.incognito.presentation.setting.managerfile.util.AbsIncognitoPreloadAdapter
import com.hahalolo.incognito.presentation.setting.managerfile.util.IncognitoPreloadHolder
import com.hahalolo.incognito.presentation.setting.model.ContactModel
import com.halo.common.utils.GlideRequestBuilder
import com.halo.common.utils.ThumbImageUtils
import com.halo.common.utils.list.ListUtils
import com.halo.widget.ErrorHideEmptyHolder
import java.util.*

class IncognitoForwardGroupAdapter(
    diffCallback: IncognitoForwardGroupDiffUtil,
    preloadSizeProvider: ViewPreloadSizeProvider<ContactModel>,
    requestManager: RequestManager,
    val listener: ForwardGroupListener
) : AbsIncognitoPreloadAdapter<ContactModel>(diffCallback, preloadSizeProvider, requestManager) {

    companion object {
        const val ERROR_ITEM = 0
        const val LAST_ITEM = 1
        const val GROUP_ITEM = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val holder = when (viewType) {
            GROUP_ITEM -> {
                IncognitoForwardGroupHolder.createHolder(parent, requestManager, listener)
            }
            LAST_ITEM -> {
                BlankViewHolder.createHolder(parent)
            }
            else -> {
                ErrorHideEmptyHolder.build(parent)
            }
        }
        checkPreloadHolder(holder)
        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is IncognitoForwardGroupHolder -> {
                holder.bind(getItem(position))
            }
            is ErrorHideEmptyHolder -> {
                holder.bind()
            }
            is BlankViewHolder -> {
                holder.bind()
            }
        }
        checkPreloadHolder(holder)
    }

    override fun getCountPreload(): Int {
        return 3
    }

    override fun getItemViewType(position: Int): Int {
        return GROUP_ITEM
    }

    fun notifyUpdateStatus(id: String, status: Int) {
        ListUtils.getPosition(currentList()) { input -> TextUtils.equals(input?.id, id) }
            .let { postion ->
                if (postion >= 0 && postion < currentList().size) {
                    getItem(postion)?.status = status
                    notifyItemChanged(postion)
                }
            }
    }
}

class IncognitoForwardGroupHolder(
    val binding: IncognitoForwardMessageGroupItemBinding,
    val requestManager: RequestManager,
    val listener: ForwardGroupListener
) :
    RecyclerView.ViewHolder(binding.root), IncognitoPreloadHolder {

    fun bind(item: ContactModel?) {
        binding.nameTv.text = item?.name ?: ""
//        GlideRequestBuilder
//            .getCenterCropRequest(requestManager)
//            .load(ThumbImageUtils.thumb(item?.avatar))
//            .placeholder(R.drawable.ic_dummy_personal_circle)
//            .into(binding.avatarImg)
        binding.statusView.setStatus(item?.status ?: -1)
        binding.statusView.setOnClickListener { listener.onItemClick(item) }
    }

    companion object {
        @JvmStatic
        fun createHolder(
            viewGroup: ViewGroup,
            requestManager: RequestManager,
            listener: ForwardGroupListener
        ): IncognitoForwardGroupHolder {
            val inflater = LayoutInflater.from(viewGroup.context)
            val binding =
                IncognitoForwardMessageGroupItemBinding.inflate(inflater, viewGroup, false)
            return IncognitoForwardGroupHolder(binding, requestManager, listener)
        }
    }

    override fun getTargets(): MutableList<View> {
        val views = ArrayList<View>()
        //views.add(binding.groupAvatar)
        return views
    }

    override fun invalidateLayout(requestManager: RequestManager?) {
//        requestManager?.clear(binding.avatarImg)
//        binding.avatarImg.setImageDrawable(null)
    }
}

interface ForwardGroupListener {
    fun onItemClick(item: ContactModel?)
}

