package matej.lamza.betshops.utils

import android.Manifest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.VisibleRegion
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import matej.lamza.betshops.data.domain.models.MarkerItem

abstract class MapUtils<T : ClusterItem> {

    lateinit var map: GoogleMap
    lateinit var clusterManager: ClusterManager<T>


    companion object {
        const val MUNICH_LAT = 48.137154
        const val MUNICH_LON = 11.576124
        const val OFFSET = 60.0

        val permissions =
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)

        val MunichMarker = LatLng(MUNICH_LAT, MUNICH_LON)
    }

    fun setupMap(googleMap: GoogleMap, onCameraMoveListener: (() -> Unit)? = null) {
        map = googleMap
        clusterManager = setupClusterManager()
        setupListeners(onCameraMoveListener)
    }

    /**
     * Function creates location from given coordinates, adds marker to the map and moves camera to that location.
     * @param zoom - [Float] value represent level of zoom from 0 - 16.
     * @param updateVisibleRegion - optional parameter used to update visible region so map can be updated accordingly
     * when action on screen occurs such as zoom on the new location or requesting current location.
     */
    fun setLocationOnTheMapAndZoom(
        latitude: Double,
        longitude: Double,
        zoom: Float = 16f,
        updateVisibleRegion: ((VisibleRegion) -> Unit)? = null
    ) {
        val currentLocation = LatLng(latitude, longitude)
        map.addMarker(MarkerOptions().position(currentLocation))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, zoom))
        updateVisibleRegion?.invoke(map.projection.visibleRegion)
    }

    fun moveToGivenLocation(
        latitude: Double,
        longitude: Double,
        zoom: Float = 16f,
        updateVisibleRegion: ((VisibleRegion) -> Unit)?
    ) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), zoom))
        updateVisibleRegion?.invoke(map.projection.visibleRegion)
    }

    internal fun isClusterManagerInitialized() = this::clusterManager.isInitialized

    abstract fun setupListeners(onCameraMoveListener: (() -> Unit)? = null)

    abstract fun <T : MarkerItem> createMarkerCluster(dataset: List<T>, numberOfClusters: Int = 10)

    abstract fun <T : ClusterItem> updateMarkerState(markerClicked: T)


    abstract fun setupClusterManager(): ClusterManager<T>
}
