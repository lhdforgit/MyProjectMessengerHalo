package com.hahalolo.incognito.presentation.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoMainActBinding
import com.hahalolo.incognito.presentation.base.AbsIncActivity
import com.hahalolo.incognito.presentation.controller.IncognitoController
import com.hahalolo.incognito.presentation.create.channel.IncognitoCreateChannelAct
import com.hahalolo.incognito.presentation.create.conversation.IncognitoCreateConversationAct
import com.hahalolo.incognito.presentation.main.IncognitoBottomItem.Companion.CONTACTS_ITEM
import com.hahalolo.incognito.presentation.main.IncognitoBottomItem.Companion.CONVERSATION_ITEM
import com.hahalolo.incognito.presentation.main.IncognitoBottomItem.Companion.GROUP_ITEM
import com.hahalolo.incognito.presentation.main.contact.IncognitoContactFr
import com.hahalolo.incognito.presentation.main.conversation.IncognitoConversationFr
import com.hahalolo.incognito.presentation.main.group.IncognitoGroupFr
import com.hahalolo.incognito.presentation.main.owner.IncognitoOwnerProfileAct
import com.hahalolo.incognito.presentation.main.search.IncognitoSearchFr
import com.hahalolo.notification.worker.RegisterFcmTokenWorker
import com.hahalolo.qrcode.QRCodeReaderAct
import com.halo.common.utils.GlideRequestBuilder
import com.halo.common.utils.ktx.notNull
import com.halo.common.utils.ktx.setSafeOnClickListener
import com.halo.common.utils.ktx.viewModels
import com.halo.di.ActivityScoped
import com.halo.fragnav.FragNavController
import com.halo.fragnav.FragNavLogger
import com.halo.fragnav.FragNavSwitchController
import com.halo.fragnav.FragNavTransactionOptions
import com.halo.fragnav.tabhistory.UniqueTabHistoryStrategy
import dagger.Lazy
import javax.inject.Inject


/**
 * Create by ndn
 * Create on 5/26/21
 * com.hahalolo.incognito.presentation.main
 */
