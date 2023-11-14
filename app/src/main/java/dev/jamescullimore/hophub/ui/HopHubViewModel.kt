package dev.jamescullimore.hophub.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jamescullimore.hophub.data.PunkClient
import dev.jamescullimore.hophub.data.models.Beer
import dev.jamescullimore.hophub.data.preferences.FavouritesPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HopHubViewModel: ViewModel() {

    private val _beers = MutableStateFlow<List<Beer>>(emptyList())
    val beers: StateFlow<List<Beer>> = _beers
    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading
    var currentPage = 1
    private var throttle = 0L

    fun getBeers(input: String = "", page: Int = 1) = throttle {
        currentPage = page
        viewModelScope.launch(Dispatchers.IO) {
            _loading.emit(true)
            val beers = PunkClient.searchBeers(input, page)
            if (page == 1) {
                _beers.emit(beers)
            } else {
                _beers.emit(_beers.value + beers)
            }
            _loading.emit(false)
        }
    }

    fun getFavouriteBeers(input: String = "", favouritePreferences: FavouritesPreferences) {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.emit(true)
            favouritePreferences.getFavourites()?.apply {
                if (input.isNotBlank()) {
                    _beers.emit(filter { it.name.contains(input, ignoreCase = true) })
                } else {
                    _beers.emit(this)
                }
            }
            _loading.emit(false)
        }
    }

    private fun throttle(action: () -> Unit) {
        val timestamp = System.currentTimeMillis()
        if (timestamp - throttle > 500) {
            throttle = timestamp
            action.invoke()
        }
    }
}