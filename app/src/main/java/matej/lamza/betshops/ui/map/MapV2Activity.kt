package matej.lamza.betshops.ui.map

import android.os.Bundle
import android.util.Log
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.maps.android.clustering.ClusterManager
import matej.lamza.betshops.R
import matej.lamza.betshops.common.base.BaseActivity
import matej.lamza.betshops.data.domain.models.ClusterBetshop
import matej.lamza.betshops.databinding.ActivityMapsBinding
import matej.lamza.betshops.utils.MapUtils
import matej.lamza.betshops.utils.extensions.getScreenMeasurements
import org.koin.androidx.viewmodel.ext.android.viewModel

class MapV2Activity : BaseActivity<ActivityMapsBinding>(ActivityMapsBinding::inflate), OnMapReadyCallback {

    private lateinit var clusterManager: ClusterManager<ClusterBetshop>

    private val bottomSheetView by lazy { binding.bottomSheet.bottomSheet }
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>


    private var mIsRestored = false
    private val mapViewModel by viewModel<MapViewModel>()
    private val screenDimensions by lazy { getScreenMeasurements() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mIsRestored = savedInstanceState != null
        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).also { it.getMapAsync(this) }

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView)
        setBottomSheetVisibility(false)
        setupListeners()
    }

    private fun setupListeners() {
        binding.bottomSheet.route.setOnClickListener {
            val selectedBetshop = mapViewModel.currentlySelectedLocation
            if (selectedBetshop != null) {
                MapUtils.launchNavigationToCords(selectedBetshop.position, this)
            }
        }
    }

    private fun render(map: GoogleMap) {
        if (!mIsRestored) map.moveCamera(CameraUpdateFactory.newLatLngZoom(MapUtils.MunichMarker, 10f))
        mapViewModel.betshopLocations.observe(this) {
            Log.d("kkk", "Dobio sam:  ${it.size} lokacija")
            MapUtils.createMarkerCluster(it, clusterManager)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        clusterManager = MapUtils.setupClusterManager(this, googleMap, screenDimensions)
        setupMapListeners(googleMap)
        render(googleMap)
    }

    private fun setupMapListeners(googleMap: GoogleMap) {
        googleMap.setOnCameraMoveListener { mapViewModel.updateMapVisibleRegion(googleMap.projection.visibleRegion) }
        googleMap.setOnCameraIdleListener(clusterManager)
        clusterManager.setOnClusterItemClickListener {
            mapViewModel.currentlySelectedLocation = it
            setLocationDetails(it)
            setBottomSheetVisibility(true)
            return@setOnClusterItemClickListener false
        }
        googleMap.setOnMapClickListener {
            mapViewModel.currentlySelectedLocation = null
            setBottomSheetVisibility(false)
        }
    }

    private fun setLocationDetails(betshop: ClusterBetshop) {
        with(binding.bottomSheet) {
            location.text = betshop.title
            phone.text = betshop.snippet
            schedule.text = "Open now!"
        }
    }

    private fun setBottomSheetVisibility(isVisible: Boolean) {
        val updatedState = if (isVisible) BottomSheetBehavior.STATE_EXPANDED else BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.state = updatedState
    }
}
