package matej.lamza.betshops.utils

import android.Manifest
import android.app.Activity
import android.graphics.BitmapFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.VisibleRegion
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.algo.NonHierarchicalViewBasedAlgorithm
import com.google.maps.android.clustering.view.ClusterRenderer
import matej.lamza.betshops.R
import matej.lamza.betshops.utils.extensions.getScreenMeasurements
import matej.lamza.betshops.utils.extensions.height
import matej.lamza.betshops.utils.extensions.width

class MapUtilsV2<T : ClusterItem>(private val context: Activity) {

    lateinit var map: GoogleMap
    lateinit var clusterManager: ClusterManager<T>
    lateinit var clusterRenderer: ClusterRenderer<T>

    var onBetshopSelected: ((marker: T?) -> Unit)? = null

    companion object {
        const val MUNICH_LAT = 48.137154
        const val MUNICH_LON = 11.576124
        const val OFFSET = 60.0

        val permissions =
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)

        val MunichMarker = LatLng(MUNICH_LAT, MUNICH_LON)
    }

    fun setupMap(googleMap: GoogleMap) {
        map = googleMap
        clusterManager = setupClusterManager(context)
        setupListeners()
    }

    fun setLocationOnTheMapAndZoom(
        latitude: Double,
        longitude: Double,
        zoom: Float = 16f,
        updateVisibleRegion: ((visibleRegion: VisibleRegion) -> Unit)? = null
    ) {
        val currentLocation = LatLng(latitude, longitude)
        map.addMarker(MarkerOptions().position(currentLocation))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, zoom))
        updateVisibleRegion?.invoke(map.projection.visibleRegion)
    }

    private fun setupListeners() {
        map.setOnCameraIdleListener(clusterManager)
        map.setOnMapClickListener { onBetshopSelected?.invoke(null) }
        clusterManager.setOnClusterItemClickListener { marker ->
            onBetshopSelected?.invoke(marker)
            setLocationOnTheMapAndZoom(marker.position.latitude, marker.position.longitude)
            updateMarkerState(marker)
            return@setOnClusterItemClickListener false
        }
    }

    private fun <T : ClusterItem> updateMarkerState(markerClicked: T) {
        if (this::clusterManager.isInitialized) {
            val markerBitmap = BitmapFactory.decodeResource(context.resources, (R.drawable.pin_normal))
            clusterManager.markerCollection.markers
                .find { marker -> marker.position == markerClicked.position }
                ?.setIcon(BitmapDescriptorFactory.fromBitmap(markerBitmap))
        }
    }

    private fun setupClusterManager(context: Activity): ClusterManager<T> {
        val screenDimensions = context.getScreenMeasurements()
        return ClusterManager<T>(context, map).apply {
            algorithm = NonHierarchicalViewBasedAlgorithm(screenDimensions.width, screenDimensions.height)
        }
    }
}
