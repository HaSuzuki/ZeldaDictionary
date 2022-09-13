package com.suzukiha.zeldadictionary.ui

import android.graphics.Bitmap
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.suzukiha.zeldadictionary.R
import com.suzukiha.zeldadictionary.util.isEnglish

@Composable
fun GameDetailContent(
    gameId: Long,
    gameName: String,
    gameDescription: String,
    thumbnailUrl: String?,
    navigation: NavController
) {
    val fontFamily = if (isEnglish()) {
        FontFamily(
            typeface = ResourcesCompat.getFont(LocalContext.current, R.font.hylia_serif_beta_regular)!!
        )
    } else {
        null
    }

    ConstraintLayout {
        val (
            topappbar,
            image,
            name,
            description,
            topDivider,
            staffListTitle,
            staffList
        ) = createRefs()

        GameDetailImage(
            imageUrl = thumbnailUrl,
            name = gameName,
            modifier = Modifier
                .constrainAs(image) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }
                .aspectRatio(4f / 3f)
        )
        GameDetailTopAppBar(
            modifier = Modifier
                .constrainAs(topappbar) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Top
                    )
                ),
            navController = navigation
        )
        GameDetailName(
            name = gameName,
            modifier = Modifier
                .constrainAs(name) {
                    start.linkTo(parent.start)
                    top.linkTo(image.bottom)
                    end.linkTo(parent.end)
                }
                .padding(dimensionResource(id = R.dimen.grid)),
            fontFamily = fontFamily
        )
        GameDetailDescription(
            description = gameDescription,
            modifier = Modifier
                .constrainAs(description) {
                    start.linkTo(parent.start)
                    top.linkTo(name.bottom)
                    end.linkTo(parent.end)
                }
                .padding(dimensionResource(id = R.dimen.grid)),
            fontFamily = fontFamily
        )
        GameDetailDivider(
            modifier = Modifier
                .constrainAs(topDivider) {
                    start.linkTo(parent.start)
                    top.linkTo(description.bottom)
                    end.linkTo(parent.end)
                }
        )
        GameStaffListTitle(
            modifier = Modifier
                .constrainAs(staffListTitle) {
                    start.linkTo(parent.start)
                    top.linkTo(topDivider.bottom)
                    end.linkTo(parent.end)
                }
                .padding(0.dp, dimensionResource(id = R.dimen.margin_8dp))
        )
    }
}

@Composable
fun GameDetailImage(
    imageUrl: String?,
    name: String,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(true)
            .error(drawableResId = R.drawable.dictionary)
            .bitmapConfig(Bitmap.Config.ARGB_8888)
            .allowHardware(false)
            .build(),
        contentDescription = "$name image",
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailTopAppBar(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    SmallTopAppBar(
        title = {},
        navigationIcon = {
            IconButton(
                onClick = {
                    navController.navigateUp()
                }
            ) {
                Icon(
                    painter = painterResource(
                        id = R.drawable.ic_baseline_arrow_back_24
                    ),
                    contentDescription = stringResource(
                        id = R.string.back_arrow_content_description
                    )
                )
            }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = Color.Transparent,
            navigationIconContentColor = colorResource(id = R.color.back_arrow)),
        modifier = modifier
    )
}

@Composable
fun GameDetailName(
    name: String,
    modifier: Modifier = Modifier,
    fontFamily: FontFamily?
) {

    Text(
        text = name,
        modifier = modifier,
        style = MaterialTheme.typography.headlineSmall,
        color = colorResource(id = R.color.text_color),
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        fontFamily = fontFamily
    )
}

@Composable
fun GameDetailDescription(
    description: String,
    modifier: Modifier = Modifier,
    fontFamily: FontFamily?
) {
    Text(
        text = description,
        modifier = modifier,
        style = MaterialTheme.typography.bodyLarge,
        color = colorResource(id = R.color.text_color),
        textAlign = TextAlign.Center,
        fontFamily = fontFamily
    )
}

@Composable
fun GameDetailDivider(
    modifier: Modifier = Modifier
) {
    Divider(
        color = colorResource(id = R.color.image_bg),
        modifier = modifier
    )
}

@Composable
fun GameStaffListTitle(
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(id = R.string.staff_list_title),
        modifier = modifier,
        textAlign = TextAlign.Center,
        fontFamily = FontFamily(
            typeface = ResourcesCompat.getFont(LocalContext.current, R.font.hylia_serif_beta_regular)!!
        ),
        color = colorResource(id = R.color.text_color),
        fontSize = 19.sp
    )
}

@Composable
fun StaffList() {

}

@Preview
@Composable
private fun GameDetailContentPreview() {
    MaterialTheme {
        GameDetailContent(
            1L,
            "Ocarina of Time",
            "description",
            "https://firebasestorage.googleapis.com/v0/b/zelda-dictionary.appspot.com/o/botw.jpeg?alt=media&token=e841b9d1-4fa5-4ec0-983b-7726c5bb33a6",
            rememberNavController()
        )
    }
}
