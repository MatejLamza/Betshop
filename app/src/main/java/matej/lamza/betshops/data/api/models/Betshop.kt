package matej.lamza.betshops.data.api.models

import com.google.gson.annotations.SerializedName

data class Betshop(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("county") val county: String,
    @SerializedName("city_id") val cityID: Int,
    @SerializedName("city") val city: String,
    @SerializedName("address") val address: String,
    @SerializedName("location") val location: Location
)
