package matej.lamza.betshops.data.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import matej.lamza.betshops.data.BetshopLocationsRepo
import matej.lamza.betshops.data.api.BetshopAPI
import matej.lamza.betshops.data.api.models.BetshopResponse

class BetshopLocationRepoImpl(private val betshopAPI: BetshopAPI) : BetshopLocationsRepo {

    override fun fetchBetshopLocation(cords: List<Double>): Flow<BetshopResponse> {
        val args = cords.joinToString(separator = ",")
        Log.d("MapViewModel", "fetchBetshopLocation $cords: \n after joining: $args")
        return flow {
            emit(betshopAPI.fetchBetshops(args))
        }
    }
}
