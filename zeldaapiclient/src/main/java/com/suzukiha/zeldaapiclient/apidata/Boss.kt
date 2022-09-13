package com.suzukiha.zeldaapiclient.apidata

import com.google.gson.annotations.SerializedName

data class Boss(
    @SerializedName("success")
    var isSuccess: Boolean,
    var count: Int,
    var data: ArrayList<BossData>
)

data class BossData(
    var appearances: String?,
    var dungeons: String?,
    var name: String?,
    var description: String?,
    var id: String?
)