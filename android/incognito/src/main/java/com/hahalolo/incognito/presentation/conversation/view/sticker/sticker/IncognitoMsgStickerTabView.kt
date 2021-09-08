package com.hahalolo.incognito.presentation.conversation.view.sticker.sticker

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoMsgStickerTabViewBinding

class IncognitoMsgStickerTabView : FrameLayout{
    constructor(context: Context) : super(context){initView()}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){initView()}
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){initView()}

    private lateinit var binding: IncognitoMsgStickerTabViewBinding

    private fun initView(){
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.incognito_msg_sticker_tab_view,
            this, false
        )
        addView(binding.root)
    }


    fun updateIcon(link:String?){
        Glide.with(context)
            .load(link)
            .into(binding.iconTab)
    }

    fun onTabSelected() {
        binding.layoutTab.setBackgroundResource(
            R.drawable.bg_incognito_msg_sticker_tab_selected
        )
    }

    fun onTabUnselected() {
        binding.layoutTab.setBackgroundColor(Color.TRANSPARENT)
    }
}