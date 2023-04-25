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
    private val clusterManager: ClusterManager<ClusterBetshop>
) : DefaultClusterRenderer<ClusterBetshop>(context, map, clusterManager) {

    override fun onBeforeClusterItemRendered(item: ClusterBetshop, markerOptions: MarkerOptions) {
        markerOptions.icon(getItemIcon(context = context))
    }

    private fun getItemIcon(context: Context): BitmapDescriptor? {
        return BitmapDescriptorFactory.fromBitmap(
            BitmapFactory.decodeResource(context.resources, R.drawable.pin_normal)
        )
    }
}
