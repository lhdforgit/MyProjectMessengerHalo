package com.hahalolo.incognito.presentation.conversation.view.sticker.gif

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.giphy.sdk.core.models.enums.MediaType
import com.giphy.sdk.core.network.api.GPHApi
import com.giphy.sdk.core.network.api.GPHApiClient
import com.giphy.sdk.core.network.response.ListMediaResponse
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoMsgGifViewBinding
import com.hahalolo.incognito.presentation.conversation.view.sticker.IncognitoMessageStickerListener
import kotlinx.coroutines.*
import java.util.*

class IncognitoMsgGifView : FrameLayout {

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    private lateinit var binding: IncognitoMsgGifViewBinding

    private var gifAdapter: IncognitoMsgGifAdapter? = null

    private var listener: IncognitoMessageStickerListener? = null

    fun updateListener(listener: IncognitoMessageStickerListener) {
        this.listener = listener
        bindLayout()
    }

    private fun bindLayout() {

    }

    private fun initView() {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.incognito_msg_gif_view, this, false
        )
        addView(binding.root)
        initRecycleView()
        initActions()
    }

    private var searchJob: Job?=null

    private fun initActions(){
        binding.closeBt.setOnClickListener {
            binding.searchEt.text?.toString()?.trim()?.takeIf { it.isNotEmpty() }?.run{
                binding.searchEt.setText("")
            }?: run{
                listener?.closeGif()
            }
        }
        binding.searchEt.addTextChangedListener {
            searchJob?.cancel()
            searchJob = CoroutineScope(Dispatchers.Main).launch {
                delay(500)
                loadGifList(it?.toString()?:"")
            }
        }
        loadGifList("")
    }

    private fun initRecycleView() {
        gifAdapter = IncognitoMsgGifAdapter(onClick = {
            listener?.onClickGif(it )
        })
        binding.listItem.adapter = gifAdapter
        binding.listItem.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false )
    }

    fun actionShowKeyBroad() {
        binding.searchEt.requestFocus()
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(binding.searchEt, InputMethodManager.SHOW_IMPLICIT)
    }

    fun actionHideKeyBroad() {
        binding.searchEt.setText("")
        binding.searchEt.clearFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun loadGifList(query: String?) {
        val client: GPHApi = GPHApiClient(context.getString(R.string.key_giphy))
        binding.loading.visibility = VISIBLE
        if (query != null && query.isNotEmpty()) {
            client.search(query, MediaType.gif, null, null, null, null, null) { result: ListMediaResponse?, e: Throwable? ->
                binding.loading.visibility = GONE
                val list = if (result != null && result.data != null) result.data else ArrayList()
                gifAdapter?.updateList(list)
                binding.noResult.visibility = if (list != null && list.isNotEmpty()) GONE else VISIBLE
            }
        } else {
            client.trending(MediaType.gif, null, null, null) { result: ListMediaResponse?, e: Throwable? ->
                binding.loading.visibility = GONE
                val list =
                    if (result != null && result.data != null) result.data else ArrayList()
                gifAdapter?.updateList(list)
                binding.noResult.visibility = if (list != null && list.isNotEmpty()) GONE else VISIBLE
            }
        }
    }
}