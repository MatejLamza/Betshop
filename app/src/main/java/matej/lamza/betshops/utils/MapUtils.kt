package matej.lamza.betshops.utils

import android.Manifest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import matej.lamza.betshops.data.domain.models.MarkerItem

abstract class MapUtils<T : ClusterItem> {

    lateinit var map: GoogleMap
    lateinit var clusterManager: ClusterManager<T>
    lateinit var mapListeners: MapListeners

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
        clusterManager = setupClusterManager()
        map.apply(::setupListeners)
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
    ) {
        val currentLocation = LatLng(latitude, longitude)
        map.addMarker(MarkerOptions().position(currentLocation))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, zoom))
        mapListeners.OnUpdateVisibleRegion(map.projection.visibleRegion)
    }

    fun moveToGivenLocation(latitude: Double, longitude: Double, zoom: Float = 16f) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), zoom))
        mapListeners.OnUpdateVisibleRegion(map.projection.visibleRegion)
    }

    internal fun isClusterManagerInitialized() = this::clusterManager.isInitialized
    internal fun isMapInitialized() = this::map.isInitialized

    abstract fun setupListeners(googleMap: GoogleMap)

    abstract fun <T : MarkerItem> createMarkerCluster(dataset: List<T>, numberOfClusters: Int = 10)

    abstract fun <T : ClusterItem> updateMarkerState(markerClicked: T)


    abstract fun setupClusterManager(): ClusterManager<T>
}
