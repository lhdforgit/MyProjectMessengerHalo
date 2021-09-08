package com.hahalolo.incognito.presentation.setting.invite.adapter

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.*
import com.hahalolo.incognito.presentation.base.PagingIncognitoAdapterWithHeader
import com.hahalolo.incognito.presentation.setting.model.ContactModel
import com.halo.common.utils.ktx.executeAfter
import com.halo.data.entities.contact.Contact
import com.halo.widget.databinding.ErrorHideEmptyHolderBinding

class IncognitoInviteContactAdapter
constructor(
    val listener: IncognitoInviteContactListener,
    val requestManager: RequestManager
) : PagingIncognitoAdapterWithHeader<Contact, RecyclerView.ViewHolder>(
    ITEM_COMPARATOR,
    headerCount
) {
    private var listInvited = mutableListOf<String>()

    fun setListInvited(listId: MutableList<String>) {
        this.listInvited.clear()
        this.listInvited = listId
        notifyDataSetChanged()
    }

    companion object {
        const val headerCount = 2
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
        private const val ITEM_LINK_INVITE_TYPE = 3
    }


    override fun getItemViewType(position: Int): Int {
        return when {
            isSearchView(position) -> ITEM_SEARCH_VIEW_TYPE
            isLinkView(position) -> ITEM_LINK_INVITE_TYPE
            isErrorData(position) -> ITEM_VIEW_ERROR_TYPE
            else -> ITEM_VIEW_CONTACT_TYPE
        }
    }

    private fun isSearchView(position: Int): Boolean {
        return position == 0
    }

    private fun isLinkView(position: Int): Boolean {
        return position == 1
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
            ITEM_LINK_INVITE_TYPE -> {
                IncognitoInviteContactHolder.LinkInvitedHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.incognito_invite_link_view_item,
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
                        R.layout.incognito_invite_contact_item,
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
            is IncognitoInviteContactHolder.LinkInvitedHolder -> {
                holder.binding.executeAfter {
                    this.linkView.setOnClickListener { listener.onGetLinkInvite() }
                }
            }
            is IncognitoInviteContactHolder.ErrorHolder -> {
                holder.binding.executeAfter {

                }
            }
            is IncognitoInviteContactHolder.ContactHolder -> {
                holder.binding.executeAfter {
                    getItem(position)?.let { contact ->
                        ContactModel.convertToModel(contact).let {
                            this.nameTv.text = it.name ?: ""

                            requestManager.apply {
                                load(it.avatar ?: "")
                                    .placeholder(R.drawable.ic_dummy_personal_circle)
                                    .error(R.drawable.ic_dummy_personal_circle)
                                    .circleCrop()
                                    .into(avatarImg)
                            }
                            this.iconBt.setImageResource(
                                if (listener.isInvited(contactId = it.id))
                                    R.drawable.ic_incognito_invite_friend_success
                                else
                                    R.drawable.ic_incognito_invite_friend
                            )
                            this.iconBt.setOnClickListener {
                                this.iconBt.setImageResource(R.drawable.ic_incognito_invite_friend_success)
                                listener.onInviteFriend(contact)
                            }
                        }
                    }
                }
            }
        }
    }

    sealed class IncognitoInviteContactHolder(view: View) : RecyclerView.ViewHolder(view) {

        class ErrorHolder(val binding: ErrorHideEmptyHolderBinding) :
            IncognitoInviteContactHolder(binding.root)

        class ContactHolder(val binding: IncognitoInviteContactItemBinding) :
            IncognitoInviteContactHolder(binding.root)

        class LinkInvitedHolder(val binding: IncognitoInviteLinkViewItemBinding) :
            IncognitoInviteContactHolder(binding.root)

        class SearchViewHolder(val binding: IncognitoInviteSearchViewItemBinding) :
            IncognitoInviteContactHolder(binding.root)
    }
}

// Adapter that displays a loading spinner when
// state = LoadState.Loading, and an error message and retry
// button when state is LoadState.Error.
class InviteContactLoadStateAdapter(val callback: LoadStateCallback) :
    LoadStateAdapter<LoadStateViewHolder>() {
    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<LoadStatePagingItemBinding>(
            inflater,
            R.layout.load_state_paging_item,
            parent,
            false
        )
        return LoadStateViewHolder(callback, binding)
    }
}

class LoadStateViewHolder(
    val callback: LoadStateCallback,
    val binding: LoadStatePagingItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(loadState: LoadState) {
        binding.apply {
            loading.visibility = if (loadState is LoadState.Loading) View.VISIBLE else View.GONE
            retryBt.visibility = if (loadState is LoadState.Error) View.VISIBLE else View.GONE
            retryBt.setOnClickListener { callback.retry() }
        }
    }
}

interface LoadStateCallback {
    fun retry()
}