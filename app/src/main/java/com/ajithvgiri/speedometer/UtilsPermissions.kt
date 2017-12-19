package com.ajithvgiri.speedometer

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.widget.Toast

/**
 * Created by ajithvgiri on 15/12/17.
 */

class UtilsPermissions(context: Activity) {
    var sentToSettings = false
    var sharedPreferences: SharedPreferences

    init {
        sharedPreferences = context.getSharedPreferences("permission", MODE_PRIVATE)
    }

    fun checkSelfPermission(context: Activity): Boolean {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //Show Information about why you need the permission
                val builder = AlertDialog.Builder(context)
                builder.setTitle(context.getString(R.string.location_permission))
                builder.setMessage(context.getString(R.string.permission_description))
                builder.setPositiveButton(context.getString(R.string.permission_grant)) { dialog, which ->
                    dialog.cancel()
                    ActivityCompat.requestPermissions(context, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_CONSTANT)
                }
                builder.setNegativeButton(context.getString(R.string.permission_cancel)) { dialog, which -> dialog.cancel() }
                builder.show()
            } else if (sharedPreferences.getBoolean(Manifest.permission.ACCESS_FINE_LOCATION, false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                val builder = AlertDialog.Builder(context)
                builder.setTitle(context.getString(R.string.location_permission))
                builder.setMessage(context.getString(R.string.permission_description))
                builder.setPositiveButton(context.getString(R.string.permission_grant)) { dialog, which ->
                    dialog.cancel()
                    sentToSettings = true
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", context.packageName, null)
                    intent.data = uri
                    context.startActivityForResult(intent, REQUEST_PERMISSION_SETTING)
                    Toast.makeText(context, context.getString(R.string.goto_settings), Toast.LENGTH_LONG).show()
                }
                builder.setNegativeButton(context.getString(R.string.permission_cancel)) { dialog, which -> dialog.cancel() }
                builder.show()
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(context, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_CONSTANT)
            }


            val editor = sharedPreferences.edit()
            editor.putBoolean(Manifest.permission.ACCESS_FINE_LOCATION, true)
            editor.commit()


        } else {
            //You already have the permission, just go ahead.
            return true
        }

        return false
    }

    companion object {


        val LOCATION_PERMISSION_CONSTANT = 100
        val REQUEST_PERMISSION_SETTING = 101
    }


}
