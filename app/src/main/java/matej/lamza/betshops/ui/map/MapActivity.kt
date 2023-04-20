package matej.lamza.betshops.ui.map

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import matej.lamza.betshops.R
import matej.lamza.betshops.common.base.BaseActivity
import matej.lamza.betshops.databinding.ActivityMapsBinding
import matej.lamza.betshops.utils.MapUtils
import matej.lamza.betshops.utils.extensions.arePermissionsGranted
import matej.lamza.betshops.utils.extensions.requestPermission
import org.koin.androidx.viewmodel.ext.android.viewModel

class MapActivity : BaseActivity<ActivityMapsBinding>(ActivityMapsBinding::inflate), OnMapReadyCallback {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap

    private val mapViewModel by viewModel<MapViewModel>()
    private val permisions: List<String> =
        listOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).also {
            it.getMapAsync(this)
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

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

    override fun onMapReady(googleMaps: GoogleMap) {
        mMap = googleMaps
    }
}
