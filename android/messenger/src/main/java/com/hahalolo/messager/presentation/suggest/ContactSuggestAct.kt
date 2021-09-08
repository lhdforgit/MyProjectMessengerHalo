/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.presentation.suggest

import android.content.Context
import android.content.Intent
import androidx.databinding.DataBindingUtil
import com.hahalolo.messager.presentation.base.AbsMessActivity
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatContactSuggestActBinding
import com.halo.common.utils.ktx.addFragment
import com.halo.common.utils.ktx.notNull
import dagger.Lazy
import javax.inject.Inject

fun Context.launcherContactSuggest() {
    startActivity(Intent(this, ContactSuggestAct::class.java))
}

@com.halo.di.ActivityScoped
class ContactSuggestAct : AbsMessActivity() {

    var binding by notNull<ChatContactSuggestActBinding>()

    @Inject
    lateinit var fr: Lazy<ContactsSuggestFr>

    override fun initializeBindingViewModel() {
        binding = DataBindingUtil.setContentView(this, R.layout.chat_contact_suggest_act)
    }

    override fun initializeLayout() {
        addFragment(fr.get(), R.id.container)
    }
}