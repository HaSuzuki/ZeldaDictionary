package com.suzukiha.zeldadictionary.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.material.composethemeadapter.MdcTheme
import com.suzukiha.zeldadictionary.model.Game

// TODO SharedElementsがSupportされたら利用
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameItemList(
    games: List<Game>,
    selectGame: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier) {
        item {
            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
        }
        items(
            items = games,
        ) { game ->
            GameItem(
                game = game,
                selectGame = selectGame
            )
        }
    }
}

@Composable
fun GameItem(
    game: Game,
    selectGame: (Int) -> Unit
) {
    Row(modifier = Modifier.padding(bottom = 8.dp)) {
        GameItemEntity(
            game = game,
            onClick = { selectGame(game.id) },
            modifier = Modifier.height(96.dp)
        )
    }
}

@Preview(name = "GameItemList")
@Composable
private fun GameItemListPreview() {
    val games = arrayListOf<Game>()
    games.add(Game(
        id = 0,
        name = "sample",
        thumbnailUrl = "",
        description = "samplesamplesamplesamplesamplesamplesamplesamplesamplesamplesamplesamplesamplesample",
        releaseDate = "2022/09/01"
    ))
    games.add(Game(
        id = 1,
        name = "sample",
        thumbnailUrl = "",
        description = "samplesamplesamplesamplesamplesamplesamplesamplesamplesamplesamplesamplesamplesample",
        releaseDate = "2022/09/01"
    ))
    MdcTheme {
        GameItemList(
            games = games,
            selectGame = { }
        )
    }
}