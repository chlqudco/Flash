package com.chlqudco.flash

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.chlqudco.flash.ui.FlashlightScreen
import com.chlqudco.flash.ui.theme.FlashTheme

class MainActivity : ComponentActivity() {
    private val cameraManager by lazy { getSystemService(CameraManager::class.java) }
    private var torchCameraId: String? = null
    private var callbackRegistered = false
    private var permissionRequested = false
    private var uiState by mutableStateOf(FlashlightUiState())

    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            permissionRequested = true
            if (granted) {
                uiState = uiState.copy(showSettingsAction = false)
                setTorchEnabled(true)
            } else {
                uiState = FlashlightUiState(
                    status = FlashlightStatus.PERMISSION_DENIED,
                    showSettingsAction = !shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
                )
            }
        }

    private val torchCallback = object : CameraManager.TorchCallback() {
        override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
            if (cameraId != torchCameraId) return
            uiState = FlashlightUiState(
                status = if (enabled) FlashlightStatus.ON else FlashlightStatus.OFF
            )
        }

        override fun onTorchModeUnavailable(cameraId: String) {
            if (cameraId != torchCameraId) return
            uiState = FlashlightUiState(status = FlashlightStatus.UNAVAILABLE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        resolveTorchCamera()
        setContent {
            FlashTheme {
                FlashlightScreen(
                    state = uiState,
                    onToggle = ::toggleTorch,
                    onOpenSettings = ::openAppSettings
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (torchCameraId == null) resolveTorchCamera()
        registerTorchCallback()
    }

    override fun onResume() {
        super.onResume()
        if (
            uiState.status == FlashlightStatus.PERMISSION_DENIED &&
            hasCameraPermission()
        ) {
            uiState = FlashlightUiState(status = FlashlightStatus.OFF)
        }
    }

    override fun onStop() {
        unregisterTorchCallback()
        super.onStop()
    }

    override fun onDestroy() {
        if (isFinishing && uiState.status == FlashlightStatus.ON) {
            setTorchEnabled(false)
        }
        super.onDestroy()
    }

    private fun toggleTorch() {
        when (uiState.status) {
            FlashlightStatus.NO_FLASH,
            FlashlightStatus.UNAVAILABLE -> return

            FlashlightStatus.ON -> setTorchEnabled(false)
            FlashlightStatus.PERMISSION_DENIED -> {
                if (uiState.showSettingsAction) {
                    openAppSettings()
                } else {
                    requestCameraPermission()
                }
            }

            else -> {
                if (hasCameraPermission()) {
                    setTorchEnabled(true)
                } else {
                    requestCameraPermission()
                }
            }
        }
    }

    private fun requestCameraPermission() {
        permissionRequested = true
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun setTorchEnabled(enabled: Boolean) {
        val cameraId = torchCameraId ?: run {
            uiState = FlashlightUiState(status = FlashlightStatus.NO_FLASH)
            return
        }

        try {
            cameraManager.setTorchMode(cameraId, enabled)
            uiState = FlashlightUiState(
                status = if (enabled) FlashlightStatus.ON else FlashlightStatus.OFF
            )
        } catch (error: SecurityException) {
            uiState = FlashlightUiState(
                status = FlashlightStatus.PERMISSION_DENIED,
                showSettingsAction = permissionRequested &&
                    !shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
            )
            Log.w(TAG, "Camera permission is unavailable", error)
        } catch (error: CameraAccessException) {
            uiState = FlashlightUiState(status = FlashlightStatus.ERROR)
            Log.w(TAG, "Unable to access the camera flash", error)
        } catch (error: IllegalArgumentException) {
            uiState = FlashlightUiState(status = FlashlightStatus.ERROR)
            Log.w(TAG, "Invalid torch camera", error)
        }
    }

    private fun resolveTorchCamera() {
        try {
            val flashCameras = cameraManager.cameraIdList.filter { cameraId ->
                cameraManager.getCameraCharacteristics(cameraId)
                    .get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            }
            torchCameraId = flashCameras.firstOrNull { cameraId ->
                cameraManager.getCameraCharacteristics(cameraId)
                    .get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK
            } ?: flashCameras.firstOrNull()
            uiState = FlashlightUiState(
                status = if (torchCameraId == null) {
                    FlashlightStatus.NO_FLASH
                } else {
                    FlashlightStatus.OFF
                }
            )
        } catch (error: CameraAccessException) {
            torchCameraId = null
            uiState = FlashlightUiState(status = FlashlightStatus.ERROR)
            Log.w(TAG, "Unable to inspect available cameras", error)
        }
    }

    private fun registerTorchCallback() {
        if (callbackRegistered || torchCameraId == null) return
        try {
            cameraManager.registerTorchCallback(mainExecutor, torchCallback)
            callbackRegistered = true
        } catch (error: RuntimeException) {
            Log.w(TAG, "Unable to observe torch state", error)
        }
    }

    private fun unregisterTorchCallback() {
        if (!callbackRegistered) return
        cameraManager.unregisterTorchCallback(torchCallback)
        callbackRegistered = false
    }

    private fun hasCameraPermission(): Boolean =
        ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED

    private fun openAppSettings() {
        startActivity(
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", packageName, null)
            )
        )
    }

    private companion object {
        private const val TAG = "Flashlight"
    }
}

internal data class FlashlightUiState(
    val status: FlashlightStatus = FlashlightStatus.OFF,
    val showSettingsAction: Boolean = false
)

internal enum class FlashlightStatus {
    OFF,
    ON,
    UNAVAILABLE,
    NO_FLASH,
    PERMISSION_DENIED,
    ERROR
}
