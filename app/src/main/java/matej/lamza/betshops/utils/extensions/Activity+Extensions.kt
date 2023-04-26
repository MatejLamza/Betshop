package matej.lamza.betshops.utils.extensions

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.WindowMetrics
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.permissionx.guolindev.PermissionX
import matej.lamza.betshops.utils.ScreenDimensions

fun AppCompatActivity.arePermissionsGranted(permissions: Array<String>): Boolean {
    val arePermissionsGranted = permissions
        .map { permission -> ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED }
        .contains(false)
    Log.d("UtilsMap", "AreGranted: ${!arePermissionsGranted} ")
    return !arePermissionsGranted
}

fun AppCompatActivity.requestPermission(permissions: List<String>, onGranted: (() -> Unit), onDenied: (() -> Unit)) {
    Log.d("UtilsMap", "requesting: $permissions")
    if (permissions.isEmpty()) return
    PermissionX.init(this)
        .permissions(permissions)
        .request { allGranted, grantedList, deniedList ->
            Log.d("UtilsMap", "all Granted: $allGranted |\n GrantedList: $grantedList |\n Denied List: $deniedList  ")
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

fun Activity.infoSnackBar(
    view: View, message: String,
    @BaseTransientBottomBar.Duration duration: Int = Snackbar.LENGTH_SHORT
) = Snackbar.make(this, view, message, duration).info()

fun Activity.errorSnackBar(
    view: View, message: String,
    @BaseTransientBottomBar.Duration duration: Int = Snackbar.LENGTH_SHORT
) = Snackbar.make(this, view, message, duration).error()

fun Activity.infoSnackBar(
    view: View, @StringRes messageRes: Int,
    @BaseTransientBottomBar.Duration duration: Int = Snackbar.LENGTH_SHORT
) = this.infoSnackBar(view, getString(messageRes), duration)

fun Activity.errorSnackBar(
    view: View, @StringRes messageRes: Int,
    @BaseTransientBottomBar.Duration duration: Int = Snackbar.LENGTH_SHORT
) = this.errorSnackBar(view, getString(messageRes), duration)

