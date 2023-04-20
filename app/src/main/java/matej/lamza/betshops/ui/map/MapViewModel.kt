package matej.lamza.betshops.ui.map

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.VisibleRegion
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import matej.lamza.betshops.data.BetshopLocationsRepo
import matej.lamza.betshops.utils.extensions.launch
import matej.lamza.betshops.utils.extensions.round

private const val TAG = "MapViewModel"

class MapViewModel(private val betshopLocationsRepo: BetshopLocationsRepo) : ViewModel() {

    private val cords = listOf(48.16124, 11.60912, 48.12229, 11.52741)

    var visibleMapRange = MutableStateFlow<VisibleRegion?>(null)

    @OptIn(FlowPreview::class)
    private val updatedCoordinates =
        visibleMapRange.debounce(1500)
            .map {
                listOf(
                    it?.farRight?.latitude?.round(5),
                    it?.farRight?.longitude?.round(5),
                    it?.nearLeft?.latitude?.round(5),
                    it?.nearLeft?.longitude?.round(5)
                )
            }

    fun updateMap() {
        launch {
            val cords = updatedCoordinates.first().filterNotNull()
            Log.d(TAG, "updateMap: Saljem kordinate: $cords ")
            if (cords.isNotEmpty()) {
                betshopLocationsRepo.fetchBetshopLocation(cords).collect {
                    Log.d(TAG, "Updating map theese are shops: $it ")
                }
            }
        }
    }

}
