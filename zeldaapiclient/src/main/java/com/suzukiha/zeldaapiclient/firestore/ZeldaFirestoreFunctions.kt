package com.suzukiha.zeldaapiclient.firestore

import com.suzukiha.zeldaapiclient.firestoredata.Game
import com.suzukiha.zeldaapiclient.firestoredata.Staff

interface ZeldaFirestoreFunctions {

    sealed class GamesState {
        object Loading : GamesState()

        data class Success(val gameList: List<Game>) : GamesState()

        data class Error(val message: String? = null) : GamesState()
    }

    sealed class StaffState {
        object Loading : StaffState()

        data class Success(val staff: List<Staff>) : StaffState()

        data class Error(val message: String? = null) : StaffState()
    }
}