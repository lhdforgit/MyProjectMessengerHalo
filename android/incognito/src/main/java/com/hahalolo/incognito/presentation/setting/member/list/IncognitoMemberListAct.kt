package com.hahalolo.incognito.presentation.setting.member.list

import android.content.Context
import android.content.Intent
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoMemberListActBinding
import com.hahalolo.incognito.presentation.base.AbsIncBackActivity
import com.hahalolo.incognito.presentation.setting.member.list.adapter.BottomSheetMenuMember
import com.hahalolo.incognito.presentation.setting.member.list.adapter.IncognitoMemberAdapter
import com.hahalolo.incognito.presentation.setting.member.list.adapter.IncognitoMemberListener
import com.hahalolo.incognito.presentation.setting.model.MemberModel
import com.hahalolo.incognito.presentation.setting.model.MemberUtil
import com.halo.common.utils.RecyclerViewUtils
import com.halo.common.utils.ktx.notNull
import com.halo.widget.HaloLinearLayoutManager

class IncognitoMemberListAct : AbsIncBackActivity() {

    var binding by notNull<IncognitoMemberListActBinding>()
    private val requestManager: RequestManager by lazy { Glide.with(this@IncognitoMemberListAct) }

    override fun initActionBar() {
        setSupportActionBar(binding.toolbar)
    }

    override fun isShowTitleToolbar(): Boolean {
        return false
    }

    override fun initializeBindingViewModel() {
        binding = DataBindingUtil.setContentView(
            this@IncognitoMemberListAct,
            R.layout.incognito_member_list_act
        )
    }

    override fun initializeLayout() {
        initRec()
    }

    private fun initRec() {
        binding.memberRec.layoutManager = HaloLinearLayoutManager(this@IncognitoMemberListAct)
        RecyclerViewUtils.optimization(binding.memberRec, this@IncognitoMemberListAct)
        val adapter = IncognitoMemberAdapter(requestManager, object : IncognitoMemberListener {
            override fun onItemClick() {
                showMemberMenu()
            }
        })
        binding.memberRec.adapter = adapter
        MemberModel.createListTest().let { listMember ->
            MemberUtil.getListMemberWithHeader(listMember).let {
                adapter.updateData(it)
            }
        }
    }

    private fun showMemberMenu() {
        val menu = BottomSheetMenuMember(this@IncognitoMemberListAct)
        menu.show(supportFragmentManager, "IncognitoMemberListAct")
    }

    companion object {
        @JvmStatic
        fun getIntent(context: Context): Intent {
            return Intent(context, IncognitoMemberListAct::class.java)
        }
    }
}