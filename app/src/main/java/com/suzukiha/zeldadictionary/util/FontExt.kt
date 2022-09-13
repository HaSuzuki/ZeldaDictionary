package com.suzukiha.zeldadictionary.util

import android.content.Context
import android.graphics.Typeface

fun getHyliaSerifFont(context: Context): Typeface {
    return Typeface.createFromAsset(context.assets, "HyliaSerifBeta-Regular.otf")
}