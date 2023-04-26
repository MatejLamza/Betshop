package matej.lamza.betshops.data.domain.models

data class Betshop(
    override val name: String,
    override val county: String,
    override val city: String,
    override val address: String,
    override val location: Location
) : MarkerItem()
