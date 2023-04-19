package matej.lamza.betshops.ui.map

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import matej.lamza.betshops.data.BetshopLocationsRepo

private const val TAG = "MapViewModel"

class MapViewModel(private val betshopLocationsRepo: BetshopLocationsRepo) : ViewModel() {

    private val cords = listOf(
        48.16124, 11.60912, 48.12229, 11.52741
    )

    init {
        viewModelScope.launch {
            Log.d(TAG, "Viewmodel scope launched!!! ")
            betshopLocationsRepo.fetchBetshopLocation(cords).collect {
                it.betshops.forEach {
                    Log.d(TAG, "Dobio sam item: ${it.name} ")
                }
            }
        }
    }
}
