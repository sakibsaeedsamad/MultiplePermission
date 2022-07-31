package com.sakibssamad.multiplepermission

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button

class MainActivity : AppCompatActivity() {
    companion object {
        lateinit var sharedPreferences: SharedPreferences
    }
    val MYPREFERENCES = "MySharedPref"

    private val PermissionsRequestCode = 123
    private lateinit var managePermissions: ManagePermissions
    private lateinit var managePermissionJava: ManagePermissionJava
    var x = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreferences = this.getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE)

        val editor:SharedPreferences.Editor =  sharedPreferences.edit()
        editor.putBoolean("isFirst",true)
        editor.apply()
        editor.commit()

        val list = listOf<String>(
            Manifest.permission.CAMERA
            ,
            Manifest.permission.READ_EXTERNAL_STORAGE
            ,
            Manifest.permission.READ_CONTACTS
        )

        managePermissions = ManagePermissions(this, list, PermissionsRequestCode,this,packageName)
        managePermissionJava = ManagePermissionJava(this, list, PermissionsRequestCode,this,packageName)

        val button = findViewById(R.id.btnRequest) as Button
        button.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                if (managePermissionJava.checkPermissions()){
                    Log.e("SKB", "onCreate: ", )
                }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        x = managePermissionJava.processPermissionsResult(requestCode,permissions,grantResults)
    }
}