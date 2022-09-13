package com.suzukiha.zeldaapiclient.retrofit

import android.content.Context
import com.suzukiha.zeldaapiclient.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ZeldaFunctionsImpl(private val context: Context) : ZeldaFunctions {

    private val retrofitClient =
        RetrofitClient(context.getString(R.string.zelda_api_base_url), ZeldaApiCall::class.java)

    private val _responseGames =
        MutableStateFlow<ZeldaFunctions.GamesState>(ZeldaFunctions.GamesState.Loading)
    private val _responseCharacters =
        MutableStateFlow<ZeldaFunctions.CharactersState>(ZeldaFunctions.CharactersState.Loading)
    private val _responseBosses =
        MutableStateFlow<ZeldaFunctions.BossesState>(ZeldaFunctions.BossesState.Loading)

    override val gamesResponse: StateFlow<ZeldaFunctions.GamesState> =
        _responseGames.asStateFlow()
    override val charactersResponse: StateFlow<ZeldaFunctions.CharactersState> =
        _responseCharacters.asStateFlow()
    override val bossesResponse: StateFlow<ZeldaFunctions.BossesState> =
        _responseBosses.asStateFlow()

    override suspend fun fetchGames(
        page: String,
        limit: String
    ) {
        _responseGames.emit(ZeldaFunctions.GamesState.Loading)
        retrofitClient.getService().fetchGames(
            page = page,
            limit = limit
        ).let {
            if (!it.isSuccessful) {
                _responseGames.emit(ZeldaFunctions.GamesState.Error())
            }
            it.body()?.let { game ->
                _responseGames.emit(ZeldaFunctions.GamesState.Success(game = game, currentPage = page))
            } ?: run {
                _responseGames.emit(ZeldaFunctions.GamesState.Error(it.message()))
            }
        }
    }

    override suspend fun fetchGames(
        name: String,
        page: String,
        limit: String
    ) {
        _responseGames.emit(ZeldaFunctions.GamesState.Loading)
        retrofitClient.getService().fetchGames(
            name = name,
            page = page,
            limit = limit
        ).let {
            if (!it.isSuccessful) {
                _responseGames.emit(ZeldaFunctions.GamesState.Error())
            }
            it.body()?.let { game ->
                _responseGames.emit(ZeldaFunctions.GamesState.Success(game = game, currentPage = page))
            } ?: run {
                _responseGames.emit(ZeldaFunctions.GamesState.Error(it.message()))
            }
        }
    }

    override suspend fun fetchCharacters(
        page: String,
        limit: String
    ) {
        _responseCharacters.emit(ZeldaFunctions.CharactersState.Loading)
        retrofitClient.getService().fetchCharacters(
            page = page,
            limit = limit
        ).let {
            if (!it.isSuccessful) {
                _responseCharacters.emit(ZeldaFunctions.CharactersState.Error())
            }
            it.body()?.let { game ->
                _responseCharacters.emit(ZeldaFunctions.CharactersState.Success(game))
            } ?: run {
                _responseCharacters.emit(ZeldaFunctions.CharactersState.Error(it.message()))
            }
        }
    }

    override suspend fun fetchCharacters(
        name: String,
        page: String,
        limit: String
    ) {
        _responseCharacters.emit(ZeldaFunctions.CharactersState.Loading)
        retrofitClient.getService().fetchCharacters(
            name = name,
            page = page,
            limit = limit
        ).let {
            if (!it.isSuccessful) {
                _responseCharacters.emit(ZeldaFunctions.CharactersState.Error())
            }
            it.body()?.let { game ->
                _responseCharacters.emit(ZeldaFunctions.CharactersState.Success(game))
            } ?: run {
                _responseCharacters.emit(ZeldaFunctions.CharactersState.Error(it.message()))
            }
        }
    }

    override suspend fun fetchBosses(
        page: String,
        limit: String
    ) {
        _responseBosses.emit(ZeldaFunctions.BossesState.Loading)
        retrofitClient.getService().fetchBosses(
            page = page,
            limit = limit
        ).let {
            if (!it.isSuccessful) {
                _responseBosses.emit(ZeldaFunctions.BossesState.Error())
            }
            it.body()?.let { game ->
                _responseBosses.emit(ZeldaFunctions.BossesState.Success(game))
            } ?: run {
                _responseBosses.emit(ZeldaFunctions.BossesState.Error(it.message()))
            }
        }
    }

    override suspend fun fetchBosses(
        name: String,
        page: String,
        limit: String
    ) {
        _responseBosses.emit(ZeldaFunctions.BossesState.Loading)
        retrofitClient.getService().fetchBosses(
            name = name,
            page = page,
            limit = limit
        ).let {
            if (!it.isSuccessful) {
                _responseBosses.emit(ZeldaFunctions.BossesState.Error())
            }
            it.body()?.let { game ->
                _responseBosses.emit(ZeldaFunctions.BossesState.Success(game))
            } ?: run {
                _responseBosses.emit(ZeldaFunctions.BossesState.Error(it.message()))
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ZeldaFunctions? = null

        fun getInstance(context: Context): ZeldaFunctions =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: ZeldaFunctionsImpl(context = context).also {
                    INSTANCE = it
                }
            }
    }
}
