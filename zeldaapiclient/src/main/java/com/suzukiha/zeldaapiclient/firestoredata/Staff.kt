package com.suzukiha.zeldaapiclient.firestoredata

data class Staff(
    val id: Long,
    val name: Language,
    /** [com.suzukiha.zeldaapiclient.firestoredata.Game] */
    val workedOnGameId: ArrayList<Int>
)
