package nl.avans.freekstraten.receptenapp.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.SideEffect
import androidx.core.content.ContextCompat

/**
 * Simple permission handler for gallery access
 */
class PermissionHandler(private val context: Context) {

    // Check if the app has the permission
    fun hasGalleryPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    // Get the required permission based on Android version
    fun getRequiredPermission(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }
}

/**
 * Composable function to request storage permission if not granted already
 */
@Composable
fun RequestGalleryPermission(
    permissionHandler: PermissionHandler,
    onPermissionResult: (Boolean) -> Unit
) {
    // Create permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        onPermissionResult(isGranted)
    }

    // Check if permission is already granted
    val hasPermission = remember(permissionHandler) {
        permissionHandler.hasGalleryPermission()
    }

    // Request permission if not already granted
    if (!hasPermission) {
        SideEffect {
            permissionLauncher.launch(permissionHandler.getRequiredPermission())
        }
    } else {
        // Permission already granted, call the result callback
        SideEffect {
            onPermissionResult(true)
        }
    }
}