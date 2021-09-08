package com.hahalolo.messager.presentation.search.user.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.hahalolo.messager.presentation.main.contacts.detail.PersonalDetailPopup
import com.hahalolo.messager.presentation.message.ChatMessageAct
import com.hahalolo.messager.presentation.search.user.adapter.SearchUserAdapter
import com.hahalolo.messager.presentation.search.user.adapter.SearchUserCallback
import com.hahalolo.messager.presentation.search.user.adapter.SearchUserDiff
import com.hahalolo.messager.utils.Strings
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatSearchUserResultViewBinding
import com.halo.common.utils.RecyclerViewUtils
import com.halo.data.entities.user.User
import com.halo.widget.HaloLinearLayoutManager

class UserSearchResultView : FrameLayout, SearchUserCallback {

    private var binding: ChatSearchUserResultViewBinding? = null
    private var adapter: SearchUserAdapter? = null
    private val requestManager by lazy { Glide.with(this) }

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context)
    }

    private fun initView(context: Context) {
        val layoutInflater = LayoutInflater.from(context)
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.chat_search_user_result_view,
            null,
            false
        )
        addView(
            binding?.root,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        initLayout()
    }

    private fun initLayout() {
        binding?.apply {
            userRec.layoutManager = HaloLinearLayoutManager(context)
            RecyclerViewUtils.optimization(userRec, this@UserSearchResultView)
            val viewPreloadSizeProvider = ViewPreloadSizeProvider<User>()
            adapter = SearchUserAdapter(
                this@UserSearchResultView,
                SearchUserDiff(),
                viewPreloadSizeProvider,
                requestManager
            )

            userRec.adapter = adapter

            binding?.isEmpty = true

            actionBt.setOnClickListener {
                val current = adapter?.isShowMore ?: false
                adapter?.isShowMore = !current

                if (adapter?.isShowMore == true) {
                    actionBt.text = "Less"
                } else {
                    actionBt.text = "More"
                }
            }
        }
    }

    fun submitList(users: MutableList<User>) {
        binding?.isEmpty = users.isEmpty()
        adapter?.submitList(users)
    }

    override fun onSendMessage(userId: String) {
        kotlin.runCatching {
            context?.apply {
                startActivity(ChatMessageAct.getIntent(this, userId))
            }
        }
    }

    override fun onViewPersonalWall(userId: String) {
        kotlin.runCatching {
            getFragmentManager(context)?.run {
                PersonalDetailPopup.startUserDetail(this, userId)
            }
        }
    }

    private fun getFragmentManager(context: Context?): FragmentManager? {
        return when (context) {
            is AppCompatActivity -> context.supportFragmentManager
            is ContextThemeWrapper -> getFragmentManager(context.baseContext)
            else -> null
        }
    }
}