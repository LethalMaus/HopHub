package dev.jamescullimore.hophub.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import dev.jamescullimore.hophub.R
import dev.jamescullimore.hophub.data.models.Beer
import dev.jamescullimore.hophub.data.models.BeerManager

@Composable
fun BeerListScreen(
    onBeerClicked: () -> Unit,
) {
    val viewModel: HopHubViewModel = viewModel()
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                viewModel.getBeers(page = 1)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val loadMoreItems = {
        viewModel.getBeers(page = viewModel.currentPage + 1)
    }

    InfiniteBeerList(
        viewModel = viewModel,
        onBeerClicked = onBeerClicked,
        onSearch = { viewModel.getBeers(input = it, page = 1) },
        loadMoreItems = loadMoreItems
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfiniteBeerList(
    viewModel: HopHubViewModel,
    onBeerClicked: () -> Unit,
    onSearch: (String) -> Unit,
    loadMoreItems: () -> Unit = {}
) {
    val beers by viewModel.beers.collectAsState()
    val isLoading by viewModel.loading.collectAsState()
    var searchText by remember { mutableStateOf("") }

    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val (search, list, loader, empty) = createRefs()
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.constrainAs(loader) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
            )
        } else if (beers.isEmpty()) {
            Text(
                text = stringResource(id = R.string.empty),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(8.dp)
                    .constrainAs(empty) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                        start.linkTo(parent.start)
                        bottom.linkTo(parent.bottom)
                    }
            )
        }

        OutlinedTextField(
            value = searchText,
            onValueChange = {
                searchText = it
                onSearch(it)
            },
            label = { Text(text = stringResource(id = R.string.search)) },
            modifier = Modifier
                .constrainAs(search) {
                    top.linkTo(parent.top, margin = 8.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                    width = Dimension.fillToConstraints
                }
        )

        InfiniteScrollList(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(list) {
                    top.linkTo(search.bottom, margin = 16.dp)
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                    bottom.linkTo(parent.bottom)
                    height = Dimension.fillToConstraints
                },
            itemCount = beers.size,
            loadMoreItems = {
                loadMoreItems.invoke()
            }
        ) {
            BeerItem(beers[it], onBeerClicked)
        }
    }
}

@Composable
fun BeerItem(beer: Beer, onBeerClicked: () -> Unit) {
    Card(
        modifier = Modifier
            .height(300.dp)
            .padding(4.dp)
            .clickable {
                BeerManager.beer = beer
                onBeerClicked()
            }
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxSize()
        ) {
            val (image, name) = createRefs()

            AsyncImage(
                model = beer.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .height(200.dp)
                    .padding(top = 40.dp, start = 16.dp, end = 16.dp)
                    .constrainAs(image) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                        start.linkTo(parent.start)
                    },
            )
            Text(
                text = beer.name,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(8.dp)
                    .constrainAs(name) {
                        top.linkTo(image.bottom)
                        end.linkTo(parent.end)
                        start.linkTo(parent.start)
                        bottom.linkTo(parent.bottom)
                    }
            )
        }
    }
}

@Composable
fun InfiniteScrollList(
    modifier: Modifier,
    itemCount: Int,
    loadMoreItems: () -> Unit,
    content: @Composable (Int) -> Unit
) {
    val listState = rememberLazyGridState()
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        state = listState
    ) {
        items(itemCount) { index ->
            content(index)
            if (index == itemCount - 1) {
                loadMoreItems()
            }
        }
    }
}
