package matej.lamza.betshops.utils.extensions

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import android.view.WindowMetrics
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.permissionx.guolindev.PermissionX
import matej.lamza.betshops.utils.ScreenDimensions

fun AppCompatActivity.arePermissionsGranted(permissions: List<String>): Boolean {
    val arePermissionsGranted = permissions
        .map { permission -> ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED }
        .contains(false)
    Log.d("UtilsMap", "AreGranted: ${!arePermissionsGranted} ")
    return !arePermissionsGranted
}

fun AppCompatActivity.requestPermission(permissions: List<String>, onGranted: (() -> Unit), onDenied: (() -> Unit)) {
    Log.d("UtilsMap", "requesting: $permissions")
    PermissionX.init(this)
        .permissions(permissions)
        .request { allGranted, grantedList, deniedList ->
            Log.d("UtilsMap", "all Granted: $allGranted |\n GrantedList: $grantedList |\n Denied List: $deniedList  ")
            if (grantedList.isNotEmpty()) onGranted.invoke()
            else onDenied.invoke()
        }

}

fun Activity.getScreenMeasurements(): ScreenDimensions {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val metrics: WindowMetrics = this.getSystemService(WindowManager::class.java).currentWindowMetrics
        with(metrics.bounds) { return@with ScreenDimensions(width(), height()) }
    } else {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        ScreenDimensions(displayMetrics.widthPixels, displayMetrics.heightPixels)
    }
}
