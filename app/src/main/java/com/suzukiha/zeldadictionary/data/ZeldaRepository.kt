package com.suzukiha.zeldadictionary.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.suzukiha.zeldaapiclient.datasource.ZeldaRemoteDataSource
import com.suzukiha.zeldaapiclient.firestore.ZeldaFirestoreFunctions
import com.suzukiha.zeldaapiclient.firestoredata.Game
import com.suzukiha.zeldaapiclient.firestoredata.Language
import com.suzukiha.zeldaapiclient.firestoredata.Staff
import com.suzukiha.zeldaapiclient.retrofit.ZeldaFunctions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ZeldaRepository(
    private val zeldaRemoteDataSource: ZeldaRemoteDataSource,
    private val firestore: FirebaseFirestore = Firebase.firestore,
    private val externalScope: CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.IO)
) {

    private val _gamesResponse = MutableSharedFlow<ZeldaFunctions.GamesState>()
    val gameResponse = _gamesResponse.asSharedFlow()

    private val _charactersResponse = MutableSharedFlow<ZeldaFunctions.CharactersState>()
    val charactersResponse = _charactersResponse.asSharedFlow()

    private val _bossesResponse = MutableSharedFlow<ZeldaFunctions.BossesState>()
    val bossesResponse = _bossesResponse.asSharedFlow()

    private val _gamesFromFirestore: MutableLiveData<ZeldaFirestoreFunctions.GamesState> =
        MutableLiveData<ZeldaFirestoreFunctions.GamesState>()
    val gamesFromFirestore: LiveData<ZeldaFirestoreFunctions.GamesState> = _gamesFromFirestore

    private val _staffsFromFirestore: MutableLiveData<ZeldaFirestoreFunctions.StaffState> =
        MutableLiveData<ZeldaFirestoreFunctions.StaffState>()
    val staffsFromFirestore: LiveData<ZeldaFirestoreFunctions.StaffState> = _staffsFromFirestore

    private val _workedOnGameFromFirestore = MutableSharedFlow<ZeldaFirestoreFunctions.GamesState>()
    val workedOnGameFromFirestore = _workedOnGameFromFirestore.asSharedFlow()

    init {
        externalScope.launch {
            zeldaRemoteDataSource.gamesResponse.collectLatest {
                _gamesResponse.emit(it)
            }
        }
        externalScope.launch {
            zeldaRemoteDataSource.charactersResponse.collectLatest {
                _charactersResponse.emit(it)
            }
        }
        externalScope.launch {
            zeldaRemoteDataSource.bossesResponse.collectLatest {
                _bossesResponse.emit(it)
            }
        }
    }

    fun fetchGamesFromFirestore() {
        firestore.collection(COLLECTION_PATH_GAME)
            .orderBy("id", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val list = documents.map {
                    Game(
                        id = it.getLong("id")!!,
                        name = Language(
                            en = it.getString("name.en"),
                            ja = it.getString("name.ja"),
                            zh = it.getString("name.zh")
                        ),
                        description = Language(
                            en = it.getString("description.en"),
                            ja = it.getString("description.ja"),
                            zh = it.getString("description.zh")
                        ),
                        thumbnailUrl = it.getString("thumbnailUrl"),
                        releaseDate = it.getString("releaseDate")
                    )
                }
                _gamesFromFirestore.postValue(ZeldaFirestoreFunctions.GamesState.Success(list))
            }
            .addOnFailureListener { exception ->
                _gamesFromFirestore.postValue(ZeldaFirestoreFunctions.GamesState.Error(exception.message))
            }
    }

    fun fetchGamesFromFirestore(gameIdList: ArrayList<Int>) {
        firestore.collection(COLLECTION_PATH_GAME)
            .whereIn("id", gameIdList)
            .get()
            .addOnSuccessListener { documents ->
                val list = documents.map {
                    Game(
                        id = it.getLong("id")!!,
                        name = Language(
                            en = it.getString("name.en"),
                            ja = it.getString("name.ja"),
                            zh = it.getString("name.zh")
                        ),
                        description = Language(
                            en = it.getString("description.en"),
                            ja = it.getString("description.ja"),
                            zh = it.getString("description.zh")
                        ),
                        thumbnailUrl = it.getString("thumbnailUrl"),
                        releaseDate = it.getString("releaseDate")
                    )
                }
                externalScope.launch {
                    _workedOnGameFromFirestore.emit(ZeldaFirestoreFunctions.GamesState.Success(list))
                }
            }
            .addOnFailureListener { exception ->
                externalScope.launch {
                    _workedOnGameFromFirestore.emit(ZeldaFirestoreFunctions.GamesState.Error(exception.message))
                }
            }
    }

    fun fetchStaffsFromFirestore(gameId: Number) {
        firestore.collection(COLLECTION_PATH_STAFF)
            .whereArrayContains("workedOnGameId", gameId)
            .get()
            .addOnSuccessListener { documents ->
                val list = documents.map {
                    Staff(
                        id = it.getLong("id")!!,
                        name = Language(
                            en = it.getString("name.en"),
                            ja = it.getString("name.ja"),
                            zh = it.getString("name.zh")
                        ),
                        workedOnGameId = it.get("workedOnGameId") as ArrayList<Int>
                    )
                }
                _staffsFromFirestore.postValue(ZeldaFirestoreFunctions.StaffState.Success(list))
            }
            .addOnFailureListener { exception ->
                externalScope.launch {
                    _staffsFromFirestore.postValue(
                        ZeldaFirestoreFunctions.StaffState.Error(
                            exception.message
                        )
                    )
                }
            }
    }

    suspend fun fetchGamesFromApi(
        page: String,
        limit: String
    ) {
        zeldaRemoteDataSource.fetchGames(
            page = page,
            limit = limit
        )
    }

    suspend fun fetchGamesFromApi(
        name: String,
        page: String,
        limit: String
    ) {
        zeldaRemoteDataSource.fetchGames(
            name = name,
            page = page,
            limit = limit
        )
    }

    suspend fun fetchCharactersFromApi(
        page: String,
        limit: String
    ) {
        zeldaRemoteDataSource.fetchCharacters(
            page = page,
            limit = limit
        )
    }

    suspend fun fetchCharactersFromApi(
        name: String,
        page: String,
        limit: String
    ) {
        zeldaRemoteDataSource.fetchCharacters(
            name = name,
            page = page,
            limit = limit
        )
    }

    suspend fun fetchBossesFromApi(
        page: String,
        limit: String
    ) {
        zeldaRemoteDataSource.fetchBosses(
            page = page,
            limit = limit
        )
    }

    suspend fun fetchBossesFromApi(
        name: String,
        page: String,
        limit: String
    ) {
        zeldaRemoteDataSource.fetchBosses(
            name = name,
            page = page,
            limit = limit
        )
    }


    companion object {
        private const val COLLECTION_PATH_GAME = "games"
        private const val COLLECTION_PATH_STAFF = "staffs"
    }
}