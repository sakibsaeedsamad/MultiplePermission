package com.sakibssamad.multiplepermission

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class ManagePermissions(val activity: Activity, val list: List<String>, val code: Int, val context: Context, val packageName:String) {

    fun checkPermissions() {
        if (isPermissionsGranted() != PackageManager.PERMISSION_GRANTED) {
            rationaleCheck()
        } else {
            Toast.makeText(activity, "Permissions already granted.", Toast.LENGTH_SHORT).show()
        }

    }

    private fun isPermissionsGranted(): Int {
        var counter = 0;
        for (permission in list) {
            counter += ContextCompat.checkSelfPermission(activity, permission)
        }
        return counter
    }


    private fun deniedPermission(): String {
        for (permission in list) {
            if (ContextCompat.checkSelfPermission(activity, permission)
                == PackageManager.PERMISSION_DENIED
            ) return permission
        }
        return ""
    }


    private fun showAlert() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Need permission(s)")
        builder.setMessage("Some permissions are required to do the task.")
        builder.setPositiveButton("OK", { dialog, which -> requestPermission() })
        builder.setNeutralButton("Cancel", null)
        val dialog = builder.create()
        dialog.show()
    }


    private fun rationaleCheck() {
        val permission = deniedPermission()
        var rationaleStatus =
            ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)

        val isFirst = MainActivity.sharedPreferences.getBoolean("isFirst", false)

        if (!isFirst) {
            if (rationaleStatus) {
                showAlert()
            } else {
                showSettingDialog()
            }

        } else {
            requestPermission()
            val editor: SharedPreferences.Editor = MainActivity.sharedPreferences.edit()
            editor.putBoolean("isFirst", false)
            editor.apply()
            editor.commit()

        }
    }

     fun showSettingDialog() {
        val builder = AlertDialog.Builder(context)
        builder.apply {
            builder.setTitle("Need permission(s)")
            builder.setMessage("Some permissions are required to do the task.")
            builder.setPositiveButton("Allow") { dialog, which ->
                dialog.cancel()
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", packageName, null)
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
            builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
        }
        val dialog = builder.create()
        dialog.show()
    }

     fun requestPermission(){
        ActivityCompat.requestPermissions(activity, list.toTypedArray(), code)
    }


    fun processPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ): Boolean {
        var result = 0
        if (grantResults.isNotEmpty()) {
            for (item in grantResults) {
                result += item
            }
        }
        if (result == PackageManager.PERMISSION_GRANTED) return true
        return false
    }
}