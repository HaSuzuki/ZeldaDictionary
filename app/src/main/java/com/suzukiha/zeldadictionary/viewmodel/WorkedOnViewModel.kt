package com.suzukiha.zeldadictionary.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.suzukiha.zeldaapiclient.datasource.ZeldaRemoteDataSource
import com.suzukiha.zeldaapiclient.firestore.ZeldaFirestoreFunctions
import com.suzukiha.zeldaapiclient.firestoredata.Language
import com.suzukiha.zeldaapiclient.retrofit.ZeldaFunctionsImpl
import com.suzukiha.zeldadictionary.data.ZeldaRepository
import com.suzukiha.zeldadictionary.util.QUERY_LIMIT
import com.suzukiha.zeldadictionary.util.sliceLimitList
import kotlinx.coroutines.flow.SharedFlow
import java.util.*
import kotlin.collections.ArrayList

class WorkedOnViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private val _zeldaRemoteDataSource: ZeldaRemoteDataSource by lazy {
        ZeldaRemoteDataSource.getInstance(
            ZeldaFunctionsImpl.getInstance(context = application)
        )
    }
    private val _zeldaRepository: ZeldaRepository by lazy {
        ZeldaRepository(zeldaRemoteDataSource = _zeldaRemoteDataSource)
    }

    val workedOnGameFromFirestore: SharedFlow<ZeldaFirestoreFunctions.GamesState> =
        _zeldaRepository.workedOnGameFromFirestore

    fun fetchGamesFromFirestore(
        gameIdList: ArrayList<Int>
    ) {
        val size = gameIdList.size
        if (size > QUERY_LIMIT) {
            val list = sliceLimitList(list = gameIdList)
            list.forEach {
                _zeldaRepository.fetchGamesFromFirestore(
                    gameIdList = ArrayList(it)
                )
            }
        } else {
            _zeldaRepository.fetchGamesFromFirestore(
                gameIdList = gameIdList
            )
        }
    }

    fun getTextFromUseLanguage(text: Language): String? {
        return when (Locale.getDefault().language) {
            MainActivityModel.ENGLISH -> text.en
            MainActivityModel.JAPANESE -> text.ja
            MainActivityModel.CHINESE -> text.zh
            else -> text.en
        }
    }
}