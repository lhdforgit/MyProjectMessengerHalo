package com.hahalolo.messager.presentation.main.contacts.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.hahalolo.messager.presentation.adapter.preload.PreloadHolder
import com.hahalolo.messager.presentation.main.contacts.ChatContactsListener
import com.hahalolo.messager.utils.Strings
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatContactItemBinding
import com.halo.data.entities.contact.Contact

class ChatContactsViewHolder private constructor(
    private val info: Boolean,
    private val binding: ChatContactItemBinding,
    private val requestManager: RequestManager,
    private val listener: ChatContactsListener
) : RecyclerView.ViewHolder(binding.root), PreloadHolder {


    fun bind(entity: Contact) {
        this.binding.conversationNameTv.text = entity.fullName
        requestManager.load(entity.avatar)
            .error(R.drawable.ic_dummy_personal_circle)
            .placeholder(R.drawable.ic_dummy_personal_circle)
            .circleCrop()
            .into(this.binding.avatarIv)

        this.binding.isInfo = info

        this.binding.info.setOnClickListener {
            listener.detailUser(entity.userId, entity.contactId)
        }
        itemView.setOnClickListener { listener.onMessage(entity) }
        binding.executePendingBindings()
    }


    companion object {
        fun getHolder(
            info: Boolean,
            parent: ViewGroup,
            requestManager: RequestManager,
            listener: ChatContactsListener
        ): ChatContactsViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = DataBindingUtil.inflate<ChatContactItemBinding>(
                inflater,
                R.layout.chat_contact_item,
                parent,
                false
            )
            return ChatContactsViewHolder(info, binding, requestManager, listener)
        }
    }

    override fun getTargets(): MutableList<View> {
        val views = mutableListOf<View>()
        views.add(binding.avatarIv)
        return views
    }

    override fun invalidateLayout(requestManager: RequestManager?) {
        requestManager?.clear(binding.avatarIv)
        binding.avatarIv.setImageDrawable(null)
    }
}
