package com.hahalolo.incognito.presentation.view.avatar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.DataBindingUtil
import androidx.transition.TransitionManager
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoGroupAvatarViewBinding
import kotlin.random.Random


class GroupAvatar : FrameLayout {

    constructor(context: Context) : this(context, null) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    private lateinit var binding: IncognitoGroupAvatarViewBinding

    private fun initView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context),
            R.layout.incognito_group_avatar_view,
            this,
            false)
        addView(binding.root)
        initializeLayout()
    }

    private fun initializeLayout() {
        if (Random.nextBoolean()) {
            updateAvatar(mutableListOf("", "", "", ""))
        } else {
            updateAvatar(mutableListOf(""))
        }
    }

    fun updateAvatar(avatars:MutableList<String>){

        binding.countTv.visibility = if(avatars.size>2) View.VISIBLE else View.INVISIBLE
        binding.countTv.text = avatars.size.toString()
        binding.avatar2.visibility = if(avatars.size>=2)  View.VISIBLE else View.INVISIBLE
        binding.avatar1.visibility = View.VISIBLE

        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.avatarGr)
        constraintSet.setGuidelinePercent(R.id.colum_1, if(avatars.size>=2) 0.75f else 1f)
        constraintSet.setGuidelinePercent(R.id.row_1, if(avatars.size>=2) 0.75f else 1f)
        TransitionManager.beginDelayedTransition(binding.avatarGr)
        constraintSet.applyTo(binding.avatarGr)
    }
}