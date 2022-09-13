package com.suzukiha.zeldaapiclient.retrofit

import com.suzukiha.zeldaapiclient.apidata.Boss
import com.suzukiha.zeldaapiclient.apidata.Character
import com.suzukiha.zeldaapiclient.apidata.Game
import kotlinx.coroutines.flow.StateFlow

interface ZeldaFunctions {

    val gamesResponse: StateFlow<GamesState>

    val charactersResponse: StateFlow<CharactersState>

    val bossesResponse: StateFlow<BossesState>


    suspend fun fetchGames(
        page: String,
        limit: String
    )

    suspend fun fetchGames(
        name: String,
        page: String,
        limit: String
    )

    suspend fun fetchCharacters(
        page: String,
        limit: String
    )

    suspend fun fetchCharacters(
        name: String,
        page: String,
        limit: String
    )

    suspend fun fetchBosses(
        page: String,
        limit: String
    )

    suspend fun fetchBosses(
        name: String,
        page: String,
        limit: String
    )

    sealed class GamesState {
        object Loading : GamesState()

        data class Success(val game: Game, val currentPage: String) : GamesState()

        data class Error(val message: String? = null) : GamesState()
    }

    sealed class CharactersState {
        object Loading : CharactersState()

        data class Success(val character: Character) : CharactersState()

        data class Error(val message: String? = null) : CharactersState()
    }

    sealed class BossesState {
        object Loading : BossesState()

        data class Success(val boss: Boss) : BossesState()

        data class Error(val message: String? = null) : BossesState()
    }
}