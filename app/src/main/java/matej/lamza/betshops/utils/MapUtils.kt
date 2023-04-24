package matej.lamza.betshops.utils

import com.google.maps.android.clustering.ClusterManager
import matej.lamza.betshops.data.domain.models.Betshop
import matej.lamza.betshops.data.domain.models.ClusterBetshop

object MapUtils {
    fun createMarkerCluster(
        dataset: List<Betshop>,
        clusterManager: ClusterManager<ClusterBetshop>,
        numberOfClusters: Int = 10
    ) {
        clusterManager.clearItems()
        for (i in 0..numberOfClusters) {
            val offset = i / MapUtilsV2.OFFSET
            dataset.onEach { shop ->
                val lat = shop.location.latitude + offset
                val lng = shop.location.longitude + offset
                clusterManager.addItem(ClusterBetshop(lat, lng, shop.name, shop.county, shop.city, shop.address))
            }
        }
    }
}
