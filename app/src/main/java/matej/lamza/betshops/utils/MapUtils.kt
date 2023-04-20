package matej.lamza.betshops.utils

import android.content.Context
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.algo.NonHierarchicalViewBasedAlgorithm
import matej.lamza.betshops.R
import matej.lamza.betshops.data.domain.models.Betshop
import matej.lamza.betshops.data.domain.models.ClusterLocation
import matej.lamza.betshops.utils.extensions.height
import matej.lamza.betshops.utils.extensions.width

object MapUtils {

    val MunichMarker = LatLng(48.137154, 11.576124)
    fun setCurrentLocationOnMapWithZoom(latitude: Double, longitude: Double, map: GoogleMap, zoom: Float = 13.0f) {
        val currentLocation = LatLng(latitude, longitude)
        map.addMarker(
            MarkerOptions().position(currentLocation)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_active))
        )
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, zoom))
    }

    fun createMarkerCluster(
        dataset: List<Betshop>,
        clusterManager: ClusterManager<ClusterLocation>,
        numberOfClusters: Int = 10
    ) {
        clusterManager.clearItems()
        for (i in 0..numberOfClusters) {
            val offset = i / 60.0
            dataset.onEach { shop ->
                val lat = shop.location.latitude + offset
                val lng = shop.location.longitude + offset
                clusterManager.addItem(ClusterLocation(lat, lng))
            }
        }
    }

    fun <T : ClusterItem?> setupClusterManager(
        context: Context,
        map: GoogleMap,
        screenDimensions: ScreenDimensions
    ): ClusterManager<T> {
        return ClusterManager<T>(context, map).apply {
            algorithm =
                NonHierarchicalViewBasedAlgorithm(screenDimensions.width, screenDimensions.height)
        }
    }
}
