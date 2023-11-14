package dev.jamescullimore.hophub.data

import dev.jamescullimore.hophub.data.models.Beer
import io.ktor.client.HttpClient
import io.ktor.client.call.receive
import io.ktor.client.engine.android.Android
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.Json

object PunkClient {

    private val punkClient = HttpClient(Android) {
        install(JsonFeature) {
            serializer = KotlinxSerializer(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(Logging) {
            level = LogLevel.ALL
        }
    }

    suspend fun searchBeers(input: String = "", page: Int = 1): List<Beer> {
        val beerParam = if (input.isNotBlank()) "beer_name=${input.trim().replace(" ", "_")}" else ""
        val response: HttpResponse = punkClient.get { url("https://api.punkapi.com/v2/beers?${beerParam}&page=${page}") }
        return if (response.status == HttpStatusCode.OK) {
            response.receive()
        } else {
            emptyList()
        }
    }
}