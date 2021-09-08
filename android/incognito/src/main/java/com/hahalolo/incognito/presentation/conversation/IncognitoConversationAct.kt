package com.hahalolo.incognito.presentation.conversation

import android.content.Context
import android.content.Intent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoConversationActBinding
import com.hahalolo.incognito.presentation.base.AbsIncActivity
import com.hahalolo.incognito.presentation.conversation.adapter.IncognitoConversationAdapter
import com.hahalolo.incognito.presentation.conversation.adapter.IncognitoConversationListener
import com.hahalolo.incognito.presentation.conversation.adapter.model.IncognitoMsgModel
import com.hahalolo.incognito.presentation.conversation.dialog.IncognitoMsgDetailDialog
import com.hahalolo.incognito.presentation.conversation.dialog.IncognitoMsgDetailListener
import com.hahalolo.incognito.presentation.conversation.view.IncognitoMessageInputListener
import com.hahalolo.incognito.presentation.create.forward.IncognitoForwardMessageBottomSheet
import com.hahalolo.incognito.presentation.setting.channel.IncognitoChannelSettingAct
import com.halo.common.utils.ktx.notNull
import com.halo.common.utils.ktx.viewModels
import com.halo.di.ActivityScoped
import com.halo.widget.repository.sticker.StickerRepository
import com.halo.widget.sticky_header.StickyRecyclerHeadersDecoration
import javax.inject.Inject

/**
 * Create by ndn
 * Create on 5/26/21
 * com.hahalolo.incognito.presentation.main
 */
@ActivityScoped
class IncognitoConversationAct
@Inject constructor() : AbsIncActivity() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    @Inject
    lateinit var stickerRepository: StickerRepository

    var binding by notNull<IncognitoConversationActBinding>()
    val viewModel: IncognitoConversationViewModel by viewModels { factory }

    override fun initializeBindingViewModel() {
        binding = DataBindingUtil.setContentView(this, R.layout.incognito_conversation_act)
    }

    private var adapter: IncognitoConversationAdapter? = null

    override fun initializeLayout() {
        initActions()
        initMessageInput()
        initRecycleView()
    }

    /*INIT LAYOUT*/

    private fun initActions() {
        binding.btnInfo.setOnClickListener {
            navigateConversationInfo()
        }
        binding.btnCall.setOnClickListener {
            val forward = IncognitoForwardMessageBottomSheet()
            forward.show(supportFragmentManager, "IncognitoConversationAct")
        }
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun initMessageInput() {
        binding.messageInput.updateInputListener(object : IncognitoMessageInputListener {
            override fun lifecycleOwner(): LifecycleOwner {
                return this@IncognitoConversationAct
            }

            override fun onSendMessageText(textSend: String): Boolean {

                return true
            }

            override fun stickerRepository(): StickerRepository {
                return stickerRepository
            }
        })
    }

    private fun initRecycleView() {
        adapter = IncognitoConversationAdapter(object : IncognitoConversationListener {
            override fun getLanguageCode(): String {
                return "vi"
            }

            override fun onClickMessage(incognitoMsgModel: IncognitoMsgModel) {
                showMessageDetail(incognitoMsgModel)

            }
        })
        binding.listItem.adapter = adapter
        /*Header date*/
        val headersDecor = StickyRecyclerHeadersDecoration(adapter)
        binding.listItem.addItemDecoration(headersDecor)

        binding.listItem.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL, true
        )
    }

    private fun showMessageDetail(incognitoMsgModel: IncognitoMsgModel) {
        IncognitoMsgDetailDialog(object: IncognitoMsgDetailListener {
            override fun onClickReplyMessage() {

            }

            override fun onClickCopyMessage() {

            }

            override fun onClickForwardMessage() {

            }

            override fun onClickEditMessage() {

            }

            override fun onClickSaveMessage() {

            }

            override fun onClickTickMessage() {

            }

            override fun onClickDeleteMessage() {

            }

            override fun onClickCancelMessage() {

            }
        }).show()
    }
    /*INIT LAYOUT END*/

    /*Action*/

    override fun onBackPressed() {
        if (!binding.messageInput.isBackPressed()) {
            super.onBackPressed()
        }
    }

    /*Action End*/

    /*Navigate */

    private fun navigateConversationInfo() {
        startActivity(IncognitoChannelSettingAct.getIntent(this@IncognitoConversationAct))
    }

    /*Navigate Info*/

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, IncognitoConversationAct::class.java)
        }
    }
}