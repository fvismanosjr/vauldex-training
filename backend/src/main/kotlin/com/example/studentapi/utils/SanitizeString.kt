package com.example.studentapi.utils

import org.jsoup.Jsoup
import org.jsoup.safety.Safelist

object SanitizeString {
    fun clean(input: String?): String {
        return input?.let { Jsoup.clean(it, Safelist.none()) } ?: ""
    }
}
