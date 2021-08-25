package com.projects.authenticate.utils


import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.snackbar.Snackbar
import com.projects.authenticate.R

object Utilities {



    fun isBiometricHardWareAvailable(con: Context): Boolean {
        var result = false
        val biometricManager = BiometricManager.from(con)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            when (biometricManager.canAuthenticate(BiometricManager.Authenticators.DEVICE_CREDENTIAL or BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
                BiometricManager.BIOMETRIC_SUCCESS -> result = true
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> result = false
                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> result = false
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> result = false
                BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED ->
                    result = true
                BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED ->
                    result = true
                BiometricManager.BIOMETRIC_STATUS_UNKNOWN ->
                    result = false
            }
        } else {
            when (biometricManager.canAuthenticate()) {
                BiometricManager.BIOMETRIC_SUCCESS -> result = true
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> result = false
                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> result = false
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> result = false
                BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> result = true
                BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED->result = false
                BiometricManager.BIOMETRIC_STATUS_UNKNOWN->result =false
            }
        }
        return result
    }


    fun deviceHasPasswordPinLock(con: Context): Boolean {
        val keymgr = con.getSystemService(AppCompatActivity.KEYGUARD_SERVICE) as KeyguardManager
        if (keymgr.isKeyguardSecure)
            return true
        return false
    }
}
