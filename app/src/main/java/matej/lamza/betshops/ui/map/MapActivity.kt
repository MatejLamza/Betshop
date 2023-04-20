package matej.lamza.betshops.ui.map

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.algo.NonHierarchicalViewBasedAlgorithm
import matej.lamza.betshops.R
import matej.lamza.betshops.common.base.BaseActivity
import matej.lamza.betshops.data.domain.models.Betshop
import matej.lamza.betshops.data.domain.models.ClusterLocation
import matej.lamza.betshops.databinding.ActivityMapsBinding
import matej.lamza.betshops.utils.MapUtils
import matej.lamza.betshops.utils.extensions.arePermissionsGranted
import matej.lamza.betshops.utils.extensions.requestPermission
import org.koin.androidx.viewmodel.ext.android.viewModel

abstract class MapActivity : BaseActivity<ActivityMapsBinding>(ActivityMapsBinding::inflate), OnMapReadyCallback {

    private lateinit var clusterManager: ClusterManager<ClusterLocation>
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap

    private var widthDp: Int = 0
    private var heightDp: Int = 0

    private val mapViewModel by viewModel<MapViewModel>()
    private val permisions: List<String> =
        listOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).also { it.getMapAsync(this) }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val temp = this.getSystemService(WindowManager::class.java).currentWindowMetrics
            widthDp = temp.bounds.width()
            heightDp = temp.bounds.height()
        } else {
            val temp = Resources.getSystem().displayMetrics
            widthDp = temp.widthPixels
            heightDp = temp.heightPixels
        }


        mapViewModel.betshopLocations.observe(this) {
            Log.d("kkk", "Dobio sam:  ${it.size} lokacija")
            setupCluster(it)
        }
    }

    override fun onMapReady(googleMaps: GoogleMap) {
        mMap = googleMaps
        clusterManager = ClusterManager(this, mMap)
        clusterManager.setAlgorithm(NonHierarchicalViewBasedAlgorithm(widthDp, heightDp));
        mMap.setOnCameraIdleListener(clusterManager)
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (!arePermissionsGranted(permisions)) {
            requestPermission(permisions,
                onGranted = {
                    fusedLocationClient.lastLocation.addOnSuccessListener {
                        Log.d("bbb", "Your current location is: ${it.latitude} ${it.longitude}  | $it ")
                        MapUtils.setCurrentLocationOnMapWithZoom(it.latitude, it.longitude, mMap)
                    }
                },
                onDenied = {}
            )
        }
    }

    private fun setupCluster(lista: List<Betshop>) {
        Log.d("bbb", "setupCluster: Settam cluster!")
        for (i in 0..10) {
            val offset = i / 60.0
            lista.forEach {
                val position = it.location
                val lat = position.latitude + offset
                val lng = position.longitude + offset
                val offsetItem = ClusterLocation(lat, lng, "", "")
                clusterManager.addItem(offsetItem)
            }
        }
    }
}
