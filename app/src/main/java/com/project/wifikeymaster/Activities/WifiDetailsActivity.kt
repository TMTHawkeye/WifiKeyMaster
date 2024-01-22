package com.project.wifikeymaster.Activities

import android.content.Context
import android.net.ConnectivityManager
import android.net.DhcpInfo
import android.net.LinkProperties
import android.net.NetworkInfo
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.project.wifikeymaster.databinding.ActivityWifiDetailsBinding

class WifiDetailsActivity : AppCompatActivity() {
    lateinit var binding: ActivityWifiDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWifiDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.hide()

        //get current connected wifi information
        getConnectedWifiInfo()

        binding.backBtn.setOnClickListener{
            finish()
        }

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
            var ssid: String = ""

            ssid = wifiInfo.getSSID().toString()
            ssid = ssid.substring(1, ssid.length - 1)

//            if (connManager is ConnectivityManager) {
//                var link: LinkProperties =
//                    connManager.getLinkProperties(connManager.activeNetwork) as LinkProperties
//                binding.ipid.text = link.linkAddresses.get(3).toString()  //ip address
//
//            } else {
//                binding.ipid.text = "------------"
//            }

            val manager = this.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val dhcp: DhcpInfo = manager.dhcpInfo

            Log.e("NETWOOOOOOOOOOOORK",wifiInfo.toString())



            binding.ssid.text = "\"" + ssid + "\""  //ssid
            binding.bssid.text=wifiInfo.bssid.toString()        //bssid
            binding.speedid.text = wifiInfo.linkSpeed.toString() + " Mbps"      //speed
            binding.strenthid.text = wifiInfo.rssi.toString() + " dbm"      //strength
            binding.channelid.text = getChannel(wifiInfo.frequency).toString()      //channel
            binding.ipid.text=intToIp(dhcp.ipAddress)  //ip address
            binding.netmaskid.text=intToIp(dhcp.netmask)  //subnet mask
            binding.gatewayid.text=intToIp(dhcp.gateway)    //gateway
            binding.serverid.text=intToIp(dhcp.serverAddress)  //DHCP-Server
            binding.dnsid.text=intToIp(dhcp.dns1)   //dns1
            binding.macid.text = wifiInfo.macAddress.toString()     //mac address








        }
    }

    fun intToIp(i: Int): String? {
        return ((i and 0xFF).toString()+ "." + (i shr 8 and 0xFF)+ "." +(i shr 16 and 0xFF)+ "." +(i shr 24 and 0xFF))
    }

    fun getChannel(freq: Int): Int {
        if (freq == 2484) return 14
        return if (freq < 2484) (freq - 2407) / 5 else freq / 5 - 1000
    }

}