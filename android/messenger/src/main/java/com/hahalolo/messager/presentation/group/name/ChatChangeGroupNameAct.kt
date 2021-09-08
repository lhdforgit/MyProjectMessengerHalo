/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.presentation.group.name

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.hahalolo.messager.presentation.base.AbsMessBackActivity
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatMessageChangeGroupNameActBinding
import com.halo.common.utils.KeyboardUtils
import com.halo.data.common.resource.StatusNetwork
import com.halo.data.common.utils.Strings
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
 * Created on 10/6/2018.
 */
class ChatChangeGroupNameAct : AbsMessBackActivity() {
    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private var binding: ChatMessageChangeGroupNameActBinding? = null
    private var viewModel: ChatChangeGroupNameViewModel? = null
    private var oldNameGroup = ""

    override fun initActionBar() {
        setSupportActionBar(binding?.toolbar)
    }

    override fun isLightTheme(): Boolean {
        return true
    }

    override fun initializeBindingViewModel() {
        binding = DataBindingUtil.setContentView(this, R.layout.chat_message_change_group_name_act)
        viewModel =
            ViewModelProvider(this, factory).get(ChatChangeGroupNameViewModel::class.java)
    }

    override fun initializeLayout() {
        initAction()
        initData()
    }

    private fun initData() {
        intent?.run {
            getStringExtra(ROOM_ID)?.let { roomId ->
                viewModel?.roomId = roomId
            }
            getStringExtra(OLD_NAME)?.let { oldName ->
                binding?.inputGroupName?.setText(oldName)
                oldNameGroup = oldName
            }
        }
        enableConfirmBt(false)
    }

    private fun onDelayShowKeyBroad() {
        observeDelay(300, object : DisposableObserver<Long>() {
            override fun onNext(t: Long) {

            }

            override fun onError(e: Throwable) {
            }

            override fun onComplete() {
                KeyboardUtils.showSoftInput(binding?.inputGroupName)
            }
        })
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initAction() {
        Strings.checkMoreSpace(binding?.inputGroupName)
        binding?.inputGroupName?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val newNameGroup = p0?.toString()?.trim() ?: ""
                val lengthNewName = newNameGroup.length
                enableConfirmBt(
                    lengthNewName < MAX_LENGTH && newNameGroup.trim()
                        .isNotEmpty() && !TextUtils.equals(oldNameGroup, newNameGroup)
                )

                if (lengthNewName > MAX_LENGTH) {
                    try{
                        binding?.inputGroupName?.setText(newNameGroup.substring(0, MAX_LENGTH))
                    }catch (e: Exception){
                        e.printStackTrace()
                    }

                    binding?.inputGroupName?.error = getString(
                        R.string.chat_message_max_lenght_group_name,
                        MAX_LENGTH
                    )
                } else {
                    binding?.inputGroupName?.error = null
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        binding?.confirmBt?.setOnClickListener {
            handleChangeName()
        }
        binding?.nestedScroll?.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
            KeyboardUtils.hideSoftInput(binding?.inputGroupName)
            return@OnTouchListener true
        })
    }

    private fun handleChangeName() {
        binding?.inputGroupName?.text?.toString()?.trim()?.let { newName ->
            viewModel?.updateRoomName(newName)
            viewModel?.updateRoomNameResponse?.observe(this, Observer {
                it?.run {
                    if (statusNetwork == StatusNetwork.SUCCESS) {
                        data?.takeIf { it.isNotEmpty() }?.run {
                            Toast.makeText(
                                this@ChatChangeGroupNameAct,
                                getString(R.string.chat_message_change_nick_name_success),
                                Toast.LENGTH_SHORT
                            ).show()
                            onDelayDismiss()
                        } ?: kotlin.run {
                            errorNetwork()
                        }
                    } else if (statusNetwork != StatusNetwork.LOADING) {
                        errorNetwork()
                    }
                }
            })
        }
    }

    private fun handleChangeNameSuccess() {
        val intent = Intent()
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun onDelayDismiss() {
        observeDelay(500, object : DisposableObserver<Long>() {
            override fun onNext(t: Long) {

            }

            override fun onError(e: Throwable) {

            }

            override fun onComplete() {
                handleChangeNameSuccess()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.chat_message_changed_group_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.cancel_action) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun enableConfirmBt(enable: Boolean) {
        binding?.confirmBt?.alpha = if (enable) 1f else 0.5f
        binding?.confirmBt?.isEnabled = enable
    }

    private fun showErrorMessage(sid: Int) {
        Toast.makeText(this, sid, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val ROOM_ID = "ChatMessageChangeGroupNameAct_GROUP_ID"
        private const val OLD_NAME = "ChatMessageChangeGroupNameAct_OLD_NAME"
        private const val MAX_LENGTH = 255

        fun getIntent(context: Context, groupId: String, oldName: String): Intent {
            val intent = Intent(context, ChatChangeGroupNameAct::class.java)
            intent.putExtra(ROOM_ID, groupId)
            intent.putExtra(OLD_NAME, oldName)
            return intent
        }
    }

    private var disposables: CompositeDisposable? = null

    private fun observeDelay(time: Long, longDisposableObserver: DisposableObserver<Long>) {
        if (disposables == null) disposables = CompositeDisposable()
        disposables?.add(
            Observable.timer(time, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(longDisposableObserver)
        )
    }

    private fun clearCache() {
        disposables?.run { this.clear() }
    }

    override fun onDestroy() {
        clearCache()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        viewModel?.onResumeChatAct()
    }

    override fun finish() {
        viewModel?.onFinishChatAct()
        super.finish()
    }
}
