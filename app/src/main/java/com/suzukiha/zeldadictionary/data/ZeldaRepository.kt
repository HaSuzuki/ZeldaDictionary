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
import com.suzukiha.zeldadictionary.FirebaseConst
import com.suzukiha.zeldadictionary.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ZeldaRepository @Inject constructor(
    private val zeldaRemoteDataSource: ZeldaRemoteDataSource,
    private val firestore: FirebaseFirestore = Firebase.firestore,
    @ApplicationScope private val externalScope: CoroutineScope =
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
        firestore.collection(FirebaseConst.COLLECTIONPATH.GAME)
            .orderBy(FirebaseConst.FIELD.ID, Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val list = documents.map {
                    Game(
                        id = it.getLong(FirebaseConst.FIELD.ID)!!,
                        name = Language(
                            en = it.getString(FirebaseConst.FIELD.NAME_ENGLISH),
                            ja = it.getString(FirebaseConst.FIELD.NAME_JAPANESE),
                            zh = it.getString(FirebaseConst.FIELD.NAME_CHINESE)
                        ),
                        description = Language(
                            en = it.getString(FirebaseConst.FIELD.DESCRIPTION_ENGLISH),
                            ja = it.getString(FirebaseConst.FIELD.DESCRIPTION_JAPANESE),
                            zh = it.getString(FirebaseConst.FIELD.DESCRIPTION_CHINESE)
                        ),
                        thumbnailUrl = it.getString(FirebaseConst.FIELD.THUMBNAIL_URL),
                        releaseDate = it.getString(FirebaseConst.FIELD.RELEASE_DATE)
                    )
                }
                _gamesFromFirestore.postValue(ZeldaFirestoreFunctions.GamesState.Success(list))
            }
            .addOnFailureListener { exception ->
                _gamesFromFirestore.postValue(ZeldaFirestoreFunctions.GamesState.Error(exception.message))
            }
    }

    fun fetchGamesFromFirestore(gameIdList: ArrayList<Int>) {
        firestore.collection(FirebaseConst.COLLECTIONPATH.GAME)
            .whereIn(FirebaseConst.FIELD.ID, gameIdList)
            .get()
            .addOnSuccessListener { documents ->
                val list = documents.map {
                    Game(
                        id = it.getLong(FirebaseConst.FIELD.ID)!!,
                        name = Language(
                            en = it.getString(FirebaseConst.FIELD.NAME_ENGLISH),
                            ja = it.getString(FirebaseConst.FIELD.NAME_JAPANESE),
                            zh = it.getString(FirebaseConst.FIELD.NAME_CHINESE)
                        ),
                        description = Language(
                            en = it.getString(FirebaseConst.FIELD.DESCRIPTION_ENGLISH),
                            ja = it.getString(FirebaseConst.FIELD.DESCRIPTION_JAPANESE),
                            zh = it.getString(FirebaseConst.FIELD.DESCRIPTION_CHINESE)
                        ),
                        thumbnailUrl = it.getString(FirebaseConst.FIELD.THUMBNAIL_URL),
                        releaseDate = it.getString(FirebaseConst.FIELD.RELEASE_DATE)
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
        firestore.collection(FirebaseConst.COLLECTIONPATH.STAFF)
            .whereArrayContains(FirebaseConst.FIELD.WORKED_ON_GAME_ID, gameId)
            .get()
            .addOnSuccessListener { documents ->
                val list = documents.map {
                    Staff(
                        id = it.getLong(FirebaseConst.FIELD.ID)!!,
                        name = Language(
                            en = it.getString(FirebaseConst.FIELD.NAME_ENGLISH),
                            ja = it.getString(FirebaseConst.FIELD.NAME_JAPANESE),
                            zh = it.getString(FirebaseConst.FIELD.NAME_CHINESE)
                        ),
                        workedOnGameId = it.get(FirebaseConst.FIELD.WORKED_ON_GAME_ID) as ArrayList<Int>
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
}