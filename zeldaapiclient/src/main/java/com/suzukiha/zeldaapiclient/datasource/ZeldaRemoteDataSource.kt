package com.suzukiha.zeldaapiclient.datasource

import com.suzukiha.zeldaapiclient.retrofit.ZeldaFunctions

class ZeldaRemoteDataSource(
    private val zeldaFunctions: ZeldaFunctions
) {

    // from retrofit(zelda api)
    val gamesResponse = zeldaFunctions.gamesResponse
    val charactersResponse = zeldaFunctions.charactersResponse
    val bossesResponse = zeldaFunctions.bossesResponse

    suspend fun fetchGames(
        page: String,
        limit: String
    ) = zeldaFunctions.fetchGames(
        page = page,
        limit = limit
    )

    suspend fun fetchGames(
        name: String,
        page: String,
        limit: String
    ) = zeldaFunctions.fetchGames(
        name = name,
        page = page,
        limit = limit
    )

    suspend fun fetchCharacters(
        page: String,
        limit: String
    ) = zeldaFunctions.fetchCharacters(
        page = page,
        limit = limit
    )

    suspend fun fetchCharacters(
        name: String,
        page: String,
        limit: String
    ) = zeldaFunctions.fetchCharacters(
        name = name,
        page = page,
        limit = limit
    )

    suspend fun fetchBosses(
        page: String,
        limit: String
    ) = zeldaFunctions.fetchBosses(
        page = page,
        limit = limit
    )

    suspend fun fetchBosses(
        name: String,
        page: String,
        limit: String
    ) = zeldaFunctions.fetchBosses(
        name = name,
        page = page,
        limit = limit
    )

    companion object {
        @Volatile
        private var INSTANCE: ZeldaRemoteDataSource? = null

        fun getInstance(
            callableFunctions: ZeldaFunctions
        ): ZeldaRemoteDataSource =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: ZeldaRemoteDataSource(callableFunctions).also {
                    INSTANCE = it
                }
            }
    }
}