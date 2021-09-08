package com.hahalolo.incognito.presentation.conversation.view.sticker.sticker

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoMsgStickerPackViewBinding
import com.hahalolo.incognito.presentation.conversation.view.sticker.IncognitoMessageStickerListener
import com.halo.widget.room.table.StickerPackTable

class IncognitoMsgStickerPackView : FrameLayout {

    constructor(context: Context) : super(context){initView()}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){initView()}
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){initView()}

    private lateinit var binding: IncognitoMsgStickerPackViewBinding

    private var adapter:IncognitoMsgStickerAdapter?=null

    private var listener: IncognitoMessageStickerListener? = null
    private val packId = MutableLiveData<String>()

    fun updateListener(listener: IncognitoMessageStickerListener) {
        this.listener = listener
        bindLayout()
    }

    private fun initView(){
        binding = DataBindingUtil.inflate(LayoutInflater.from(context),
        R.layout.incognito_msg_sticker_pack_view, this, false)
        addView(binding.root)
        initRecycleView()
    }

    private fun initRecycleView() {
        adapter = IncognitoMsgStickerAdapter(onClick = {
            listener?.onClickSticker(it)
        })
        binding.listItem.adapter = adapter
        binding.listItem.layoutManager = GridLayoutManager(context, 4)

    }

    fun updateSticker(pack: StickerPackTable) {
        listener?.observerSticker(pack.id, Observer {
            adapter?.submitList(it?: mutableListOf())
        })
    }

    private fun bindLayout() {

    }
}