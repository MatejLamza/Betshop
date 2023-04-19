package matej.lamza.betshops.data.domain.models

import matej.lamza.betshops.data.api.models.LocationModel

data class Betshop(
    val name: String,
    val county: String,
    val city: String,
    val address: String,
    val location: LocationModel
)
