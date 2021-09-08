package com.halo.data.room

import java.text.Normalizer
import java.util.regex.Pattern

object StringUtils {
    fun deAccent(str: String?): String? {
        try {
            return str?.run {
                val nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD)
                val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
                pattern.matcher(nfdNormalizedString).replaceAll("")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}