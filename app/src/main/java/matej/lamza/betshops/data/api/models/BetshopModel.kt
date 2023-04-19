package matej.lamza.betshops.data.api.models

import com.google.gson.annotations.SerializedName
import matej.lamza.betshops.data.domain.models.Betshop

/**
 * This is representation of an network response for single Betshop model.
 * It has a function for mapping properties to Domain model to make it easier to bubble down data from repository layer
 * down to UI.
 *
 * Annotations are not necessary on properties with the same name as Serialized name but they are here for consistency.
 */
data class BetshopModel(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("county") val county: String,
    @SerializedName("city_id") val cityID: Int,
    @SerializedName("city") val city: String,
    @SerializedName("address") val address: String,
    @SerializedName("location") val location: LocationModel
) {
    fun toDomain(): Betshop {
        return Betshop(name, county, city, address, location)
    }
}
