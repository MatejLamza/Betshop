package matej.lamza.betshops.data.domain.models

import matej.lamza.betshops.data.api.models.LocationModel

data class Betshop(
    override val name: String,
    override val county: String,
    override val city: String,
    override val address: String,
    override val location: LocationModel
) : MarkerItem()
