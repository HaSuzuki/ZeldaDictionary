package com.suzukiha.zeldaapiclient.apidata

import com.google.gson.annotations.SerializedName

data class Character(
    @SerializedName("success")
    var isSuccess: Boolean,
    var count: Int,
    var data: ArrayList<CharacterData>
)

data class CharacterData(
    var appearances: String?,
    var name: String?,
    var description: String?,
    var gender: String?,
    var race: String?,
    var id: String?
)
