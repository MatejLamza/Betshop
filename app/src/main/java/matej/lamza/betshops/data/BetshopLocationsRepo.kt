package matej.lamza.betshops.data

import kotlinx.coroutines.flow.Flow
import matej.lamza.betshops.data.domain.models.Betshop

interface BetshopLocationsRepo {
    fun fetchBetshopLocation(cords: List<Double>): Flow<List<Betshop>>
}
