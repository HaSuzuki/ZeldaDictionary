package com.suzukiha.zeldadictionary.model

typealias GameId = Int

data class Game(
    val id: GameId,
    val name: String?,
    val thumbnailUrl: String?,
    val description: String?,
    val releaseDate: String?
)