package com.hahalolo.incognito.presentation.conversation.adapter.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.databinding.ViewDataBinding

class IncognitoMsgMediaView : FrameLayout{
    constructor(context: Context) : super(context){initView()}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){initView()}
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){initView()}

    private var binding: ViewDataBinding?=null

    private fun initView(){

    }


    private var listMedia = mutableListOf<String>()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val width = MeasureSpec.getSize(widthMeasureSpec)
        val type = MeasureSpec.getMode(widthMeasureSpec)
        val height: Int
        when (listMedia.size) {
            0 -> {
                height = width
            }
            1 -> {
//                val widthSource = listMedia[0].width
//                val heightSource = listMedia[0].height

                val widthSource =0
                val heightSource =0
                if (widthSource > 0) {
                    val maxHeight: Int
                    if (widthSource > heightSource) {
                        maxHeight = width * 9 / 16
                        height = Math.max(heightSource * width / widthSource, maxHeight)
                    } else {
                        maxHeight = width * 16 / 9
                        height = Math.min(heightSource * width / widthSource, maxHeight)
                    }
                } else {
                    height = width
                }
            }
            2 -> {
                height = width / 2
            }
            else -> {
                height = width
            }
        }
        setMeasuredDimension(width, height)
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(width, type),
            MeasureSpec.makeMeasureSpec(height, type)
        )
    }

    fun updateMedia(medias: MutableList<String>){
        removeAllViews()

    }
}