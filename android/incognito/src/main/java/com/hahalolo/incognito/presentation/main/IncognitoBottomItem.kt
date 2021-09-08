package com.hahalolo.incognito.presentation.main

import androidx.annotation.IntDef
import com.hahalolo.incognito.presentation.main.IncognitoBottomItem.Companion.CONTACTS_ITEM
import com.hahalolo.incognito.presentation.main.IncognitoBottomItem.Companion.CONVERSATION_ITEM
import com.hahalolo.incognito.presentation.main.IncognitoBottomItem.Companion.GROUP_ITEM
import com.halo.fragnav.FragNavController

@Retention(AnnotationRetention.SOURCE)
@IntDef(CONVERSATION_ITEM, CONTACTS_ITEM, GROUP_ITEM)
annotation class IncognitoBottomItem {
    companion object {
        const val CONTACTS_ITEM = FragNavController.TAB1
        const val CONVERSATION_ITEM = FragNavController.TAB2
        const val GROUP_ITEM = FragNavController.TAB3
    }
}