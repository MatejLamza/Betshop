package matej.lamza.betshops.data.api.models

import com.google.gson.annotations.SerializedName

data class BetshopResponse(
    @SerializedName("count")
    val count: Int,
    @SerializedName("betshops")
    val betshops: List<Betshop>
)
