package com.project.wifikeymaster.Activities

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.DhcpInfo
import android.net.LinkProperties
import android.net.NetworkInfo
import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.project.wifikeymaster.R
import com.project.wifikeymaster.databinding.ActivitySignalStrengthBinding
import java.util.concurrent.Executors

class SignalStrengthActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignalStrengthBinding
    var ssid: String = ""
    var mExecutorService = Executors.newSingleThreadExecutor()
    var mhandler = Handler(Looper.getMainLooper())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignalStrengthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.hide()

        //get current connected wifi information
        getConnectedWifiInfo()

    }

    fun getConnectedWifiInfo() {
        val connManager: ConnectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)!!
        if (networkInfo.isConnected()) {
            val wifiManager = getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo: WifiInfo = wifiManager.connectionInfo
            wifiInfo.getSSID()
            ssid = wifiInfo.getSSID().toString()
            ssid = ssid.substring(1, ssid.length - 1)
            setProgressBar(0f)

            val manager =
                this.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val dhcp: DhcpInfo = manager.dhcpInfo
            binding.ipId.text = intToIp(dhcp.ipAddress)

            binding.ssid.text = "\"" + ssid + "\""
            binding.signalId.text = wifiInfo.rssi.toString() + " dbm"
            binding.speedId.text = wifiInfo.linkSpeed.toString() + " Mbps"
            binding.macId.text = wifiInfo.macAddress.toString()
            binding.frequencyId.text = wifiInfo.frequency.toString() + " MHZ"
            binding.channelId.text = getChannel(wifiInfo.frequency).toString()

            var level = getsignalLevel(wifiInfo.rssi)
//            Toast.makeText(this, level.toString(), Toast.LENGTH_SHORT).show()
            when (level) {
                5 -> {
                    binding.wifiIcon.setImageResource(R.drawable.wifi_connected_svg)
                }
                4 -> {
                    binding.wifiIcon.setImageResource(R.drawable.connected_level4_svg)
                }
                3 -> {
                    binding.wifiIcon.setImageResource(R.drawable.connected_level4_svg)

                }
                2 -> {
                    binding.wifiIcon.setImageResource(R.drawable.connected_level2or3_png)

                }
                1 -> {
                    binding.wifiIcon.setImageResource(R.drawable.connected_level1or0_svg)

                }
                0 -> {
                    binding.wifiIcon.setImageResource(R.drawable.connected_level1or0_svg)
                }
            }
            //setting progress bar for the signal strength
            setProgressBar(level.toFloat())
        }

        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    fun intToIp(i: Int): String? {
        return ((i and 0xFF).toString() + "." + (i shr 8 and 0xFF) + "." + (i shr 16 and 0xFF) + "." + (i shr 24 and 0xFF))
    }

    fun getChannel(freq: Int): Int {
        if (freq == 2484) return 14
        return if (freq < 2484) (freq - 2407) / 5 else freq / 5 - 1000
    }

    fun getsignalLevel(item: Int): Int {
        val levell: Int = WifiManager.calculateSignalLevel(item, 5)
        return levell

    }

    fun setProgressBar(progres: Float) {
        binding.circularProgressBar.apply {
            // Set Progress Max
            progressMax = 5f
            //start angle
            startAngle = 0f

            mExecutorService.execute {
                runOnUiThread {
                    setProgressWithAnimation(progres, 2000)  //progress animation
                }
                mhandler.post {
                    var lvl = ((progres * 100) / 5)
                    runOnUiThread {
                        binding.percentSignal.text = lvl.toString() + " %"  //signal in percentage
                    }
                }
            }
        }
    }
}