package co.formaloo.common

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import android.os.Build
import androidx.annotation.RequiresApi


fun hasPermissions(context: Context, vararg permissions: String): Boolean = permissions.all {
    ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
}

fun hasPermission(context: Context, permission: String): Boolean {
    return ActivityCompat.checkSelfPermission(
        context,
        permission
    ) == PackageManager.PERMISSION_GRANTED
}

fun reqPermissions(activity: Activity, permission: Array<String?>, requestCode: Int) {
    ActivityCompat.requestPermissions(activity, permission, requestCode);
}


fun requestPermissions(fragment: Fragment, permission: Array<String?>, requestCode: Int) {
    fragment.requestPermissions(permission, requestCode)
}

fun shouldAskForPermission(activity: Activity, permission: String): Boolean {
    return !hasPermission(activity, permission) && shouldShowRational(activity, permission)
}

@RequiresApi(Build.VERSION_CODES.M)
fun shouldShowRational(activity: Activity, permission: String?): Boolean {
    return activity.shouldShowRequestPermissionRationale(permission!!)
}

fun isCameraPermissionGranted(context: Context): Boolean {
    return hasPermissions(
        context,
        Manifest.permission.CAMERA
    )
}


fun isStorageCameraPermissionGranted(context: Context): Boolean {
    return hasPermissions(
        context,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA

    )
}

fun isStoragePermissionGranted(context: Context): Boolean {
    return hasPermissions(
        context,
        Manifest.permission.WRITE_EXTERNAL_STORAGE

    )
}

fun isLocationPermissionGranted(context: Context): Boolean {
    return hasPermissions(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION,

    )
}

fun hasPermissionInManifest(context: Context, permissionName: String): Boolean {
    val packageName = context.packageName
    try {
        val packageInfo = context.packageManager
            .getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
        val declaredPermisisons = packageInfo.requestedPermissions
        if (declaredPermisisons != null && declaredPermisisons.isNotEmpty()) {
            for (p in declaredPermisisons) {
                if (p == permissionName) {
                    return true
                }
            }
        }
    } catch (e: PackageManager.NameNotFoundException) {
    }
    return false
}
