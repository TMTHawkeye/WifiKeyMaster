package com.project.wifikeymaster.Activities

import android.Manifest
import android.bluetooth.BluetoothGattCharacteristic
import android.content.*
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.wifikeymaster.Adaptors.WifiListAdaptor
import com.project.wifikeymaster.databinding.ActivityWifiListBinding
import io.paperdb.Paper
import java.util.*
import kotlin.collections.ArrayList

class WifiListActivity : AppCompatActivity() {


    lateinit var binding: ActivityWifiListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWifiListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.hide()

        //current connected wifi SSID is obtained
        var currentSSID = intent.getStringExtra("currentSSID")

        //list of all the nearby wifi is obtained stored in paper database
        var list = Paper.book().read("wifiList", ArrayList<ScanResult>())

        if (list!!.size == 0) {
            binding.nofileIV.visibility = View.VISIBLE
            binding.filesRV.visibility = View.GONE
        } else {
            binding.nofileIV.visibility = View.GONE
            binding.filesRV.visibility = View.VISIBLE

            //set adaptor for wifi list
            var adaptor = WifiListAdaptor(this, list!!, currentSSID!!)
            binding.filesRV.visibility = View.VISIBLE
            binding.filesRV.layoutManager = LinearLayoutManager(this)
            binding.filesRV.adapter = adaptor
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

    }


}