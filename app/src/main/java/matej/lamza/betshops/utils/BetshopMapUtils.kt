package matej.lamza.betshops.utils

import android.app.Activity
import android.graphics.BitmapFactory
import com.google.android.gms.maps.CameraUpdateFactory
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


class BetshopMapUtils(private val activity: Activity) : MapUtils<ClusterBetshop>() {

    var onBetshopSelected: ((marker: ClusterBetshop?) -> Unit)? = null

    private lateinit var rendererMy: ClusterMarkerRenderer

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
            rendererMy = ClusterMarkerRenderer(activity, map, this)
            renderer = rendererMy
        }
    }

    override fun setupListeners(onCameraMoveListener: (() -> Unit)?) {
        map.setOnCameraIdleListener(clusterManager)
        map.setOnMapClickListener { onBetshopSelected?.invoke(null) }
        clusterManager.setOnClusterItemClickListener { marker ->
            rendererMy.currentlySelectedMarker = marker
            onBetshopSelected?.invoke(marker)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.position, 16f))
            updateMarkerState(marker)
            return@setOnClusterItemClickListener false
        }
        map.setOnCameraMoveListener { onCameraMoveListener?.invoke() }
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

}
