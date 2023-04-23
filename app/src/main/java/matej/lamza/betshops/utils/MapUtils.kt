package matej.lamza.betshops.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
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
import matej.lamza.betshops.data.domain.models.ClusterBetshop
import matej.lamza.betshops.data.domain.models.ClusterMarkerRenderer
import matej.lamza.betshops.utils.extensions.getScreenMeasurements
import matej.lamza.betshops.utils.extensions.height
import matej.lamza.betshops.utils.extensions.width

object MapUtils {

    val permisions: List<String> =
        listOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)

    val MunichMarker = LatLng(48.137154, 11.576124)
    fun setCurrentLocationOnMapWithZoom(latitude: Double, longitude: Double, map: GoogleMap, zoom: Float = 13.0f) {
        val currentLocation = LatLng(latitude, longitude)
        map.addMarker(
            MarkerOptions().position(currentLocation)
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_active))
        )
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, zoom))
    }

    fun createMarkerCluster(
        dataset: List<Betshop>,
        clusterManager: ClusterManager<ClusterBetshop>,
        numberOfClusters: Int = 10
    ) {
        clusterManager.clearItems()
        for (i in 0..numberOfClusters) {
            val offset = i / 60.0
            dataset.onEach { shop ->
                val lat = shop.location.latitude + offset
                val lng = shop.location.longitude + offset
                clusterManager.addItem(ClusterBetshop(lat, lng, shop.name, shop.county, shop.city, shop.address))
            }
        }
    }

    fun <T : ClusterItem?> setupClusterManager(
        context: Activity,
        map: GoogleMap
    ): ClusterManager<ClusterBetshop> {
        val screenDimensions = context.getScreenMeasurements()
        return ClusterManager<ClusterBetshop>(context, map).apply {
            algorithm = NonHierarchicalViewBasedAlgorithm(screenDimensions.width, screenDimensions.height)
            renderer = ClusterMarkerRenderer(context, map, this)
        }
    }

    fun launchNavigationToCords(latitude: Double, longitude: Double, context: Context) {
        val gmmIntentUri =
            Uri.parse("google.navigation:q=${latitude},${longitude}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        context.startActivity(mapIntent)
    }

    fun launchNavigationToCords(position: LatLng, context: Context) {
        val gmmIntentUri =
            Uri.parse("google.navigation:q=${position.latitude},${position.longitude}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        context.startActivity(mapIntent)
    }

    fun updateMarkerState(
        context: Context,
        clusterManager: ClusterManager<ClusterBetshop>,
        clusterClicked: ClusterBetshop
    ) {
        clusterManager.markerCollection.markers
            .find { marker ->
                marker.position == clusterClicked.position
            }?.setIcon(
                BitmapDescriptorFactory
                    .fromBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.pin_active))
            )
    }
}
