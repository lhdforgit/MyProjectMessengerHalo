package com.halo.presentation.setting

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.hahalolo.messager.bubble.BubbleService
import com.halo.R
import com.halo.common.utils.ktx.notNull
import com.halo.databinding.SettingActBinding
import com.halo.presentation.base.AbsActivity
import com.halo.widget.dialog.HaloDialogCustom
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import javax.inject.Inject

/**
 * Create by ndn
 * Create on 12/17/20
 * com.halo.presentation.setting
 */
class SettingAct : AbsActivity() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    var binding by notNull<SettingActBinding>()
    val viewModel: SettingViewModel by viewModels { factory }

    override fun initializeBindingViewModel() {
        super.initializeBindingViewModel()
        binding = DataBindingUtil.setContentView(this, R.layout.setting_act)
    }

    override fun initializeLayout() {
        super.initializeLayout()
        binding.apply {
            toolbar.setNavigationOnClickListener {
                finish()
            }
            initSoundVideo()
            initBubbleChat()
            initBubbleChatAction()
        }
    }

    /*SOUND VIDEO*/
    private fun SettingActBinding.initSoundVideo() {
        soundVideo.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setVideoSound(isChecked)
        }
        soundVideo.isChecked = viewModel.isVideoSound
    }
    /*SOUND VIDEO END*/

    /*BUBBLE SETTING*/
    private fun initBubbleChat() {
        //Check điều kiện và hiển thị
        if (viewModel.isOpenBubbleChat && !isHavePermission(this@SettingAct)) {
            // nếu enable bubble chat và chưa có quyền -> disable bubble chat
            viewModel.setBubbleChat(false)
        }
        handleBubbleSetting()
    }

    private fun SettingActBinding.initBubbleChatAction() {
        bubbleChat.setOnCheckedChangeListener { _, isChecked ->
            viewModel.apply {
                // When permission == false user click open setting
                if (isChecked) {
                    if (isHavePermission(this@SettingAct)) {
                        // nếu enable Bubble và có quyền Popup
                        viewModel.setBubbleChat(true)
                    } else {
                        // nếu chưa có quyền Popup
                        showAlertDialogMessage(title = getString(R.string.chat_message_setting_permission_title),
                            content = getString(R.string.chat_message_setting_permission_des),
                            cancelable = false,
                            listener = {
                                if (!navigateXiaomiBubblePermission()) {
                                    if (navigateBubblePermission()) {
                                        // gửi yêu cầu xin quyền thành công, chờ phản hồi
                                    } else {
                                        // gửi yêu cầu xin quyền thất bại
                                        viewModel.setBubbleChat(true)
                                    }
                                }
                            },
                            primaryText = getString(R.string.chat_message_bubble_permission_primary_text)
                        )
                    }
                } else {
                    viewModel.setBubbleChat(false)
                }
            }
        }
    }

    private fun handleBubbleSetting() {
        // update layout khi bubble setting changed
        binding.bubbleChat.isChecked = viewModel.isOpenBubbleChat
    }

    private fun enableBubbleSetting(enable: Boolean) {
        binding.bubbleChat.alpha = if (enable) 1f else 0.5f
        binding.bubbleChat.isEnabled = enable
    }
    /*BUBBLE SETTING END*/

    /*NAVIGATE */
    private fun navigateBubblePermission(): Boolean {
        return enablePermission(this@SettingAct, REQUEST_OVERLAY)
    }

    private fun navigateXiaomiBubblePermission(): Boolean {
        return goToXiaomiPermissions(this@SettingAct, REQUEST_XIAOMI_OVERLAY)
    }
    /*NAVIGATE END */

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_OVERLAY -> {
                //Trường hợp này permission bị delay 1000ms sau khi thay đổi
                lifecycleScope.launch {
                    enableBubbleSetting(false)
                    delay(1000)
                    enableBubbleSetting(true)
                    if (isHavePermission(this@SettingAct)) {
                        // đã có permission ->  update layout
                        viewModel.setBubbleChat(true)
                        handleBubbleSetting()
                    } else {
                        // chưa có permission -> hiện thông cáo cần quyền ms sử dụng đc
                        showAlertDialogMessage(
                            title =  getString(R.string.chat_message_setting_permission_title),
                            content = getString(R.string.chat_message_setting_permission_dismiss_des),
                            cancelable = true,
                            listener = {

                            },
                            primaryText = getString(R.string.ok)
                        )
                        viewModel.setBubbleChat(false)
                        handleBubbleSetting()
                    }
                }
            }
            REQUEST_XIAOMI_OVERLAY -> {
                if (isHavePermission(this@SettingAct)) {
                    // đã có permission -> update enable bubble setting
                    viewModel.setBubbleChat(true)
                    handleBubbleSetting()
                } else {
                    // chưa có permission -> start trực tiếp đến màn yêu cầu Popup windown
                    showAlertDialogMessage(title = getString(R.string.chat_message_setting_permission_title),
                        content = getString(R.string.chat_message_setting_permission_des),
                        cancelable = false,
                        listener = {
                            navigateBubblePermission()
                        },
                        primaryText = getString(R.string.ok)
                    )
                }
            }
        }
    }

    private fun isMiUi(): Boolean {
        return getSystemProperty("ro.miui.ui.version.name")?.isNotBlank() == true
    }

    private fun isMiuiWithApi28OrMore(): Boolean {
        return isMiUi() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    private fun getSystemProperty(propName: String): String? {
        val line: String
        var input: BufferedReader? = null
        try {
            val p = Runtime.getRuntime().exec("getprop $propName")
            input = BufferedReader(InputStreamReader(p.inputStream), 1024)
            line = input.readLine()
            input.close()
        } catch (ex: IOException) {
            return null
        } finally {
            if (input != null) {
                try {
                    input.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return line
    }

    private fun isHavePermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
            return false
        }
        return true
    }

    private fun goToXiaomiPermissions(activity: Activity, requestCode :Int ): Boolean {
        if (isMiuiWithApi28OrMore()) {
            try {
                val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
                intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity")
                intent.putExtra("extra_pkgname", activity.packageName)
                activity.startActivityForResult(intent, requestCode)
                return true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return false
    }

    fun enablePermission(activity: Activity, REQUEST_CODE: Int): Boolean {
        if (!BubbleService.ENABLE_BUBBLE) return false
        if (!isHavePermission(activity)) {
//                context.startActivity(Intent(context, BubbleAct::class.java))
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${activity.packageName}"))
            try {
                activity.startActivityForResult(intent, REQUEST_CODE)
                return true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return false
    }

    private fun showAlertDialogMessage(
        title: String = "",
        content: String = "",
        listener: View.OnClickListener? = null,
        primaryText: String = "",
        cancelable: Boolean = true
    ) {
        HaloDialogCustom.Builder().apply {
            setTitle(title)
            setDescription(content)
            setTextPrimary(primaryText)
            setOnClickCancel(null)
            setOnClickPrimary(listener)
        }.build().apply {
            isCancelable = cancelable
        }.show(supportFragmentManager, "SettingAct")
    }

    companion object {
        private const val REQUEST_OVERLAY = 111
        private const val REQUEST_XIAOMI_OVERLAY = 112

        fun launchSetting(context: Context) {
            context.startActivity(Intent(context, SettingAct::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
    }

    override fun onResume() {
        super.onResume()
        initBubbleChat()
    }
}