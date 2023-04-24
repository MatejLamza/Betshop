package matej.lamza.betshops.ui.map

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.distinctUntilChanged
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import matej.lamza.betshops.R
import matej.lamza.betshops.data.domain.models.ClusterBetshop
import matej.lamza.betshops.databinding.ActivityMapsBinding
import matej.lamza.betshops.utils.MapUtils
import matej.lamza.betshops.utils.MapUtilsV2
import matej.lamza.betshops.utils.extensions.arePermissionsGranted
import matej.lamza.betshops.utils.extensions.openNavigation
import matej.lamza.betshops.utils.extensions.requestPermission
import org.koin.androidx.viewmodel.ext.android.viewModel


private const val TAG = "MojNekiTag"

class MapActivityV3 : AppCompatActivity(), OnMapReadyCallback {

    private val utils by lazy { MapUtilsV2<ClusterBetshop>(this) }
    private val mapViewModel by viewModel<MapViewModel>()
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var binding: ActivityMapsBinding

    private var isSavedState: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isSavedState = savedInstanceState != null
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet.bottomSheet)


        initMap()
        checkPermissions()
        setupObservers()
    }

    private fun initMap() {
        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment)
            .also { it.getMapAsync(this@MapActivityV3) }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        if (isSavedState) return
        Log.d(TAG, "onMapReady: ")
        utils.setupMap(googleMap)
        utils.map.apply(::setupMapListeners)
    }

    private fun setupMapListeners(googleMap: GoogleMap) {
        googleMap.setOnCameraMoveListener { mapViewModel.updateMapVisibleRegion(googleMap.projection.visibleRegion) }
    }

    //region UI
    private fun setupObservers() {
        mapViewModel.lastLocation.distinctUntilChanged().observe(this) {
            utils.setLocationOnTheMapAndZoom(it.latitude, it.longitude)
            mapViewModel.updateMapVisibleRegion(utils.map.projection.visibleRegion)
        }
        mapViewModel.betshopLocations.distinctUntilChanged().observe(this) {
            if (it.isEmpty()) Toast.makeText(
                this, "Unfortunatley there are no besthops near this location!", Toast.LENGTH_LONG
            ).show()
            else {
                MapUtils.createMarkerCluster(it, utils.clusterManager)
                utils.map.moveCamera(CameraUpdateFactory.newCameraPosition(utils.map.cameraPosition))
            }
        }
        mapViewModel.currentlySelectedBetshop.distinctUntilChanged().observe(this) { betshop ->
            if (betshop != null) setLocationDetails(betshop)
            setBottomSheetVisibility(betshop != null)
        }

        utils.onBetshopSelected = {
            mapViewModel.updateCurrentlySelectedBetshop(it)
        }
        binding.bottomSheet.route.setOnClickListener {
            val currentPosition = mapViewModel.currentlySelectedBetshop.value?.position
            if (currentPosition != null) openNavigation(currentPosition)
        }
    }

    private fun checkPermissions() {
        if (!arePermissionsGranted(MapUtilsV2.permissions)) {
            requestPermission(MapUtilsV2.permissions, onGranted = {
                Toast.makeText(this, "Permissions granted!", Toast.LENGTH_SHORT).show()
                mapViewModel.requestLastLocation(utils.fusedLocationClient)
            }, onDenied = {
                utils.setLocationOnTheMapAndZoom(MapUtilsV2.MunichMarker.latitude, MapUtilsV2.MunichMarker.longitude)
                mapViewModel.updateMapVisibleRegion(utils.map.projection.visibleRegion)
            })
        } else {
            mapViewModel.requestLastLocation(utils.fusedLocationClient)
        }
    }

    private fun setBottomSheetVisibility(isVisible: Boolean) {
        if (isVisible) binding.bottomSheet.bottomSheet.visibility = View.VISIBLE
        val updatedState = if (isVisible) BottomSheetBehavior.STATE_EXPANDED else BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.state = updatedState
    }

    private fun setLocationDetails(betshop: ClusterBetshop) {
        with(binding.bottomSheet) {
            location.text = betshop.title
            phone.text = betshop.snippet
            schedule.text = "Open now!"
        }
    }
    //endregion
}
