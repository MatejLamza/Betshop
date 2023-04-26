package matej.lamza.betshops.data.domain.models

abstract class MarkerItem {
    abstract val name: String
    abstract val county: String
    abstract val city: String
    abstract val address: String
    abstract val location: Location
}
