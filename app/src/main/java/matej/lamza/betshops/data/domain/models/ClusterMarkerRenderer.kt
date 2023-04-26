package matej.lamza.betshops.data.domain.models

import android.content.Context
import android.graphics.BitmapFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import matej.lamza.betshops.R


class ClusterMarkerRenderer(
    private val context: Context,
    map: GoogleMap,
    clusterManager: ClusterManager<ClusterBetshop>
) : DefaultClusterRenderer<ClusterBetshop>(context, map, clusterManager) {

    var currentlySelectedMarker: ClusterBetshop? = null

    // SEAL CLASS
    private val active = BitmapFactory.decodeResource(context.resources, R.drawable.pin_active)
    private val normal = BitmapFactory.decodeResource(context.resources, R.drawable.pin_normal)

    override fun onBeforeClusterItemRendered(item: ClusterBetshop, markerOptions: MarkerOptions) {
        val isActive = currentlySelectedMarker?.position == item.position
        markerOptions.icon(getItemIcon(isActive))
    }

    private fun getItemIcon(isActive: Boolean): BitmapDescriptor? {
        return BitmapDescriptorFactory.fromBitmap(if (isActive) active else normal)
    }
}
