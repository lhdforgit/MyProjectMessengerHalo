package com.hahalolo.incognito.presentation.setting.managerfile.file.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoManagerDocItemBinding
import com.hahalolo.incognito.presentation.setting.managerfile.util.AbsIncognitoPreloadAdapter
import com.hahalolo.incognito.presentation.setting.managerfile.util.IncognitoPreloadHolder
import com.hahalolo.incognito.presentation.setting.model.ManagerDocModel
import com.halo.widget.ErrorHideEmptyHolder
import java.util.*

class ManagerDocAdapter(
    diffCallback: ManagerDocDiffCallback,
    preloadSizeProvider: ViewPreloadSizeProvider<ManagerDocModel>,
    requestManager: RequestManager,
    private val listener: ManagerDocListener
) : AbsIncognitoPreloadAdapter<ManagerDocModel>(
    diffCallback,
    preloadSizeProvider,
    requestManager
) {

    companion object {
        const val DOC_ITEM_VIEW = 111
        const val DATE_ITEM_VIEW = 112
        const val ERROR_ITEM_VIEW = 113
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val holder: RecyclerView.ViewHolder
        when (viewType) {
            DOC_ITEM_VIEW -> {
                holder = ManagerDocViewHolder.createHolder(parent, requestManager, listener)
            }
            else -> {
                holder = ErrorHideEmptyHolder.build(parent)
            }
        }
        checkPreloadHolder(holder)
        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ManagerDocViewHolder -> {
                holder.bind(getItem(position))
            }
            is ErrorHideEmptyHolder -> {
                holder.bind()
            }
        }
        checkPreloadHolder(holder)
    }

    override fun getItemViewType(position: Int): Int {
        getItem(position)?.let {
            if (TextUtils.isEmpty(it.id) || TextUtils.isEmpty(it.name)) {
                return ERROR_ITEM_VIEW
            }
        }
        return DOC_ITEM_VIEW
    }

    override fun getCountPreload(): Int {
        return 3
    }
}

class ManagerDocViewHolder(
    val binding: IncognitoManagerDocItemBinding,
    val requestManager: RequestManager,
    val listener: ManagerDocListener
) : RecyclerView.ViewHolder(binding.root), IncognitoPreloadHolder {

    private fun getDrawableType(type: Int): Int {
        return when (type) {
            1 -> R.drawable.ic_incognito_manager_file_pdf
            2 -> R.drawable.ic_incognito_manager_file_xls
            3 -> R.drawable.ic_incognito_manager_file_doc
            else -> R.drawable.ic_incognito_manager_file_doc
        }
    }

    fun bind(docModel: ManagerDocModel?) {
        docModel?.let {item ->
            binding.apply {
                titleTv.text = item.name ?: ""
                sizeFileTv.text = item.size ?: ""
                usernameTv.text = item.userName ?: ""
                avatarImg.setBackgroundResource(getDrawableType(item.type ?: 0))
                root.setOnClickListener{
                    listener.onItemClick(item)
                }
                menuBt.setOnClickListener{
                    listener.onItemClick(item)
                }
            }
        }
    }

    override fun getTargets(): MutableList<View> {
        val views = ArrayList<View>()
        views.add(binding.avatarImg)
        return views
    }

    override fun invalidateLayout(requestManager: RequestManager?) {
        requestManager?.clear(binding.avatarImg)
        binding.avatarImg.setImageDrawable(null)
    }

    companion object {
        fun createHolder(
            viewGroup: ViewGroup,
            requestManager: RequestManager,
            listener: ManagerDocListener
        ): ManagerDocViewHolder {
            val layoutInflater = LayoutInflater.from(viewGroup.context)
            val binding = IncognitoManagerDocItemBinding.inflate(layoutInflater, viewGroup, false)
            return ManagerDocViewHolder(binding, requestManager, listener)
        }
    }

}

class ManagerDocDiffCallback : DiffUtil.ItemCallback<ManagerDocModel>() {
    override fun areItemsTheSame(oldItem: ManagerDocModel, newItem: ManagerDocModel): Boolean {
        return TextUtils.equals(oldItem.id, newItem.id)
    }

    override fun areContentsTheSame(oldItem: ManagerDocModel, newItem: ManagerDocModel): Boolean {
        return TextUtils.equals(oldItem.id, newItem.id)
                && TextUtils.equals(oldItem.name, newItem.name)

    }
}

interface ManagerDocListener{
    fun onItemClick(item : ManagerDocModel)
}