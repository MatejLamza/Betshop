package matej.lamza.betshops.ui.map

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.algo.NonHierarchicalViewBasedAlgorithm
import matej.lamza.betshops.R
import matej.lamza.betshops.common.base.BaseActivity
import matej.lamza.betshops.data.domain.models.ClusterLocation
import matej.lamza.betshops.databinding.ActivityMapsBinding
import matej.lamza.betshops.utils.MapUtils
import org.koin.androidx.viewmodel.ext.android.viewModel

class MapV2Activity : BaseActivity<ActivityMapsBinding>(ActivityMapsBinding::inflate), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var clusterManager: ClusterManager<ClusterLocation>


    private val mapViewModel by viewModel<MapViewModel>()
    private var mIsRestored = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mIsRestored = savedInstanceState != null
        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).also { it.getMapAsync(this) }

//        setupObservers()
    }

    private fun setupObservers() {
        mapViewModel.betshopLocations.observe(this) { betshops ->
            map.clear()
        }
    }

    private fun startDemo(isRestore: Boolean) {
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)

        val widthDp = (metrics.widthPixels / metrics.density).toInt()
        val heightDp = (metrics.heightPixels / metrics.density).toInt()

        if (!isRestore) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(MapUtils.MunichMarker, 10f))
        }

        clusterManager = ClusterManager(this, map)
        clusterManager.setAlgorithm(NonHierarchicalViewBasedAlgorithm(widthDp, heightDp))

        map.setOnCameraIdleListener(clusterManager)

        mapViewModel.betshopLocations.observe(this) {

            clusterManager.clearItems()
            Log.d("kkk", "Dobio sam:  ${it.size} lokacija")
            MapUtils.createMarkerCluster(it, clusterManager)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        if (this::map.isInitialized) return
        map = googleMap
        map.setOnCameraMoveListener { mapViewModel.visibleMapRange.value = map.projection.visibleRegion }
        startDemo(mIsRestored)
    }
}
