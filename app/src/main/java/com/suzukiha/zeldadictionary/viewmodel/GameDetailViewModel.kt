package com.suzukiha.zeldadictionary.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.suzukiha.zeldaapiclient.datasource.ZeldaRemoteDataSource
import com.suzukiha.zeldaapiclient.firestore.ZeldaFirestoreFunctions
import com.suzukiha.zeldaapiclient.firestoredata.Language
import com.suzukiha.zeldaapiclient.retrofit.ZeldaFunctionsImpl
import com.suzukiha.zeldadictionary.data.ZeldaRepository
import java.util.*

class GameDetailViewModel(
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

    val staffsFromFirestore: LiveData<ZeldaFirestoreFunctions.StaffState> = _zeldaRepository.staffsFromFirestore

    fun fetchStaffsFromFirestore(
        gameId: Number
    ) {
        _zeldaRepository.fetchStaffsFromFirestore(
            gameId = gameId
        )
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
