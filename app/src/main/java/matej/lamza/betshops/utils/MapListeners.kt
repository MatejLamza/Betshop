package matej.lamza.betshops.utils

import com.google.android.gms.maps.model.VisibleRegion

interface MapListeners {
    fun OnUpdateVisibleRegion(visibleRegion: VisibleRegion)
}
