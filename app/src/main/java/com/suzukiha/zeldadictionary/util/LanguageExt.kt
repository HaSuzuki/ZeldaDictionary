package com.suzukiha.zeldadictionary.util

import java.util.*

fun isEnglish(): Boolean {
    return Locale.getDefault().language == Locale.ENGLISH.language
}
