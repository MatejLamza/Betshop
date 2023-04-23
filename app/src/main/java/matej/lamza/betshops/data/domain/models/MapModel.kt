package matej.lamza.betshops.data.domain.models

import matej.lamza.betshops.data.api.models.LocationModel

abstract class MapModel {
    abstract val name: String
    abstract val county: String
    abstract val city: String
    abstract val address: String
    abstract val location: LocationModel
}
