package com.hahalolo.incognito.presentation.setting.invite

import android.content.Context
import android.content.Intent
import androidx.databinding.DataBindingUtil
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoInviteFriendActBinding
import com.hahalolo.incognito.presentation.base.AbsIncBackActivity
import com.hahalolo.incognito.presentation.setting.invite.channel.IncognitoInviteChannelFr
import com.hahalolo.incognito.presentation.setting.invite.workspace.IncognitoInviteWorkspaceFr
import com.halo.common.utils.ktx.notNull
import dagger.Lazy
import javax.inject.Inject

class IncognitoInviteFriendAct : AbsIncBackActivity() {

    @Inject
    lateinit var inviteChannel: Lazy<IncognitoInviteChannelFr>

    @Inject
    lateinit var inviteWorkspace: Lazy<IncognitoInviteWorkspaceFr>

    private var binding by notNull<IncognitoInviteFriendActBinding>()

    override fun initActionBar() {

    }

    override fun initializeBindingViewModel() {
        binding = DataBindingUtil.setContentView(
            this@IncognitoInviteFriendAct,
            R.layout.incognito_invite_friend_act
        )
    }

    override fun initializeLayout() {
        kotlin.runCatching {
            val transition = supportFragmentManager.beginTransaction()
            transition.add(R.id.invite_container, inviteChannel.get())
            transition.commit()
        }
    }

    companion object {
        @JvmStatic
        fun getIntent(context: Context): Intent {
            return Intent(context, IncognitoInviteFriendAct::class.java)
        }
    }
}