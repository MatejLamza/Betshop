package matej.lamza.betshops.utils.extensions

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.google.android.gms.maps.model.LatLng

fun Context.openNavigation(position: LatLng) {
    val gmmIntentUri = Uri.parse("google.navigation:q=${position.latitude},${position.longitude}")
    startActivity(Intent(Intent.ACTION_VIEW, gmmIntentUri))
}

fun Context.openNavigation(latitude: Double, longitude: Double) {
    val gmmIntentUri =
        Uri.parse("google.navigation:q=${latitude},${longitude}")
    startActivity(Intent(Intent.ACTION_VIEW, gmmIntentUri))
}
