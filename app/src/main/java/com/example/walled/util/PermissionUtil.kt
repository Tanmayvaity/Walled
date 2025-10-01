package com.example.walled.util

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object PermissionUtil {
    fun isPermissionGranted(
        permission: String,
        context: Context
    ): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun handleRequestLogic(
        activity: Activity,
        context: Context,
        permission: String,
        onRationale: () -> Unit = {},
        onGranted: () -> Unit = {},
        onPermissionInvoked: () -> Unit = {}
    ) {
        when {
            isPermissionGranted(
                permission = permission,
                context = context
            ) -> {
                onGranted()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                permission
            ) -> {
                onRationale()
            }

            else -> {
                onPermissionInvoked()
            }
        }
    }

    fun showRejectionDialog(
        title : String,
        message : String,
        context : Context,
        ){
        MaterialAlertDialogBuilder(context).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton("Settings") { dialog, which ->
                val settingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                val uri = Uri.fromParts("package", context.packageName, null)
                settingsIntent.data = uri
                context.startActivity(settingsIntent)
            }
            setNegativeButton("Dismiss") { dialog, which ->
                dialog.dismiss()
            }
            show()
        }
    }
}