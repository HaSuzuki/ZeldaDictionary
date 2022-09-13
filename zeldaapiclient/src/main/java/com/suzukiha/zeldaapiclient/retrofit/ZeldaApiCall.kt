package com.suzukiha.zeldaapiclient.retrofit

import com.suzukiha.zeldaapiclient.apidata.Boss
import com.suzukiha.zeldaapiclient.apidata.Character
import com.suzukiha.zeldaapiclient.apidata.Game
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ZeldaApiCall {

    /**
     * fetch games
     * from [https://docs.zelda.fanapis.com/docs/games]
     *
     * @param page navigate between pages of results
     * @param limit et the maximum amount of items in the response (default value 20)
     *
     * @return [Game]
     */
    @GET("games")
    suspend fun fetchGames(
        @Query("page") page: String,
        @Query("limit") limit: String
    ): Response<Game>

    /**
     * fetch games
     * from [https://docs.zelda.fanapis.com/docs/games]
     *
     * @param name search for games by its name
     * @param page navigate between pages of results
     * @param limit et the maximum amount of items in the response (default value 20)
     *
     * @return [Game]
     */
    @GET("games")
    suspend fun fetchGames(
        @Query("name") name: String,
        @Query("page") page: String,
        @Query("limit") limit: String
    ): Response<Game>

    /**
     * fetch characters
     * from [https://docs.zelda.fanapis.com/docs/characters]
     *
     * @param page navigate between pages of results
     * @param limit et the maximum amount of items in the response (default value 20)
     *
     * @return [Character]
     */
    @GET("characters")
    suspend fun fetchCharacters(
        @Query("page") page: String,
        @Query("limit") limit: String
    ): Response<Character>

    /**
     * fetch characters
     * from [https://docs.zelda.fanapis.com/docs/characters]
     *
     * @param name search for games by its name
     * @param page navigate between pages of results
     * @param limit et the maximum amount of items in the response (default value 20)
     *
     * @return [Character]
     */
    @GET("characters")
    suspend fun fetchCharacters(
        @Query("name") name: String,
        @Query("page") page: String,
        @Query("limit") limit: String
    ): Response<Character>

    /**
     * fetch bosses
     * from [https://docs.zelda.fanapis.com/docs/bosses]
     *
     * @param page navigate between pages of results
     * @param limit et the maximum amount of items in the response (default value 20)
     *
     * @return [Boss]
     */
    @GET("bosses")
    suspend fun fetchBosses(
        @Query("page") page: String,
        @Query("limit") limit: String
    ): Response<Boss>

    /**
     * fetch bosses
     * from [https://docs.zelda.fanapis.com/docs/bosses]
     *
     * @param name search for games by its name
     * @param page navigate between pages of results
     * @param limit et the maximum amount of items in the response (default value 20)
     *
     * @return [Boss]
     */
    @GET("bosses")
    suspend fun fetchBosses(
        @Query("name") name: String,
        @Query("page") page: String,
        @Query("limit") limit: String
    ): Response<Boss>
}
