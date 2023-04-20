package matej.lamza.betshops.ui.splash

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import matej.lamza.betshops.common.base.BaseActivity
import matej.lamza.betshops.databinding.ActivityMainBinding
import matej.lamza.betshops.utils.extensions.arePermissionsGranted
import matej.lamza.betshops.utils.extensions.requestPermission

class SplashActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    private val permisions: List<String> =
        listOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.request.setOnClickListener {
            if (arePermissionsGranted(permisions.toList())) {
                requestPermission(permisions,
                    onGranted = {
                        Log.d("Splash", "Permissions are granted go to app!")
                        Toast.makeText(this, "Permissions granted!", Toast.LENGTH_SHORT).show()
                    },
                    onDenied = {
                        Log.d("Splash", "Permissions denied!")
                        Toast.makeText(this, "Permissions are denied!", Toast.LENGTH_SHORT).show()
                    })
            }
        }
    }


}
