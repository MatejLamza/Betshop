package matej.lamza.betshops.data

import kotlinx.coroutines.flow.Flow
import matej.lamza.betshops.data.api.models.BetshopResponse

interface BetshopLocationsRepo {
    fun fetchBetshopLocation(cords: List<Double>): Flow<BetshopResponse>
}
