package matej.lamza.betshops.data.domain.models

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class ClusterBetshop(
    lat: Double,
    lng: Double,
    name: String,
    county: String = "",
    city: String = "",
    address: String
) : ClusterItem {


    private val position: LatLng
    private val title: String
    private val snippet: String

    override fun getPosition(): LatLng = position

    override fun getTitle(): String = title

    override fun getSnippet(): String = snippet

    init {
        position = LatLng(lat, lng)
        this.title = name
        this.snippet = address
    }

}
