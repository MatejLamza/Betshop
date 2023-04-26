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
import matej.lamza.betshops.common.ConnectionState
import matej.lamza.betshops.common.State
import matej.lamza.betshops.data.BetshopLocationsRepo
import matej.lamza.betshops.data.domain.models.ClusterBetshop
import matej.lamza.betshops.utils.extensions.asLiveDataWithState
import matej.lamza.betshops.utils.extensions.launch
import matej.lamza.betshops.utils.extensions.round

private const val UPDATE_MAP = 500L

class MapViewModel(private val betshopLocationsRepo: BetshopLocationsRepo) : ViewModel() {

    private val visibleMapRange = MutableLiveData<VisibleRegion>()
    val isNetworkAvailable = MutableLiveData<ConnectionState>()

    private val _stateBetshop = MutableLiveData<State>()
    val stateBetshop: LiveData<State> = _stateBetshop

    /**
     * [visibleMapRange] live data that holds currently visible map region on the users screen.
     * debounce set to [UPDATE_MAP] interval so we don't trigger multiple calls evey time user
     * scrolls on the map.
     */
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
            .map { it.filterNotNull() }

    /**
     * Observers [updatedCoordinates] so new api call is made to fetch betshop location for given coordinates.
     *  @return [LiveData] List of betshops visible on the users screen.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val betshopLocations = updatedCoordinates
        .distinctUntilChanged()
        .map { newCords -> betshopLocationsRepo.fetchBetshopLocation(newCords) }
        .flattenConcat()
        .asLiveDataWithState(_stateBetshop)

    /**
     * Represents user last or current location if he gave permissions.
     */
    private val _lastLocation = MutableLiveData<Location>()
    val lastLocation: LiveData<Location> = _lastLocation

    /**
     * Holds data about last selected betshop so Bottom Sheet can be updated with information accordingly.
     */
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
