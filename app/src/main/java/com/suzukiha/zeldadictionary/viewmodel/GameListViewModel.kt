package com.suzukiha.zeldadictionary.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.google.mlkit.nl.translate.Translator
import com.suzukiha.zeldaapiclient.datasource.ZeldaRemoteDataSource
import com.suzukiha.zeldaapiclient.firestore.ZeldaFirestoreFunctions
import com.suzukiha.zeldaapiclient.firestoredata.Language
import com.suzukiha.zeldaapiclient.retrofit.ZeldaFunctions
import com.suzukiha.zeldaapiclient.retrofit.ZeldaFunctionsImpl
import com.suzukiha.zeldadictionary.data.ZeldaRepository
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class GameListViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val _zeldaRemoteDataSource: ZeldaRemoteDataSource by lazy {
        ZeldaRemoteDataSource.getInstance(
            ZeldaFunctionsImpl.getInstance(context = application)
        )
    }
    private val _zeldaRepository: ZeldaRepository by lazy {
        ZeldaRepository(zeldaRemoteDataSource = _zeldaRemoteDataSource)
    }

    var isLastPage: Boolean = false
    var isLoading: Boolean = false
    private var currentPage: String = DEFAULT_PAGE

    // retrofit
    val games: SharedFlow<ZeldaFunctions.GamesState> = _zeldaRepository.gameResponse

    // firestore
    val gamesFromFirestore: LiveData<ZeldaFirestoreFunctions.GamesState> = _zeldaRepository.gamesFromFirestore
    val staffsFromFirestore: LiveData<ZeldaFirestoreFunctions.StaffState> = _zeldaRepository.staffsFromFirestore

    fun fetchGamesFromFirestore() {
        _zeldaRepository.fetchGamesFromFirestore()
    }

    fun fetchGamesFromApi(
        page: String = DEFAULT_PAGE,
        limit: String = DEFAULT_ITEM_LIMIT
    ) {
        viewModelScope.launch {
            _zeldaRepository.fetchGamesFromApi(
                page = page,
                limit = limit
            )
        }
    }

    fun fetchGamesFromApi(
        name: String,
        page: String = DEFAULT_PAGE,
        limit: String = DEFAULT_ITEM_LIMIT
    ) {
        viewModelScope.launch {
            _zeldaRepository.fetchGamesFromApi(
                name = name,
                page = page,
                limit = limit
            )
        }
    }

    suspend fun translateText(text: String?, translator: Translator): String? {
        if (text.isNullOrEmpty()) return null
        return suspendCoroutine { continuation ->
            translator.translate(text)
                .addOnSuccessListener { translatedText ->
                    continuation.resume(translatedText)
                }
                .addOnFailureListener {
                    continuation.resume(text)
                }
                .addOnCanceledListener {
                    continuation.resume(text)
                }
        }
    }

    fun setIncrementPage(page: String) {
        currentPage = page.toInt().inc().toString()
    }

    fun setCurrentPage(page: String) {
        currentPage = page
    }

    fun currentPage(): String {
        return currentPage
    }

    fun getTextFromUseLanguage(text: Language): String? {
        return when (Locale.getDefault().language) {
            MainActivityModel.ENGLISH -> text.en
            MainActivityModel.JAPANESE -> text.ja
            MainActivityModel.CHINESE -> text.zh
            else -> text.en
        }
    }

    companion object {
        const val DEFAULT_PAGE = "0"
        private const val DEFAULT_ITEM_LIMIT = "20"
    }
}
