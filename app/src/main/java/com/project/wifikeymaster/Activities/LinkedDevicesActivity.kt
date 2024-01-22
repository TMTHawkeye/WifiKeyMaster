package com.project.wifikeymaster.Activities

import android.content.Context
import android.net.*
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.wifikeymaster.Adaptors.LinkedDevicesAdaptor
import com.project.wifikeymaster.ModelClasses.ConnectedDevicesModelClass
import com.project.wifikeymaster.databinding.ActivityLinkedDevicesBinding
import tej.wifitoolslib.DeviceFinder
import tej.wifitoolslib.interfaces.OnDeviceFoundListener
import tej.wifitoolslib.models.DeviceItem
import java.text.DecimalFormat
import java.util.concurrent.Executors


class LinkedDevicesActivity : AppCompatActivity() {
    var ssid: String = ""

    private var devices: ArrayList<ConnectedDevicesModelClass> = ArrayList()
    private var start: Long = 0
    private  var end:kotlin.Long = 0


    var mExecutorService = Executors.newSingleThreadExecutor()
    val mhandler = Handler(Looper.getMainLooper())

    lateinit var binding: ActivityLinkedDevicesBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLinkedDevicesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.hide()
        mExecutorService.execute {
            //get current connected wifi info
            getConnectedWifiInfo()
            //get connected devices list to current wifi network
            getConnectedDevices()

            mhandler.post {


                Log.d("TAG", "devicesList: " + devices.size.toString())
            }
        }

        binding.backBtn.setOnClickListener{
            finish()
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun getConnectedWifiInfo() {
        val connManager: ConnectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)!!
        if (networkInfo.isConnected()) {
            val wifiManager = getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo: WifiInfo = wifiManager.connectionInfo
            wifiInfo.getSSID()
            val nc: NetworkCapabilities =
                connManager.getNetworkCapabilities(connManager.getActiveNetwork())!!
            val downSpeed = getStringSizeLengthFile(nc.getLinkDownstreamBandwidthKbps().toLong())
            val upSpeed = getStringSizeLengthFile(nc.getLinkUpstreamBandwidthKbps().toLong())

            binding.downloadSpeedId.text = downSpeed.toString()   //set downloading speed
            binding.uploadSpeedId.text = upSpeed.toString()       //set uploading speed
            ssid = wifiInfo.getSSID().toString()
            binding.wifiNameId.text = ssid                        //set wifi name
            ssid = ssid.substring(1, ssid.length - 1)



//            if (connManager is ConnectivityManager) {
//                var link: LinkProperties =
//                    connManager.getLinkProperties(connManager.activeNetwork) as LinkProperties
//                Log.e("NETWOOOOOOOOOOOORK", link.linkAddresses.get(3).toString())
//                binding.ipAddressWifiId.text = link.linkAddresses.get(3).toString()
//            }

            val manager = this.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val dhcp: DhcpInfo = manager.dhcpInfo
            binding.ipAddressWifiId.text=intToIp(dhcp.ipAddress)   //set ip address of current wifi

        }
    }

    // integer ip address to standard format
    fun intToIp(i: Int): String? {
        return ((i and 0xFF).toString()+ "." + (i shr 8 and 0xFF)+ "." +(i shr 16 and 0xFF)+ "." +(i shr 24 and 0xFF))
    }

    //get standard size in kb,mb or gb from long value
    fun getStringSizeLengthFile(size: Long): String {
        val df = DecimalFormat("0")
        val sizeKb = 1024.0f
        val sizeMb = sizeKb * sizeKb
        val sizeGb = sizeMb * sizeKb
        val sizeTerra = sizeGb * sizeKb
        if (size < sizeMb) return (df.format((size / sizeKb).toDouble() * 1024)).toString() + " Kbs"
        else if (size < sizeGb) return df.format(
            (size / sizeMb).toDouble()
        ) + " Mbs" else if (size < sizeTerra) return df.format((size / sizeGb).toDouble()) + " Gbs"
        return ""
    }

    fun getConnectedDevices() {
        //device finder to get list of connected devices to wifi
        val devicesFinder = DeviceFinder(this@LinkedDevicesActivity, object : OnDeviceFoundListener {
            override fun onStart(deviceFinder: DeviceFinder) {
                start = System.currentTimeMillis()
            }

            override fun onFinished(deviceFinder: DeviceFinder, deviceItems: List<DeviceItem>) {
                binding.progressBar.visibility= View.GONE
                end = System.currentTimeMillis()

                for (deviceItem in deviceItems) {
                    devices.add(ConnectedDevicesModelClass(deviceItem.vendorName,deviceItem.ipAddress))
                    Log.d("TAG", "onFinished: "+deviceItem.vendorName.toString())
                }
                val time = (end - start) / 1000f
                Toast.makeText(
                    applicationContext, "Scan finished in " + time
                            + " seconds", Toast.LENGTH_SHORT
                ).show()

                //setting adaptor for connected devices list
                binding.filesRV.visibility=View.VISIBLE
                var adaptor=LinkedDevicesAdaptor(this@LinkedDevicesActivity,devices)
                binding.filesRV.layoutManager=LinearLayoutManager(this@LinkedDevicesActivity)
                binding.filesRV.adapter=adaptor
                binding.connectedDevicesId.text="Connected Devices : "+devices.size.toString()
//                arrayAdapter.notifyDataSetChanged()
            }

            override fun onFailed(deviceFinder: DeviceFinder, errorCode: Int) {}
        })
        devicesFinder.setTimeout(5000).start()
    }


}