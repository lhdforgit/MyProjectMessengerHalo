package com.hahalolo.incognito.presentation.main.owner.update.dialog

import androidx.annotation.IntDef
import com.hahalolo.incognito.presentation.main.owner.update.dialog.IncognitoUpdateType.Companion.EDIT_ADDRESS
import com.hahalolo.incognito.presentation.main.owner.update.dialog.IncognitoUpdateType.Companion.EDIT_EMAIL
import com.hahalolo.incognito.presentation.main.owner.update.dialog.IncognitoUpdateType.Companion.EDIT_NAME
import com.hahalolo.incognito.presentation.main.owner.update.dialog.IncognitoUpdateType.Companion.EDIT_PHONE

@IntDef(EDIT_NAME, EDIT_PHONE, EDIT_EMAIL, EDIT_ADDRESS)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class IncognitoUpdateType {
    companion object {
        const val EDIT_NAME = 1
        const val EDIT_PHONE = 2
        const val EDIT_EMAIL = 3
        const val EDIT_ADDRESS = 4
    }
}
