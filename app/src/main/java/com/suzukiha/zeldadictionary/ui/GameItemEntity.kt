package com.suzukiha.zeldadictionary.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suzukiha.zeldadictionary.model.Game
import com.suzukiha.zeldadictionary.R
import com.suzukiha.zeldadictionary.util.RemoteImage


// TODO SharedElementsがSupportされたら利用
@Composable
fun GameItemEntity(
    game: Game,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    elevation: Dp = 2.dp,
) {
    Surface(
        tonalElevation = elevation,
        modifier = modifier
    ) {
        Row(modifier = Modifier.clickable(onClick = onClick)) {
            RemoteImage(
                url = game.thumbnailUrl!!,
                contentDescription = null,
                modifier = Modifier
                    .width(64.dp)
                    .height(86.dp)
            )
            Column(
                modifier = Modifier.padding(
                    start = 4.dp,
                    top = 8.dp,
                    end = 16.dp,
                )
            ) {
                Text(
                    text = game.name!!,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Text(
                    text = game.description!!,
                    color = colorResource(id = R.color.black),
                    maxLines = 2,
                    fontSize = dimensionResource(id = R.dimen.font_small).value.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = game.releaseDate!!,
                    color = colorResource(id = R.color.black),
                    maxLines = 1,
                    fontSize = dimensionResource(id = R.dimen.font_small).value.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}
