package com.project.wifikeymaster.Activities

import android.Manifest
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.project.wifikeymaster.databinding.ActivitySplashScreenBinding

class SplashScreenActivity : AppCompatActivity() {
    lateinit var binding: ActivitySplashScreenBinding
    var preferences: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)


        window.statusBarColor= Color.WHITE
        preferences = getSharedPreferences("policy", Context.MODE_PRIVATE)
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        if (preferences!!.getBoolean("accept", false)) {
            startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
        } else {
            if (!mcheckPermission()) {
                requestPermission()
            }

        }

        binding.getStart.setOnClickListener {
            preferences!!.edit().putBoolean("accept", true).apply()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
    private fun mcheckPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val result1 = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        val result2 = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.ACCESS_WIFI_STATE
        )
        val result3 = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.CHANGE_WIFI_STATE
        )

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE
            ),
            BluetoothGattCharacteristic.PERMISSION_READ
        )
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int,data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2296) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT)
                        .show()                } else {
                    Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == BluetoothGattCharacteristic.PERMISSION_READ) {
            if (grantResults.size > 0) {
                val locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (locationAccepted) {
                } else {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        showMessageOKCancel { _: DialogInterface?, _: Int ->
                            requestPermissions(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_WIFI_STATE,
                                    Manifest.permission.CHANGE_WIFI_STATE,
                                ),
                                BluetoothGattCharacteristic.PERMISSION_READ
                            )
                        }
                    }
                }
            }
        }
    }

    private fun showMessageOKCancel(onClickListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this@SplashScreenActivity)
            .setMessage("You need to allow access to both the permissions")
            .setPositiveButton("OK", onClickListener)
            .setNegativeButton("Cancel", onClickListener)
            .create()
            .show()

    }
}