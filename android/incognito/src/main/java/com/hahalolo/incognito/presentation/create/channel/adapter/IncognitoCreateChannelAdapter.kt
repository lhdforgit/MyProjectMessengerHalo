package com.hahalolo.incognito.presentation.create.channel.adapter

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.*
import com.hahalolo.incognito.presentation.base.PagingIncognitoAdapterWithHeader
import com.halo.common.utils.ktx.executeAfter
import com.halo.data.entities.contact.Contact
import com.halo.widget.databinding.ErrorHideEmptyHolderBinding

class IncognitoCreateChannelAdapter(
    val listener: IncognitoCreateChannelListener,
    val requestManager: RequestManager
) :
    PagingIncognitoAdapterWithHeader<Contact, RecyclerView.ViewHolder>(
        ITEM_COMPARATOR,
        headerCount
    ) {

    companion object {
        const val headerCount = 1
        val ITEM_COMPARATOR =
            object : DiffUtil.ItemCallback<Contact>() {
                override fun areContentsTheSame(
                    oldItem: Contact,
                    newItem: Contact
                ): Boolean {
                    return TextUtils.equals(oldItem.contactId, newItem.contactId)
                }

                override fun areItemsTheSame(
                    oldItem: Contact,
                    newItem: Contact
                ): Boolean {
                    return TextUtils.equals(
                        oldItem.contactId,
                        newItem.contactId
                    ) && TextUtils.equals(
                        oldItem.firstName,
                        newItem.firstName
                    ) && TextUtils.equals(
                        oldItem.lastName,
                        newItem.lastName
                    )
                }
            }
        private const val ITEM_VIEW_ERROR_TYPE = 0
        private const val ITEM_VIEW_CONTACT_TYPE = 1
        private const val ITEM_SEARCH_VIEW_TYPE = 2
    }


    override fun getItemViewType(position: Int): Int {
        return when {
            isSearchView(position) -> ITEM_SEARCH_VIEW_TYPE
            isErrorData(position) -> ITEM_VIEW_ERROR_TYPE
            else -> ITEM_VIEW_CONTACT_TYPE
        }
    }

    private fun isSearchView(position: Int): Boolean {
        return position == 0
    }

    private fun isErrorData(position: Int): Boolean {
        getItem(position)?.apply {
            return contactId?.isEmpty() ?: true
        }
        return true
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_SEARCH_VIEW_TYPE -> {
                IncognitoInviteContactHolder.SearchViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.incognito_invite_search_view_item,
                        parent,
                        false
                    )
                )
            }
            ITEM_VIEW_ERROR_TYPE -> {
                IncognitoInviteContactHolder.ErrorHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.error_hide_empty_holder,
                        parent,
                        false
                    )
                )
            }
            else -> {
                IncognitoInviteContactHolder.ContactHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.incognito_user_create_group_list_item,
                        parent,
                        false
                    )
                )
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is IncognitoInviteContactHolder.SearchViewHolder -> {
                holder.binding.executeAfter {
                    this.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(p0: String?): Boolean {
                            return false
                        }

                        override fun onQueryTextChange(query: String?): Boolean {
                            listener.onSearch(query ?: "")
                            return false
                        }
                    })
                }
            }
            is IncognitoInviteContactHolder.ErrorHolder -> {
                holder.binding.executeAfter {

                }
            }
            is IncognitoInviteContactHolder.ContactHolder -> {
                holder.binding.executeAfter {
                    getItem(position)?.let { contact ->
                        this.nameTv.text = "${contact.lastName}  ${contact.firstName}"

                        requestManager.apply {
                            load(contact.avatar ?: "")
                                .placeholder(R.drawable.ic_dummy_personal_circle)
                                .error(R.drawable.ic_dummy_personal_circle)
                                .circleCrop()
                                .into(avatarImg)
                        }
                        this.checkUserBt.setOnCheckedChangeListener { buttonView, isChecked ->
                            if (isChecked) {
                                listener.onSelect(contact.contactId ?: "")
                            } else {
                                listener.onUnSelect(contact.contactId ?: "")
                            }
                        }
                        this.root.setOnClickListener {
                            this.checkUserBt.isChecked = !this.checkUserBt.isChecked
                        }
                        this.checkUserBt.isChecked = listener.isSelected(contact.contactId ?: "")
                    }
                }
            }
        }
    }

    sealed class IncognitoInviteContactHolder(view: View) : RecyclerView.ViewHolder(view) {

        class ErrorHolder(val binding: ErrorHideEmptyHolderBinding) :
            IncognitoInviteContactHolder(binding.root)

        class ContactHolder(val binding: IncognitoUserCreateGroupListItemBinding) :
            IncognitoInviteContactHolder(binding.root)

        class SearchViewHolder(val binding: IncognitoInviteSearchViewItemBinding) :
            IncognitoInviteContactHolder(binding.root)
    }
}
