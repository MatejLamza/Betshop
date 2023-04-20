package matej.lamza.betshops.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import matej.lamza.betshops.data.BetshopLocationsRepo
import matej.lamza.betshops.data.api.BetshopAPI
import matej.lamza.betshops.data.domain.models.Betshop

class BetshopLocationRepoImpl(private val betshopAPI: BetshopAPI) : BetshopLocationsRepo {

    override fun fetchBetshopLocation(cords: List<Double>): Flow<List<Betshop>> =
        flow { emit(betshopAPI.fetchBetshops(cords.joinToString(separator = ","))) }
            .map { response ->
                response.betshops.map { betshopModel -> betshopModel.toDomain() }
            }
}
