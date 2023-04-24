package matej.lamza.betshops.ui.map

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.distinctUntilChanged
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import matej.lamza.betshops.R
import matej.lamza.betshops.common.base.BaseActivity
import matej.lamza.betshops.data.domain.models.ClusterBetshop
import matej.lamza.betshops.databinding.ActivityMapsBinding
import matej.lamza.betshops.utils.LocationUtils
import matej.lamza.betshops.utils.MapUtils
import matej.lamza.betshops.utils.MapUtilsV2
import matej.lamza.betshops.utils.extensions.arePermissionsGranted
import matej.lamza.betshops.utils.extensions.openNavigation
import org.koin.androidx.viewmodel.ext.android.viewModel


private const val REQUEST_CODE_PERMISSIONS = 1007
private const val REQUEST_CODE_LOCATION = 1001

class MapActivityV3 : BaseActivity<ActivityMapsBinding>(ActivityMapsBinding::inflate), OnMapReadyCallback {

    private val mapUtils by lazy { MapUtilsV2<ClusterBetshop>(this) }
    private val locationUtils by lazy { LocationUtils(this) }

    private val mapViewModel by viewModel<MapViewModel>()
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            mapViewModel.requestLastLocation(locationUtils.fusedLocationClient)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet.bottomSheet)

        initMap()
        checkPermissions()
        setupObservers()
    }

    private fun checkPermissions() {
        if (!arePermissionsGranted(MapUtilsV2.permissions))
            requestPermissions(MapUtilsV2.permissions, REQUEST_CODE_PERMISSIONS)
        else getCurrentLocation()
    }

    //region Map
    private fun initMap() {
        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment)
            .also { it.getMapAsync(this@MapActivityV3) }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mapUtils.setupMap(googleMap)
        mapUtils.map.apply(::setupMapListeners)
    }

    private fun setupMapListeners(googleMap: GoogleMap) {
        googleMap.setOnCameraMoveListener { mapViewModel.updateMapVisibleRegion(googleMap.projection.visibleRegion) }
    }
    //endregion

    //region UI
    private fun setupObservers() {
        mapViewModel.lastLocation.distinctUntilChanged().observe(this) {
            locationUtils.stopLocationUpdates(locationCallback)
            mapUtils.setLocationOnTheMapAndZoom(it.latitude, it.longitude)
            mapViewModel.updateMapVisibleRegion(mapUtils.map.projection.visibleRegion)
        }
        mapViewModel.betshopLocations.distinctUntilChanged().observe(this) {
            if (it.isEmpty()) Toast.makeText(
                this, "Unfortunatley there are no besthops near this location!", Toast.LENGTH_LONG
            ).show()
            else {
                MapUtils.createMarkerCluster(it, mapUtils.clusterManager)
                mapUtils.map.moveCamera(CameraUpdateFactory.newCameraPosition(mapUtils.map.cameraPosition))
            }
        }
        mapViewModel.currentlySelectedBetshop.distinctUntilChanged().observe(this) { betshop ->
            if (betshop != null) setLocationDetails(betshop)
            setBottomSheetVisibility(betshop != null)
        }
        mapUtils.onBetshopSelected = {
            mapViewModel.updateCurrentlySelectedBetshop(it)
        }
        binding.bottomSheet.route.setOnClickListener {
            val currentPosition = mapViewModel.currentlySelectedBetshop.value?.position
            if (currentPosition != null) openNavigation(currentPosition)
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

    /**
     * This function check weather user enabled GPS if [true] we are requesting location updates.
     * Otherwise in the [false] scenario this function will prompt user to turn on his GPS services.
     * @see LocationUtils.startLocationUpdates
     */
    private fun getCurrentLocation() {
        if (locationUtils.isGPSEnabled(this)) locationUtils.startLocationUpdates(locationCallback)
        else
            locationUtils.task.addOnFailureListener { exception ->
                if (exception is ResolvableApiException)
                    runCatching { exception.startResolutionForResult(this, REQUEST_CODE_LOCATION) }
            }
    }

    override fun onPause() {
        super.onPause()
        locationUtils.stopLocationUpdates(locationCallback)
    }

    @SuppressLint("MissingPermission")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_LOCATION) {
            if (locationUtils.isGPSEnabled(this))
                locationUtils.startLocationUpdates(locationCallback)
            else
                mapUtils.setLocationOnTheMapAndZoom(MapUtilsV2.MunichMarker.latitude,
                    MapUtilsV2.MunichMarker.longitude,
                    updateVisibleRegion = { mapViewModel.updateMapVisibleRegion(it) })
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) getCurrentLocation()
            else
                mapUtils.setLocationOnTheMapAndZoom(MapUtilsV2.MunichMarker.latitude,
                    MapUtilsV2.MunichMarker.longitude,
                    updateVisibleRegion = { mapViewModel.updateMapVisibleRegion(it) })
        }
    }
}
