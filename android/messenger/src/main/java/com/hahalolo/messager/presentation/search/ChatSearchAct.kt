/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.presentation.search

import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.annotation.IntDef
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import com.hahalolo.messager.presentation.base.AbsMessBackActivity
import com.hahalolo.messager.presentation.search.ChatSearchTab.Companion.CHAT_SEARCH_FRIEND
import com.hahalolo.messager.presentation.search.ChatSearchTab.Companion.CHAT_SEARCH_GROUP
import com.hahalolo.messager.presentation.search.friend.ChatSearchFriendFr
import com.hahalolo.messager.presentation.search.group.ChatSearchGroupFr
import com.hahalolo.messager.presentation.search.user.ChatSearchUserFr
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatSearchActBinding
import com.halo.common.utils.KeyboardUtils
import com.halo.common.utils.ktx.addFragment
import dagger.Lazy
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject


/**
 * @author BuiSon
 * Created by BuiSon
 * Created on 10/23/2018.
 */
class ChatSearchAct : AbsMessBackActivity() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private var binding: ChatSearchActBinding? = null
    private var viewModel: ChatSearchViewModel? = null

    @Inject
    lateinit var chatSearchFriendFr: Lazy<ChatSearchFriendFr>

    @Inject
    lateinit var chatSearchGroupFr: Lazy<ChatSearchGroupFr>

    @Inject
    lateinit var chatSearchUser: Lazy<ChatSearchUserFr>

    private var disposables: CompositeDisposable = CompositeDisposable()

    private var chatSearchPagerAdapter: ChatSearchPagerAdapter? = null

    override fun initActionBar() {
        setSupportActionBar(binding?.toolbar)
    }

    override fun isLightTheme(): Boolean {
        return true
    }

    override fun initializeBindingViewModel() {
        binding = DataBindingUtil.setContentView(this, R.layout.chat_search_act)
        viewModel = ViewModelProvider(this, factory).get(ChatSearchViewModel::class.java)
    }

    override fun initializeLayout() {
        //initViewPager()
        // initActions()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.search_container,chatSearchUser.get())
        transaction.commit()
    }

    /*
    private fun initViewPager() {
        chatSearchPagerAdapter =
            ChatSearchPagerAdapter(
                supportFragmentManager
            )
        chatSearchPagerAdapter?.addFragment(
            chatSearchFriendFr.get(),
            getString(R.string.chat_message_search_friend_title)
        )
        chatSearchPagerAdapter?.addFragment(
            chatSearchGroupFr.get(),
            getString(R.string.chat_message_search_group_title)
        )
        binding?.viewPager?.adapter = chatSearchPagerAdapter
        binding?.tabLayout?.setupWithViewPager(binding?.viewPager)
        binding?.tabLayout?.tabMode = TabLayout.MODE_FIXED
        intent?.getIntExtra(CHAT_SEARCH_TAB, 0)?.takeIf { it in 0..1 }?.run {
            binding?.viewPager?.currentItem = this
        }
    }

    private fun initActions() {
        KeyboardUtils.showSoftInput(binding?.searchView)

        binding?.navigationBt?.setOnClickListener {
            finish()
        }

        binding?.searchView?.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                KeyboardUtils.hideSoftInput(binding?.searchView)
                return@OnEditorActionListener true
            }
            false
        })

        binding?.searchView?.addInputSelectedListener { }

        binding?.searchView?.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString()?.trim() ?: ""
                onUpdateWithQuery(query)
                binding?.clearSearchBtn?.visibility =
                    if (query.isEmpty()) View.INVISIBLE else View.VISIBLE
            }
        })

        binding?.clearSearchBtn?.setOnClickListener {
            binding?.searchView?.setText("")
        }
    }

    private fun onUpdateWithQuery(s: String) {
        disposables.clear()
        observeDelay(300, object : DisposableObserver<Long>() {
            override fun onNext(t: Long) {

            }

            override fun onError(e: Throwable) {

            }

            override fun onComplete() {
            }
        })
    }

    private fun clearCache() {
        if (chatSearchPagerAdapter != null) chatSearchPagerAdapter = null
    }

    fun observeDelay(time: Long, longDisposableObserver: DisposableObserver<Long>) {
        disposables.add(
            Observable.timer(time, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(longDisposableObserver)
        )
    }

    override fun onDestroy() {
        disposables.clear()
        clearCache()
        super.onDestroy()
    }
    */


    companion object {
        val CHAT_SEARCH_TAB = "ChatSearchAct-CHAT_SEARCH_TAB"

        fun getIntent(context: Context): Intent {
            return Intent(context, ChatSearchAct::class.java)
        }

        fun getIntent(context: Context, tab: Int): Intent {
            val intent = Intent(context, ChatSearchAct::class.java)
            intent.putExtra(CHAT_SEARCH_TAB, tab)
            return intent;
        }
    }
}


@Retention(AnnotationRetention.SOURCE)
@IntDef(CHAT_SEARCH_FRIEND, CHAT_SEARCH_GROUP)
annotation class ChatSearchTab {
    companion object {
        const val CHAT_SEARCH_FRIEND = 0
        const val CHAT_SEARCH_GROUP = 1
    }


}
