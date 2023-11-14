package dev.jamescullimore.hophub.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Beer (
    val id: Int,
    val name: String,
    @SerialName("first_brewed")
    val firstBrewed: String?,
    @SerialName("image_url")
    val imageUrl: String?,
    val description: String,
    val tagline: String?,
    val ingredients: Ingredient?,
    @SerialName("food_pairing")
    val foodPairing: List<String>?,
    @SerialName("brewers_tips")
    val brewersTips: String?
)

@Serializable
data class Ingredient(
    val malt: List<Malt>,
    val hops: List<Hop>,
    val yeast: String
) {
    override fun toString(): String {
        val hopsString = "Hops: " + hops.joinToString(", ") { it.name }
        val maltString = "Malt: " + malt.joinToString(", ") { it.name }
        val yeastString = "Yeast: $yeast"
        return "$hopsString\n$maltString\n$yeastString"
    }
}

@Serializable
data class Malt(
    val name: String,
    val amount: Amount
)

@Serializable
data class Hop(
    val name: String,
    val amount: Amount,
    val add: String,
    val attribute: String
)

@Serializable
data class Amount(
    val value: Double,
    val unit: String
)

object BeerManager {
    var beer: Beer? = null
}