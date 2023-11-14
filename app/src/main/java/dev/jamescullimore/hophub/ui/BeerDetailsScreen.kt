package dev.jamescullimore.hophub.ui

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import dev.jamescullimore.hophub.R
import dev.jamescullimore.hophub.data.models.Beer
import dev.jamescullimore.hophub.data.models.BeerManager
import dev.jamescullimore.hophub.data.preferences.FavouritesPreferences
import kotlin.math.absoluteValue

@Composable
fun BeerDetailsScreen() {
    val favouritePreferences = FavouritesPreferences(LocalContext.current)
    val beer = BeerManager.beer!!

    val texts = arrayListOf(Pair(stringResource(id = R.string.description), beer.description))
    beer.tagline?.let {
        texts.add(Pair(stringResource(id = R.string.tagline), it))
    }
    beer.ingredients?.let {
        texts.add(Pair(stringResource(id = R.string.ingredients), it.toString()))
    }
    beer.foodPairing?.let {
        texts.add(Pair(stringResource(id = R.string.food_pairing), it.joinToString("\n\n")))
    }
    beer.brewersTips?.let {
        texts.add(Pair(stringResource(id = R.string.brewers_tips), it))
    }

    val configuration = LocalConfiguration.current
    when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            BeerDetailsLandscapeLayout(favouritePreferences, beer, texts)
        }
        else -> {
            BeerDetailsPortraitLayout(favouritePreferences, beer, texts)
        }
    }


}

@Composable
fun BeerDetailsPortraitLayout(favouritePreferences: FavouritesPreferences, beer: Beer, texts: ArrayList<Pair<String, String>>) {
    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val (image, favourite, card,  name) = createRefs()

        AsyncImage(
            model = beer.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .padding(16.dp)
                .constrainAs(image) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                    bottom.linkTo(name.top)
                    height = Dimension.fillToConstraints
                },
        )

        Favourite(
            modifier = Modifier
                .constrainAs(favourite) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                },
            favouritePreferences = favouritePreferences,
            beer = beer
        )

        Text(
            modifier = Modifier.constrainAs(name) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start, margin = 8.dp)
                end.linkTo(parent.end, margin = 8.dp)
            },
            text = beer.name,
            fontSize = 24.sp
        )

        CardPagerWithIndicator(modifier = Modifier
            .constrainAs(card) {
                top.linkTo(name.bottom, margin = 8.dp)
                start.linkTo(parent.start, margin = 16.dp)
                end.linkTo(parent.end, margin = 16.dp)
                bottom.linkTo(parent.bottom, margin = 16.dp)
                height = Dimension.fillToConstraints
                width = Dimension.fillToConstraints
            },
            texts = texts
        )
    }
}

@Composable
fun BeerDetailsLandscapeLayout(favouritePreferences: FavouritesPreferences, beer: Beer, texts: ArrayList<Pair<String, String>>) {
    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val (image, favourite, card, name) = createRefs()

        AsyncImage(
            model = beer.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .padding(16.dp)
                .constrainAs(image) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(card.start)
                    bottom.linkTo(parent.bottom)
                    height = Dimension.fillToConstraints
                },
        )

        Favourite(
            modifier = Modifier
                .constrainAs(favourite) {
                    top.linkTo(image.top)
                    end.linkTo(card.start)
                },
            favouritePreferences = favouritePreferences,
            beer = beer
        )

        Text(
            modifier = Modifier.constrainAs(name) {
                top.linkTo(parent.top, margin = 8.dp)
                bottom.linkTo(card.top, margin = 8.dp)
                start.linkTo(image.end, margin = 8.dp)
                end.linkTo(parent.end, margin = 8.dp)
            },
            text = beer.name,
            fontSize = 24.sp
        )

        CardPagerWithIndicator(modifier = Modifier
            .constrainAs(card) {
                top.linkTo(name.bottom, margin = 8.dp)
                start.linkTo(image.end, margin = 16.dp)
                end.linkTo(parent.end, margin = 16.dp)
                bottom.linkTo(parent.bottom, margin = 16.dp)
                height = Dimension.fillToConstraints
                width = Dimension.fillToConstraints
            },
            texts = texts
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardPagerWithIndicator(modifier: Modifier, texts: ArrayList<Pair<String, String>>) {
    Box(modifier = modifier.fillMaxSize()) {
        val pageCount = texts.size
        val pagerState = rememberPagerState(pageCount = { pageCount })
        HorizontalPager(
            beyondBoundsPageCount = 2,
            state = pagerState) {
            PagerItem(
                title = texts[it].first,
                text = texts[it].second,
                modifier = Modifier
                    .pagerFadeTransition(it, pagerState = pagerState)
                    .fillMaxSize()
            )
        }
        Row(
            Modifier
                .height(30.dp)
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pageCount) { iteration ->
                val color = if (pagerState.currentPage == iteration) Color.White else Color.White.copy(alpha = 0.5f)
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(10.dp)
                )
            }
        }
    }
}

@Composable
fun PagerItem(modifier: Modifier, title: String, text: String) {
    Card(modifier = modifier) {
        Text(
            text = title,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            fontSize = 20.sp
        )
        Text(
            text = text,
            textAlign = TextAlign.Center,
            maxLines = 10,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
fun PagerState.calculateCurrentOffsetForPage(page: Int): Float {
    return (currentPage - page) + currentPageOffsetFraction
}

@OptIn(ExperimentalFoundationApi::class)
fun Modifier.pagerFadeTransition(page: Int, pagerState: PagerState) =
    graphicsLayer {
        val pageOffset = pagerState.calculateCurrentOffsetForPage(page)
        translationX = pageOffset * size.width
        alpha = 1- pageOffset.absoluteValue
    }

@Composable
fun Favourite(modifier: Modifier, favouritePreferences: FavouritesPreferences, beer: Beer) {
    val isFavourite = favouritePreferences.containsBeer(beer)
    var nonce by remember { mutableIntStateOf(1) }
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.favourite))
    val animatable = rememberLottieAnimatable()

    LaunchedEffect(composition, nonce) {
        composition ?: return@LaunchedEffect
        animatable.animate(
            composition,
            continueFromPreviousAnimate = false,
            clipSpec = LottieClipSpec.Progress(
                min = if (isFavourite) 0.5f else 0f,
                max = if (isFavourite) 1f else 0.5f,
            )
        )
    }
    LottieAnimation(
        composition,
        { animatable.progress },
        modifier = modifier
            .height(50.dp)
            .width(50.dp)
            .padding(8.dp)
            .clickable {
                nonce++
                if (isFavourite) {
                    favouritePreferences.removeBeer(beer)
                } else {
                    favouritePreferences.addBeer(beer)
                }
            }
    )
}