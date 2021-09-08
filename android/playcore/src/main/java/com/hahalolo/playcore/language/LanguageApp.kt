/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.playcore.language

import android.content.Context
import android.text.TextUtils
import com.hahalolo.playcore.split.SharePreferenceImp
import com.halo.common.utils.DateUtils
import java.util.*

object LanguageApp {

    const val VIETNAMESE = "vi"
    const val ENGLISH = "en"

    fun currentLocaleCode(): String {
        return SharePreferenceImp.getCurrLanguageApp()
    }

    fun switchLanguage(lang: String) {
        SharePreferenceImp.setNewLanguageApp(lang)
    }

    fun currentLanguageCode(context: Context?): String {
        var code = VIETNAMESE
        context?.apply {
            code = SharePreferenceImp.getCurrLanguageApp()
        }
        return code
    }

    fun isCurrentVietnameseLanguage(context: Context?): Boolean {
        context?.applicationContext?.apply {
            val currentLanguage = SharePreferenceImp.getCurrLanguageApp()
            return TextUtils.equals(currentLanguage, VIETNAMESE)
        }
        return true
    }

    /** get current language in app
     *
     *if null => language default of device
     * if not found => vietnamese
     * */

    fun getCodeLanguageOfDevice(): String? {
        return Locale.getDefault().language
    }

    fun updateLocaled() {
        Locale.setDefault(Locale.forLanguageTag(currentLocaleCode()))
    }

    fun init(context: Context) {
        val codeDevice = getCodeLanguageOfDevice()
        SharePreferenceImp.init(context)
        if (TextUtils.isEmpty(SharePreferenceImp.getCurrLanguageApp())) {
            if (codeDevice.isNullOrEmpty()) {
                SharePreferenceImp.setNewLanguageApp(VIETNAMESE)
            } else {
                SharePreferenceImp.setNewLanguageApp(getCodeLanguageOfDevice())
            }
        }
    }
}