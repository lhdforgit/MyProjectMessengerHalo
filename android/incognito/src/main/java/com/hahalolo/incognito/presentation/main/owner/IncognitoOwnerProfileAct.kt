package com.hahalolo.incognito.presentation.main.owner

import android.content.Context
import android.content.Intent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoOwnerProfileActBinding
import com.hahalolo.incognito.presentation.base.AbsIncActivity
import com.hahalolo.incognito.presentation.main.owner.message.mailbox.IncognitoOwnerMailBoxAct
import com.hahalolo.incognito.presentation.main.owner.message.waiting.IncognitoOwnerMessageWaitingAct
import com.hahalolo.incognito.presentation.main.owner.update.IncognitoOwnerUpdateProfileAct
import com.hahalolo.incognito.presentation.main.owner.update.dialog.IncognitoDisplayInterface
import com.hahalolo.incognito.presentation.main.owner.update.dialog.IncognitoDisplayView
import com.hahalolo.incognito.presentation.main.owner.update.dialog.IncognitoStatusInterface
import com.hahalolo.incognito.presentation.main.owner.update.dialog.IncognitoStatusView
import com.hahalolo.incognito.presentation.setting.general.IncognitoSettingGeneralAct
import com.halo.common.utils.ktx.notNull
import com.halo.common.utils.ktx.viewModels
import com.halo.di.ActivityScoped
import com.halo.widget.dialog.HaloDialogCustom
import javax.inject.Inject

@ActivityScoped
class IncognitoOwnerProfileAct @Inject constructor(): AbsIncActivity(){

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    var binding by notNull<IncognitoOwnerProfileActBinding>()
    val viewModel: IncognitoOwnerProfileViewModel by viewModels { factory }

    override fun initializeBindingViewModel() {
        binding = DataBindingUtil.setContentView(this, R.layout.incognito_owner_profile_act)
    }

    override fun initializeLayout() {
        initActions()
    }

    private fun initActions(){
        binding.apply {
            toolbar.setOnClickListener {
                finish()
            }
            updateInfo.setOnClickListener {
                navigateUpdateProfile()
            }
            messageWaiting.setOnClickListener {
                navigateMessageWaiting()
            }
            mailBox.setOnClickListener {
                navigateMessageArchive()
            }
            display.setOnClickListener {
                val menuDisplay = IncognitoDisplayView(this@IncognitoOwnerProfileAct,
                    object: IncognitoDisplayInterface {
                        override fun onClickshining() {

                        }

                        override fun onClickDark() {

                        }

                    })
                menuDisplay.show()
            }
            status.setOnClickListener {
                val menuStatus = IncognitoStatusView(this@IncognitoOwnerProfileAct,
                    object : IncognitoStatusInterface {
                        override fun onClickLive() {

                        }

                        override fun onClickAbsent() {

                        }

                        override fun onClickBusy() {

                        }
                    })
                menuStatus.show()
            }
            setting.setOnClickListener {
                navigateGeneralSetting()
            }
            logOut.setOnClickListener {
                HaloDialogCustom.Builder().apply {
                    setTitle("Đăng xuất")
                    setDescription("Bạn có chắc chắn muốn đăng xuất tài khoản khỏi ứng dụng?")
                    setTextPrimary("Đồng ý")
                    setTextCancel("Hủy")
                    setOnClickCancel {
                    }
                    setOnClickPrimary {
                        appController.startLogin()
                    }.build().apply {
                        show(supportFragmentManager, "ChatAct")
                    }
                }
            }
        }
    }

    private fun navigateUpdateProfile() {
        startActivity(IncognitoOwnerUpdateProfileAct.getIntent(this))
    }

    private fun navigateGeneralSetting() {
        startActivity(IncognitoSettingGeneralAct.getIntent(this))
    }

    private fun navigateMessageWaiting() {
        startActivity(IncognitoOwnerMessageWaitingAct.getIntent(this))
    }

    private fun navigateMessageArchive() {
        startActivity(IncognitoOwnerMailBoxAct.getIntent(this))
    }

    companion object {
        @JvmStatic
        fun getIntent(context: Context): Intent {
            return Intent(context, IncognitoOwnerProfileAct::class.java)
        }
    }
}