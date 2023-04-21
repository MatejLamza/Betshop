package matej.lamza.betshops.ui.map

import androidx.lifecycle.*
import com.google.android.gms.maps.model.VisibleRegion
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import matej.lamza.betshops.data.BetshopLocationsRepo
import matej.lamza.betshops.utils.extensions.round

private const val TAG = "MapViewModel"

class MapViewModel(private val betshopLocationsRepo: BetshopLocationsRepo) : ViewModel() {

    private var visibleMapRange = MutableLiveData<VisibleRegion>()

    @OptIn(FlowPreview::class)
    private val updatedCoordinates =
        visibleMapRange
            .distinctUntilChanged()
            .asFlow()
            .debounce(1500)
            .map {
                listOf(
                    it?.farRight?.latitude?.round(),
                    it?.farRight?.longitude?.round(),
                    it?.nearLeft?.latitude?.round(),
                    it?.nearLeft?.longitude?.round()
                )
            }

    @OptIn(FlowPreview::class)
    val betshopLocations = updatedCoordinates
        .distinctUntilChanged()
        .debounce(1000)
        .map { it.filterNotNull() }
        .map { newCords -> betshopLocationsRepo.fetchBetshopLocation(newCords) }
        .flattenConcat()
        .asLiveData()


    fun updateMapVisibleRegion(visibleRegion: VisibleRegion) {
        visibleMapRange.value = visibleRegion
    }
}
