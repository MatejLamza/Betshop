package matej.lamza.betshops.utils

import android.app.Activity
import android.graphics.BitmapFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.algo.NonHierarchicalViewBasedAlgorithm
import matej.lamza.betshops.R
import matej.lamza.betshops.data.domain.models.ClusterBetshop
import matej.lamza.betshops.data.domain.models.ClusterMarkerRenderer
import matej.lamza.betshops.data.domain.models.MarkerItem
import matej.lamza.betshops.utils.extensions.getScreenMeasurements
import matej.lamza.betshops.utils.extensions.height
import matej.lamza.betshops.utils.extensions.width


class BetshopMapUtils(private val activity: Activity, private val mapListener: MapListeners) :
    MapUtils<ClusterBetshop>() {

    var onBetshopSelected: ((marker: ClusterBetshop?) -> Unit)? = null

    init {
        mapListeners = mapListener
    }

    //Not neccessary cluster marker renderer is doing that job
    override fun <T : ClusterItem> updateMarkerState(markerClicked: T) {
        if (isClusterManagerInitialized()) {
            val markerBitmap = BitmapFactory.decodeResource(activity.resources, (R.drawable.pin_active))
            clusterManager.markerCollection.markers
                .find { marker -> marker.position == markerClicked.position }
                ?.setIcon(BitmapDescriptorFactory.fromBitmap(markerBitmap))
        }
    }

    override fun setupClusterManager(): ClusterManager<ClusterBetshop> {
        val screenDimensions = activity.getScreenMeasurements()
        return ClusterManager<ClusterBetshop>(activity, map).apply {
            algorithm = NonHierarchicalViewBasedAlgorithm(screenDimensions.width, screenDimensions.height)
            renderer = ClusterMarkerRenderer(activity, map, this)
        }
    }

    override fun setupListeners(googleMap: GoogleMap) {
        map.setOnCameraIdleListener(clusterManager)
        map.setOnMapClickListener { onBetshopSelected?.invoke(null) }
        map.setOnCameraMoveListener { mapListener.OnUpdateVisibleRegion(map.projection.visibleRegion) }

        clusterManager.setOnClusterItemClickListener { marker ->
            (clusterManager.renderer as ClusterMarkerRenderer).currentlySelectedMarker = marker
            onBetshopSelected?.invoke(marker)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.position, 16f))

            return@setOnClusterItemClickListener false
        }
    }

    override fun <I : MarkerItem> createMarkerCluster(dataset: List<I>, numberOfClusters: Int) {
        clusterManager.clearItems()
        for (i in 0..numberOfClusters) {
            val offset = i / OFFSET
            dataset.onEach { shop ->
                val lat = shop.location.latitude + offset
                val lng = shop.location.longitude + offset
                clusterManager.addItem(ClusterBetshop(lat, lng, shop.name, shop.county, shop.city, shop.address))
            }
        }
    }

    fun setDefaultLocation() {
        setLocationOnTheMapAndZoom(MUNICH_LAT, MUNICH_LON)
        mapListener.OnUpdateVisibleRegion(map.projection.visibleRegion)
    }
}
