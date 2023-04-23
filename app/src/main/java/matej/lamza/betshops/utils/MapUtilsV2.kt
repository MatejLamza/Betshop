package matej.lamza.betshops.utils

import android.app.Activity
import android.graphics.BitmapFactory
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.algo.NonHierarchicalViewBasedAlgorithm
import com.google.maps.android.clustering.view.ClusterRenderer
import matej.lamza.betshops.R
import matej.lamza.betshops.data.domain.models.MapModel
import matej.lamza.betshops.utils.extensions.getScreenMeasurements
import matej.lamza.betshops.utils.extensions.height
import matej.lamza.betshops.utils.extensions.width

class MapUtilsV2<T : ClusterItem>(private val context: Activity) {

    lateinit var map: GoogleMap
    lateinit var clusterManager: ClusterManager<T>
    lateinit var clusterRenderer: ClusterRenderer<T>

    val fusedLocationClient by lazy { LocationServices.getFusedLocationProviderClient(context) }

    var onBetshopSelected: ((marker: T?) -> Unit)? = null

    fun setupMap(googleMap: GoogleMap) {
        map = googleMap
        clusterManager = setupClusterManager(context)
        setupListeners()
    }

    fun setLocationOnTheMapAndZoom(latitude: Double, longitude: Double, zoom: Float = 16f) {
        val currentLocation = LatLng(latitude, longitude)
        map.addMarker(MarkerOptions().position(currentLocation))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, zoom))
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

    fun <T : MapModel, K : ClusterItem> createCluster(
        dataset: List<T>,
        numberOfClusters: Int = 10
    ) {
        val updatedDataset = arrayListOf<T>()
        for (i in 0..numberOfClusters) {
            val offset = i / 60
            dataset.onEach { mapModel ->
                val updatedLat = mapModel.location.latitude + offset
                val updatedLong = mapModel.location.longitude + offset
            }
        }

    }

}
