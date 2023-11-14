package dev.jamescullimore.hophub.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dev.jamescullimore.hophub.data.models.Beer
import java.lang.reflect.Type

class FavouritesPreferences(context: Context) {

    private val favourites: SharedPreferences =
        context.getSharedPreferences("favourites", Context.MODE_PRIVATE)
    private val gson: Gson = GsonBuilder().create()

    fun getFavourites(): ArrayList<Beer>? {
        val prefString = favourites.getString(BEERS, "")
        val type: Type = object : TypeToken<List<Beer?>?>(){}.type
        return gson.fromJson<ArrayList<Beer>?>(prefString, type)
    }

    private fun saveFavourites(beers: ArrayList<Beer>) {
        with (favourites.edit()) {
            this?.putString(BEERS, gson.toJson(beers))
            this?.commit()
        }
    }

    fun addBeer(beer: Beer) {
        val beers = getFavourites() ?: ArrayList()
        beers.add(beer)
        saveFavourites(beers)
    }

    fun removeBeer(beer: Beer) {
        getFavourites()?.apply {
            removeIf { it.id == beer.id }
            saveFavourites(this)
        }
    }

    fun containsBeer(beer: Beer): Boolean {
        return getFavourites()?.any { it.id == beer.id } ?: false
    }

    companion object {
        const val BEERS = "beers"
    }
}