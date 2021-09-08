package com.hahalolo.messager.bubble.conversation.home.adapter

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.recyclerview.widget.RecyclerView
import com.hahalolo.messenger.databinding.MessageSearchInputItemBinding
import com.halo.common.utils.ktx.executeAfter


class SearchInputViewHolder(
    val binding: MessageSearchInputItemBinding,
    val searchListener: SearchInputListener
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.executeAfter {
            this.searchTv.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(s: CharSequence?, start: Int,
                                               count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int,
                                           before: Int, count: Int) {
                    val query = s?.toString()?.trim() ?: ""
                    searchListener.onSearch(query)
                    binding.btnClose.visibility = if (TextUtils.isEmpty(query)) View.INVISIBLE else View.VISIBLE
                }
            })

            this.searchTv.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val query = v.text?.toString()?.trim()?:""
                    searchListener.onSearch(query)
                    return@OnEditorActionListener true
                }
                false
            })

            this.btnClose.setOnClickListener {
                binding.searchTv.setText("")
                searchListener.onCloseSearch(binding.searchTv)
            }
        }
    }

    fun requestFocus(request:Boolean){
        if(request){
            binding.searchTv.requestFocus()
            searchListener.onOpenSearch(binding.searchTv)
        }else{
            binding.searchTv.setText("")
            binding.searchTv.clearFocus()
            searchListener.onCloseSearch(binding.searchTv)
        }
    }

    fun bind() {

    }
}