package com.suzukiha.zeldaapiclient.firestoredata

data class Game(
    val id: Long,
    val name: Language,
    val description: Language,
    val thumbnailUrl: String?,
    val releaseDate: String?
)
