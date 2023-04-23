package matej.lamza.betshops.utils.extensions

import android.content.pm.PackageManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.permissionx.guolindev.PermissionX


fun AppCompatActivity.arePermissionsGranted(vararg permissions: String): Boolean {
    return permissions
        .toList()
        .map { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }
        .any { true }
}

fun AppCompatActivity.arePermissionsGranted(permissions: List<String>): Boolean {
    val arePermissionsGranted = permissions
        .map { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }.contains(false)
    Log.d("UtilsMap", "AreGranted: ${!arePermissionsGranted} ")
    return !arePermissionsGranted
}

fun AppCompatActivity.requestPermission(permissions: List<String>, onGranted: (() -> Unit), onDenied: (() -> Unit)) {
    PermissionX.init(this)
        .permissions(permissions)
        .request { allGranted, _, _ ->
            if (allGranted) onGranted.invoke()
            else onDenied.invoke()
        }
}
