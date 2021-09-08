package com.halo.data.room.type

import androidx.annotation.StringDef
import com.halo.data.room.type.DocumentType.Companion.DOC
import com.halo.data.room.type.DocumentType.Companion.EXCEl
import com.halo.data.room.type.DocumentType.Companion.OTHER
import com.halo.data.room.type.DocumentType.Companion.PDF
import com.halo.data.room.type.DocumentType.Companion.POWERPOINT

@StringDef(DOC, PDF, EXCEl, POWERPOINT, OTHER)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class DocumentType {
    companion object {
        const val DOC = ".docx"
        const val PDF = ".pdf"
        const val EXCEl = ".slsx"
        const val POWERPOINT = ".pptx"
        const val OTHER = ".other"
    }
}