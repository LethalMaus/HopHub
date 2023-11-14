package dev.jamescullimore.hophub.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.jamescullimore.hophub.R
import dev.jamescullimore.hophub.data.models.Beer
import dev.jamescullimore.hophub.data.models.BeerManager

enum class HopHubScreen(@StringRes val title: Int, val route: String) {
    BeerList(title = R.string.app_name, route = "BeerList"),
    BeerDetails(title = R.string.details, route = "BeerDetails"),
    Favourites(title = R.string.favourites, route = "Favourites");

    companion object {
        fun getScreenByRoute(route: String): HopHubScreen {
            return when (route) {
                BeerList.route -> BeerList
                BeerDetails.route, -> BeerDetails
                Favourites.route -> Favourites
                else -> throw IllegalArgumentException("Unknown HopHubScreen route: $route")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HopHubAppBar(
    currentScreen: HopHubScreen,
    canNavigateBack: Boolean,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    var showMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            }
        },
        actions = {
            if (currentScreen == HopHubScreen.BeerList) {
                IconButton(onClick = { showMenu = !showMenu }) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = stringResource(R.string.options)
                    )
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(text = {
                        Text(text = stringResource(R.string.favourites))
                    }, onClick = {
                        showMenu = false
                        navController.navigate(HopHubScreen.Favourites.route)
                    })
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HopHubApp(
    navController: NavHostController = rememberNavController(),
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = HopHubScreen.getScreenByRoute(
        backStackEntry?.destination?.route ?: HopHubScreen.BeerList.route
    )

    Scaffold(
        topBar = {
            HopHubAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navController = navController
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = HopHubScreen.BeerList.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = HopHubScreen.BeerList.route) {
                BeerListScreen(
                    onBeerClicked = {
                        navController.navigate(HopHubScreen.BeerDetails.route)
                    },
                )
            }
            composable(route = HopHubScreen.BeerDetails.route) {
                BeerDetailsScreen()
            }
            composable(route = HopHubScreen.Favourites.route) {
                FavouritesScreen(
                    onBeerClicked = {
                        navController.navigate(HopHubScreen.BeerDetails.route)
                    },
                )
            }
        }
    }
}