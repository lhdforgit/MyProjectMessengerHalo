package com.hahalolo.incognito.presentation.create.channel

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoCreateGroupActBinding
import com.hahalolo.incognito.presentation.base.AbsIncBackActivity
import com.hahalolo.incognito.presentation.base.IncognitoLoadStateAdapter
import com.hahalolo.incognito.presentation.base.IncognitoLoadStateCallback
import com.hahalolo.incognito.presentation.create.channel.adapter.IncognitoCreateChannelAdapter
import com.hahalolo.incognito.presentation.create.channel.adapter.IncognitoCreateChannelListener
import com.halo.common.utils.RecyclerViewUtils
import com.halo.common.utils.ktx.notNull
import com.halo.common.utils.ktx.viewModels
import com.halo.common.utils.list.ListUtils
import com.halo.data.entities.channel.ChannelBody
import com.halo.data.worker.channel.CreateChannelWorker
import com.halo.di.ActivityScoped
import com.halo.widget.HaloLinearLayoutManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

/**
 * Create by ndn
 * Create on 5/26/21
 * com.hahalolo.incognito.presentation.main
 */
@ActivityScoped
class IncognitoCreateChannelAct
@Inject constructor() : AbsIncBackActivity(), IncognitoCreateChannelListener {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    var binding by notNull<IncognitoCreateGroupActBinding>()
    val viewModel: IncognitoCreateChannelViewModel by viewModels { factory }
    private var adapter: IncognitoCreateChannelAdapter? = null

    private val requestManager by lazy { Glide.with(this) }

    override fun initActionBar() {
        setSupportActionBar(binding.toolbar)
    }

    override fun isShowTitleToolbar(): Boolean {
        return false
    }

    override fun initializeBindingViewModel() {
        binding = DataBindingUtil.setContentView(this, R.layout.incognito_create_group_act)
    }

    override fun initializeLayout() {
        initRec()
        initView()
        initObserver()
    }

    private fun initView() {
        binding.apply {
            createBt.setOnClickListener { handleCreateChannel() }
            cancelBt.setOnClickListener { finish() }
        }
    }

    private fun handleCreateChannel() {
        viewModel.listId.takeIf { it.size >= 0 }?.run {
            val createBottomSheet = BottomSheetCreateChannel(
                context = this@IncognitoCreateChannelAct,
                requestManager = requestManager,
                listener = object : CreateChannelListener {
                    override fun onPickAvatar() {

                    }

                    override fun onCreateGroup(name: String, avatar: String) {
                        onCreateGroupChat(name, avatar)
                    }
                })
            createBottomSheet.show(supportFragmentManager, "IncognitoCreateChannelAct")
        } ?: kotlin.run {
            Toast.makeText(
                this@IncognitoCreateChannelAct,
                "Vui lòng chọn thành viên nhóm",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun onCreateGroupChat(name: String,  avatar: String) {
        viewModel.apply {
            val body = ChannelBody(
                name = name,
                avatar = avatar,
                userIds = listId
            )
            lifecycleScope.launch {
                val idWorker = createChannel(this@IncognitoCreateChannelAct, body)
                WorkManager.getInstance(this@IncognitoCreateChannelAct).getWorkInfoByIdLiveData(idWorker)
                    .observe(this@IncognitoCreateChannelAct, { workInfo ->
                        workInfo?.let {
                            when (it.state) {
                                WorkInfo.State.RUNNING -> {
                                    showIndicator()
                                }
                                WorkInfo.State.SUCCEEDED -> {
                                    dismissIndicator()
                                    finish()
                                    it.outputData.getString(CreateChannelWorker.CHANNEL_ID_RESULT)
                                        ?.let { channelId ->
                                            //navigateChannel(channelId)
                                        }
                                }
                                WorkInfo.State.FAILED -> {
                                    dismissIndicator()
                                }
                                else -> {
                                    dismissIndicator()
                                }
                            }
                        }
                    })
            }
        }
    }

    private fun initObserver() {
        lifecycleScope.launch {
            viewModel.apply {
                listContact().collect {
                    adapter?.submitData(it)
                }
            }
        }
    }

    private fun initRec() {
        binding.apply {
            userRec.layoutManager = HaloLinearLayoutManager(this@IncognitoCreateChannelAct)
            RecyclerViewUtils.optimization(userRec, this@IncognitoCreateChannelAct)
            adapter = IncognitoCreateChannelAdapter(this@IncognitoCreateChannelAct, requestManager)
            userRec.adapter = adapter?.withLoadStateFooter(
                footer = IncognitoLoadStateAdapter(
                    object : IncognitoLoadStateCallback {
                        override fun retry() {
                            adapter?.retry()
                        }
                    })
            )
        }
    }

    override fun onSearch(query: String) {

    }

    override fun onSelect(targetId: String) {
        viewModel.apply {
            onSelected(targetId)
            updateCountSelected(listId.size)
        }
    }

    override fun onUnSelect(targetId: String) {
        viewModel.apply {
            onUnSelect(targetId)
            updateCountSelected(listId.size)
        }
    }

    override fun isSelected(targetId: String): Boolean {
        return ListUtils.isDataExits(viewModel.listId) { input ->
            TextUtils.equals(
                input,
                targetId
            )
        }
    }

    private fun updateCountSelected(count: Int) {
        binding.countTv.text = "(Đã chọn $count)"
        binding.countTv.visibility = if (count == 0) View.GONE else View.VISIBLE
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, IncognitoCreateChannelAct::class.java)
        }
    }
}