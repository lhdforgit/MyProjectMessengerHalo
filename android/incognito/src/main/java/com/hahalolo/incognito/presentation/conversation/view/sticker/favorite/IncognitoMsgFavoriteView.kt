package com.hahalolo.incognito.presentation.conversation.view.sticker.favorite

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoMsgFavoriteViewBinding
import com.hahalolo.incognito.presentation.conversation.view.sticker.IncognitoMessageStickerListener
import com.hahalolo.incognito.presentation.conversation.view.sticker.sticker.IncognitoMsgStickerAdapter

class IncognitoMsgFavoriteView : FrameLayout {

    constructor(context: Context) : super(context){initView()}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){initView()}
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){initView()}

    private var listener: IncognitoMessageStickerListener? = null

    private lateinit var binding: IncognitoMsgFavoriteViewBinding
    private var adapter : IncognitoMsgStickerAdapter?=null

    fun updateListener(listener: IncognitoMessageStickerListener) {
        this.listener = listener
        bindLayout()
    }

    private fun bindLayout() {
        listener?.observerFavoritePacks(Observer {
            adapter?.submitList(it)
        })
    }

    private fun initView(){
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.incognito_msg_favorite_view,
            this, false
        )
        addView(binding.root)
        initRecycleView()
    }

    private fun initRecycleView() {
        adapter = IncognitoMsgStickerAdapter(onClick = {
            listener?.onClickFavorite(it)
        })
        binding.listItem.adapter = adapter
        binding.listItem.layoutManager = GridLayoutManager(context, 4)

    }
}