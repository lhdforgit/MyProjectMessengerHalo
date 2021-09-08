package com.hahalolo.incognito.presentation.setting.managerfile.file

import androidx.annotation.IntDef
import com.hahalolo.incognito.presentation.setting.managerfile.file.IncognitoFileType.Companion.DOC
import com.hahalolo.incognito.presentation.setting.managerfile.file.IncognitoFileType.Companion.PDF
import com.hahalolo.incognito.presentation.setting.managerfile.file.IncognitoFileType.Companion.XLS
@IntDef(PDF, DOC, XLS)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class IncognitoFileType{
    companion object {
        const val PDF = 1
        const val DOC = 2
        const val XLS = 3
    }
}

