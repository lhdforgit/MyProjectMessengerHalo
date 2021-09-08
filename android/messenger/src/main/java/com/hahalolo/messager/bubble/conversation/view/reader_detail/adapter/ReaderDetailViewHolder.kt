package com.hahalolo.messager.bubble.conversation.view.reader_detail.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.hahalolo.messager.bubble.conversation.view.reader_detail.ChatReaderDetailListener
import com.halo.data.room.entity.MemberEntity
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatReaderDetailItemBinding

class ReaderDetailViewHolder(
    val binding: ChatReaderDetailItemBinding,
    val listener: ChatReaderDetailListener?,
    val requestManager: RequestManager?
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(member: MemberEntity?) {
        member?.run {
            updateAvatar(getAvatar())
            updateUsername(memberName(), getFullName(), isHaveNickname())
            bindAction(this.userId())
        }
    }

    private fun bindAction(userId: String) {
        binding.root.setOnClickListener {
            listener?.onItemClick(userId)
        }
    }

    private fun updateUsername(userName: String?, fullName: String?, isHaveNickName: Boolean) {
        userName?.let {
            binding.nameTv.text = it
        }
        if (isHaveNickName) {
            binding.subNameTv.visibility = View.VISIBLE
            fullName?.let {
                binding.subNameTv.text = it
            }
        } else {
            binding.subNameTv.visibility = View.GONE
        }
    }

    private fun updateAvatar(avatar: String?) {
        avatar?.let {
            requestManager?.load(it)
                ?.placeholder(R.drawable.ic_dummy_personal_circle)
                ?.error(R.drawable.ic_dummy_personal_circle)
                ?.circleCrop()
                ?.into(binding.avatarIv)
        }
    }

    companion object {
        @JvmStatic
        fun createHolder(
            parent: ViewGroup,
            listener: ChatReaderDetailListener?,
            requestManager: RequestManager?
        ): ReaderDetailViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = DataBindingUtil.inflate<ChatReaderDetailItemBinding>(
                inflater,
                R.layout.chat_reader_detail_item,
                parent,
                false
            )
            return ReaderDetailViewHolder(binding, listener, requestManager)
        }
    }

}