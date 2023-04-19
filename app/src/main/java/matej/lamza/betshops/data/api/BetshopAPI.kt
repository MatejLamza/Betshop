package matej.lamza.betshops.data.api

import matej.lamza.betshops.data.api.models.BetshopResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface BetshopAPI {
    @GET("/betshops?boundingBox={0}")
    suspend fun fetchBetshops(
        @Path("boundingBox") boundingBox: String
    ): BetshopResponse
}
