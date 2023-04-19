package matej.lamza.betshops.data.api

import matej.lamza.betshops.data.api.models.BetshopResponseModel
import retrofit2.http.GET
import retrofit2.http.Query

interface BetshopAPI {

    @GET("/betshops?boundingBox={0}")
    suspend fun fetchBetshops(
        @Query("boundingBox") boundingBox: String
    ): BetshopResponseModel
}
