package com.suzukiha.zeldaapiclient.apidata

import com.google.gson.annotations.SerializedName

data class Game(
    @SerializedName("success")
    var isSuccess: Boolean,
    var count: Int,
    var data: ArrayList<GameData>?
)

data class GameData(
    var name: String?,
    var description: String?,
    var developer: String?,
    var publisher: String?,
    @SerializedName("released_date")
    var releasedDate: String?,
    var id: String?
)