@ActivityScoped
class IncognitoMainAct
@Inject constructor() : AbsIncActivity(), FragNavController.RootFragmentListener {

    companion object {
        @JvmStatic
        fun launchIncognitoMainAct(context: Context) {
            val intent = Intent(context, IncognitoMainAct::class.java)
            context.startActivity(intent)
        }
    }

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    @Inject
    lateinit var controller: IncognitoController

    @Inject
    lateinit var providerConversationFr: Lazy<IncognitoConversationFr>

    @Inject
    lateinit var providerContactsFr: Lazy<IncognitoContactFr>

    @Inject
    lateinit var providerGroupFr: Lazy<IncognitoGroupFr>

    @Inject
    lateinit var providerSearchFr: Lazy<IncognitoSearchFr>

    var binding by notNull<IncognitoMainActBinding>()
    val viewModel: IncognitoMainViewModel by viewModels { factory }

    private var fragNavController: FragNavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBottomNavigation(savedInstanceState)

        val work = OneTimeWorkRequest.Builder(RegisterFcmTokenWorker::class.java).build()
        WorkManager.getInstance(this).beginWith(work).enqueue()
    }

    override fun initializeBindingViewModel() {
        binding = DataBindingUtil.setContentView(this, R.layout.incognito_main_act)
    }

    override fun initializeLayout() {
        initActions()
    }

    private fun initActions() {
        binding.apply {
            qrCode.setSafeOnClickListener {
                QRCodeReaderAct.navigateQrCodeReader(this@IncognitoMainAct)
            }
            addContact.setSafeOnClickListener {
                navigateAddContact()
            }
            createMess.setSafeOnClickListener {
                navigateCreateMessage()
            }
            createGroup.setSafeOnClickListener {
                navigateCreateGroup()
            }
            mainMenu.setSafeOnClickListener {
                bottomNavigation.selectedItemId = R.id.conversation_item
            }
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    // TODO: Search Group OR Contact
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    // TODO: Search Mess
                    return false
                }
            })
            GlideRequestBuilder.getCircleCropRequest(Glide.with(this@IncognitoMainAct))
                .load(controller.oauthInfo?.avatar)
                .error(R.drawable.community_avatar_holder)
                .placeholder(R.drawable.community_avatar_holder)
                .into(ownerAvatar)
            ownerAvatar.setSafeOnClickListener {
                navigateOwnerProfile()
            }
        }
    }


    private fun initBottomNavigation(savedInstanceState: Bundle?) {
        fragNavController = FragNavController(supportFragmentManager, R.id.container).apply {
            rootFragmentListener = this@IncognitoMainAct
            createEager = true
            fragNavLogger = object : FragNavLogger {
                override fun error(message: String, throwable: Throwable) {
                    throwable.printStackTrace()
                }
            }
            fragmentHideStrategy = FragNavController.DETACH_ON_NAVIGATE_HIDE_ON_SWITCH
            navigationStrategy = UniqueTabHistoryStrategy(object : FragNavSwitchController {
                override fun switchTab(index: Int, transactionOptions: FragNavTransactionOptions?) {
                    binding.bottomNavigation.apply {
                        selectedItemId = when (index) {
                            CONTACTS_ITEM -> R.id.contact_item
                            CONVERSATION_ITEM -> R.id.conversation_item
                            GROUP_ITEM -> R.id.group_item
                            else -> R.id.conversation_item
                        }
                    }
                }
            })
            initialize(CONVERSATION_ITEM, savedInstanceState)
            if (savedInstanceState == null) {
                binding.bottomNavigation.selectedItemId = R.id.conversation_item
                changeStateHeader(1)
            }
            executePendingTransactions()
            binding.bottomNavigation.apply {
                itemIconTintList = null
                setOnNavigationItemSelectedListener { item: MenuItem ->
                    switchFloatButton(item.itemId)
                    when (item.itemId) {
                        R.id.contact_item -> {
                            switchTab(CONTACTS_ITEM)
                            changeStateHeader(0)
                            return@setOnNavigationItemSelectedListener true
                        }
                        R.id.conversation_item -> {
                            switchTab(CONVERSATION_ITEM)
                            changeStateHeader(1)
                            return@setOnNavigationItemSelectedListener true
                        }
                        R.id.group_item -> {
                            switchTab(GROUP_ITEM)
                            changeStateHeader(2)
                            return@setOnNavigationItemSelectedListener true
                        }
                    }
                    false
                }
                setOnNavigationItemReselectedListener {
                    clearStack()
                }
            }
        }
    }

    private fun changeStateHeader(state: Int) {
        binding.apply {
            when (state) {
                0 -> {
                    contact = true
                    mess = false
                    group = false
                    titleTv.text = "Danh bạ"
                }
                1 -> {
                    contact = false
                    mess = true
                    group = false
                    titleTv.text = "Tin nhắn"
                }
                2 -> {
                    contact = false
                    mess = false
                    group = true
                    titleTv.text = "Nhóm"
                }
            }
        }
    }

    private fun switchFloatButton(itemId: Int) {
        when (itemId) {
            R.id.contact_item -> {

            }
            R.id.conversation_item -> {

            }
            R.id.group_item -> {

            }
        }
    }

    override val numberOfRootFragments: Int
        get() = 3

    override fun getRootFragment(index: Int): Fragment {
        when (index) {
            CONTACTS_ITEM -> return providerContactsFr.get()
            CONVERSATION_ITEM -> return providerConversationFr.get()
            GROUP_ITEM -> return providerGroupFr.get()
        }
        throw IllegalStateException("Need to send an index that we know: $index")
    }

    private fun navigateOwnerProfile() {
        startActivity(IncognitoOwnerProfileAct.getIntent(this))
    }


    private fun navigateCreateGroup() {
        startActivity(IncognitoCreateChannelAct.getIntent(this))
    }

    private fun navigateCreateMessage() {
        startActivity(IncognitoCreateConversationAct.getIntent(this))
    }

    private fun navigateAddContact(){

    }
}
