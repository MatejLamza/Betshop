package matej.lamza.betshops.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.os.Looper
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task


private const val LOCATION_UPDATE_INTERVAL = 10000L

class LocationUtils(private val context: Context) {

    private val builder = LocationSettingsRequest.Builder()
        .setAlwaysShow(true)
        .addLocationRequest(createLocationRequest())

    private val client: SettingsClient = LocationServices.getSettingsClient(context)
    private val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

    val fusedLocationClient by lazy { LocationServices.getFusedLocationProviderClient(context) }


    private fun createLocationRequest(): LocationRequest {
        return LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, LOCATION_UPDATE_INTERVAL).apply {
            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            setWaitForAccurateLocation(true)
        }.build()
    }

    /**
     * This is done because fusedLocation will return null if GPS was previously turned off or no fused location was
     * not attached to any of the services. This will update our location every [LOCATION_UPDATE_INTERVAL] milliseconds.
     */
    @SuppressLint("MissingPermission")
    fun startLocationUpdates(locationCallback: LocationCallback) {
        fusedLocationClient.requestLocationUpdates(createLocationRequest(), locationCallback, Looper.getMainLooper())
    }

    fun stopLocationUpdates(locationCallback: LocationCallback) {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    /**
     * Location settings method can use BatterySaver mode in which case method used to determine location
     * is done via Wi-Fi instead of GPS, so we have to check if provider is GPS or NETWORK.
     */
    fun isGPSEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    }

    /**
     * This function check weather user enabled GPS if [true] we are requesting location updates.
     * Otherwise in the [false] scenario this function will prompt user to turn on his GPS services.
     * @see LocationUtils.startLocationUpdates
     */
    fun getCurrentLocation(
        context: Context,
        locationCallback: LocationCallback,
        enableGPSServicesPrompt: ((ResolvableApiException) -> Unit)
    ) {
        if (isGPSEnabled(context)) startLocationUpdates(locationCallback)
        else task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                runCatching { enableGPSServicesPrompt(exception) }
            }
        }
    }
}
