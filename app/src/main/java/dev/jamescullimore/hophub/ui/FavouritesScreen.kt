package dev.jamescullimore.hophub.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.jamescullimore.hophub.data.models.Beer
import dev.jamescullimore.hophub.data.preferences.FavouritesPreferences

@Composable
fun FavouritesScreen(
    onBeerClicked: () -> Unit,
) {
    val viewModel: HopHubViewModel = viewModel()
    val lifecycleOwner = LocalLifecycleOwner.current
    val favouritePreferences = FavouritesPreferences(LocalContext.current)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                viewModel.getFavouriteBeers(favouritePreferences = favouritePreferences)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    InfiniteBeerList(
        viewModel = viewModel,
        onSearch = { viewModel.getFavouriteBeers(it, favouritePreferences) },
        onBeerClicked = onBeerClicked,
    )
}