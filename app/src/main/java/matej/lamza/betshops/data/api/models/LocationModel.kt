package matej.lamza.betshops.data.api.models

import com.google.gson.annotations.SerializedName

data class LocationModel(
    @SerializedName("lng")
    val longitude: Double = 0.0,
    @SerializedName("lat")
    val latitude: Double = 0.0
)
