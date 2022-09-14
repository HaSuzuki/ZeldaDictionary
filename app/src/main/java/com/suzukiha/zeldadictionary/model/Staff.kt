package com.suzukiha.zeldadictionary.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

typealias StaffId = Int

@Parcelize
data class Staff(
    val id: StaffId,
    val name: String?,
    val workedOnGameId: ArrayList<Int>
) : Parcelable