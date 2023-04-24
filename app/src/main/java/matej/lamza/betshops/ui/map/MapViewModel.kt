package matej.lamza.betshops.ui.map

import android.annotation.SuppressLint
import android.location.Location
import androidx.lifecycle.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.VisibleRegion
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import matej.lamza.betshops.data.BetshopLocationsRepo
import matej.lamza.betshops.data.domain.models.ClusterBetshop
import matej.lamza.betshops.utils.extensions.launch
import matej.lamza.betshops.utils.extensions.round

private const val UPDATE_MAP = 500L

class MapViewModel(private val betshopLocationsRepo: BetshopLocationsRepo) : ViewModel() {

    private var visibleMapRange = MutableLiveData<VisibleRegion>()

    @OptIn(FlowPreview::class)
    private val updatedCoordinates =
        visibleMapRange
            .distinctUntilChanged()
            .asFlow()
            .debounce(UPDATE_MAP)
            .map {
                listOf(
                    it?.farRight?.latitude?.round(),
                    it?.farRight?.longitude?.round(),
                    it?.nearLeft?.latitude?.round(),
                    it?.nearLeft?.longitude?.round()
                )
            }

    @OptIn(ExperimentalCoroutinesApi::class)
    val betshopLocations = updatedCoordinates
        .distinctUntilChanged()
        .map { it.filterNotNull() }
        .map { newCords -> betshopLocationsRepo.fetchBetshopLocation(newCords) }
        .flattenConcat()
        .asLiveData()

    private val _lastLocation = MutableLiveData<Location>()
    val lastLocation: LiveData<Location> = _lastLocation

    private val _currentlySelectedBetshop = MutableLiveData<ClusterBetshop?>(null)
    val currentlySelectedBetshop: LiveData<ClusterBetshop?> = _currentlySelectedBetshop

    fun updateMapVisibleRegion(visibleRegion: VisibleRegion) {
        visibleMapRange.value = visibleRegion
    }

    fun updateCurrentlySelectedBetshop(selectedBetshop: ClusterBetshop?) {
        _currentlySelectedBetshop.value = selectedBetshop
    }

    @SuppressLint("MissingPermission")
    fun requestLastLocation(fusedLocationClient: FusedLocationProviderClient) {
        launch {
            _lastLocation.value = fusedLocationClient.lastLocation.await()
        }
    }
}
