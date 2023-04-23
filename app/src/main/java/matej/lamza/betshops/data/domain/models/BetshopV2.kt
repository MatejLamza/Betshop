package matej.lamza.betshops.data.domain.models

import matej.lamza.betshops.data.api.models.LocationModel

class BetshopV2 : MapModel() {
    override val name: String
        get() = ""
    override val county: String
        get() = ""
    override val city: String
        get() = ""
    override val address: String
        get() = ""
    override val location: LocationModel
        get() = LocationModel(0.0, 0.0)
}
