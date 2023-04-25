package matej.lamza.betshops.utils

import android.app.Activity
import android.graphics.BitmapFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.view.ClusterRenderer
import matej.lamza.betshops.R
import matej.lamza.betshops.data.domain.models.ClusterBetshop
import matej.lamza.betshops.data.domain.models.MarkerItem

private const val OFFSET = 60

class MapUtilsV3(private val activity: Activity) : MapUtilsAb<ClusterBetshop>(activity) {

    override fun <T : ClusterItem> updateMarkerState(markerClicked: T) {
        if (isClusterManagerInitialized()) {
            val markerBitmap = BitmapFactory.decodeResource(activity.resources, (R.drawable.pin_normal))
            clusterManager.markerCollection.markers
                .find { marker -> marker.position == markerClicked.position }
                ?.setIcon(BitmapDescriptorFactory.fromBitmap(markerBitmap))
        }
    }

    override fun setupListeners(onCameraMoveListener: (() -> Unit)?) {
        map.setOnCameraIdleListener(clusterManager)
        map.setOnMapClickListener { onBetshopSelected?.invoke(null) }
        clusterManager.setOnClusterItemClickListener { marker ->
            onBetshopSelected?.invoke(marker)
            setLocationOnTheMapAndZoom(marker.position.latitude, marker.position.longitude)
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

    override fun setRenderer(clusterRenderer: ClusterRenderer<ClusterBetshop>) {
        clusterManager.apply { renderer = clusterRenderer }
    }
}
