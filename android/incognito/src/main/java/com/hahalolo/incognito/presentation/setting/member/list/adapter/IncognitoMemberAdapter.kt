package com.hahalolo.incognito.presentation.setting.member.list.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoMemberHeaderItemBinding
import com.hahalolo.incognito.databinding.IncognitoMemberListItemBinding
import com.hahalolo.incognito.presentation.setting.model.MemberItem
import com.hahalolo.incognito.presentation.setting.model.MemberModel
import com.halo.common.utils.GlideRequestBuilder
import com.halo.common.utils.ThumbImageUtils
import com.halo.widget.ErrorHideEmptyHolder

class IncognitoMemberAdapter(
    val requestManager: RequestManager,
    val listener: IncognitoMemberListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listMember = mutableListOf<MemberItem>()


    companion object {
        const val EMPTY_ITEM = 0
        const val MEMBER_ITEM = 1
        const val HEADER_ITEM = 2
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            EMPTY_ITEM -> {
                ErrorHideEmptyHolder.build(parent)
            }
            MEMBER_ITEM -> {
                IncognitoMemberViewHolder.createHolder(parent, requestManager, listener)
            }
            HEADER_ITEM -> {
                IncognitoMemberHeaderViewHolder.createHolder(parent)
            }
            else -> {
                ErrorHideEmptyHolder.build(parent)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = listMember[position]
        when (holder) {
            is IncognitoMemberViewHolder -> {
                (item as MemberItem.Member).member.let {
                    holder.bind(it)
                }
            }
            is IncognitoMemberHeaderViewHolder -> {
                (item as MemberItem.Header).title.let {
                    holder.bind(it)
                }
            }
            is ErrorHideEmptyHolder -> {
                holder.bind()
            }
        }
    }

    override fun getItemCount(): Int {
        return listMember.size
    }

    override fun getItemViewType(position: Int): Int {
        val item = listMember[position]
        return when (item) {
            is MemberItem.Header -> {
                HEADER_ITEM
            }
            is MemberItem.Member -> {
                if (TextUtils.isEmpty(item.member.id) || TextUtils.isEmpty(item.member.id)) {
                    EMPTY_ITEM
                } else {
                    MEMBER_ITEM
                }
            }
        }
    }

    fun updateData(listNew: List<MemberItem>) {
        //val diffResult = DiffUtil.calculateDiff(IncognitoMemberDiffUtil(listMember, listNew))
        listMember.clear()
        listMember.addAll(listNew)
        notifyDataSetChanged()
    }

}


class IncognitoMemberHeaderViewHolder(val binding: IncognitoMemberHeaderItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(title: String) {
        binding.titleTv.text = title
        binding.executePendingBindings()
    }

    companion object {
        @JvmStatic
        fun createHolder(
            viewGroup: ViewGroup,
        ): IncognitoMemberHeaderViewHolder {
            val inflater = LayoutInflater.from(viewGroup.context)
            val binding = IncognitoMemberHeaderItemBinding.inflate(inflater, viewGroup, false)
            return IncognitoMemberHeaderViewHolder(binding)
        }
    }
}

class IncognitoMemberViewHolder(
    val binding: IncognitoMemberListItemBinding,
    val requestManager: RequestManager,
    val listener: IncognitoMemberListener
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: MemberModel?) {
        binding.nameTv.text = item?.name ?: ""

        GlideRequestBuilder
            .getCenterCropRequest(requestManager)
            .load(ThumbImageUtils.thumb(item?.avatar))
            .placeholder(R.drawable.ic_dummy_personal_circle)
            .into(binding.avatarImg)

        binding.root.setOnClickListener { listener.onItemClick() }
        binding.menuBt.setOnClickListener { listener.onItemClick() }
        binding.executePendingBindings()
    }

    companion object {
        @JvmStatic
        fun createHolder(
            viewGroup: ViewGroup,
            requestManager: RequestManager,
            listener: IncognitoMemberListener
        ): IncognitoMemberViewHolder {
            val inflater = LayoutInflater.from(viewGroup.context)
            val binding = IncognitoMemberListItemBinding.inflate(inflater, viewGroup, false)
            return IncognitoMemberViewHolder(binding, requestManager, listener)
        }
    }
}

interface IncognitoMemberListener {
    fun onItemClick()
}

