package matej.lamza.betshops.utils

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import matej.lamza.betshops.R

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
}
