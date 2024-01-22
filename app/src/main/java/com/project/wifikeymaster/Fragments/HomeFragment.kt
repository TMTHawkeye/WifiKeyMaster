package com.project.wifikeymaster.Fragments

import android.Manifest
import android.app.AlertDialog
import android.bluetooth.BluetoothGattCharacteristic
import android.content.*
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.motion.widget.Debug.getLocation
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.chartcore.common.ChartTypes
import com.github.chartcore.data.chart.ChartCoreModel
import com.github.chartcore.data.chart.ChartData
import com.github.chartcore.data.dataset.ChartNumberDataset
import com.github.chartcore.data.option.ChartOptions
import com.github.chartcore.data.option.elements.Elements
import com.github.chartcore.data.option.plugin.BackgroundColor
import com.github.chartcore.data.option.plugin.Plugin
import com.github.chartcore.data.option.plugin.Tooltip
import com.github.chartcore.view.ChartCoreView
import com.project.wifikeymaster.Activities.*
import com.project.wifikeymaster.BuildConfig
import com.project.wifikeymaster.R
import com.project.wifikeymaster.databinding.FragmentHomeBinding
import io.paperdb.Paper
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {
    var currentSSID = ""
    var resultList = ArrayList<ScanResult>()
    lateinit var wifiManager: WifiManager
    lateinit var list: ArrayList<ScanResult>
    val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            if (ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermission()
                return
            }
            resultList = wifiManager.scanResults as ArrayList<ScanResult>

        }
    }


    lateinit var binding: FragmentHomeBinding

    var gps_enabled = false
    var network_enabled = false

    companion object {
        private val ARG_PARAM1 = "param1"

        @JvmStatic
        fun newInstance(param1: String) = HomeFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_PARAM1, param1)
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
// Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        //open 3D drwaer
        openNavDrawer()
        //set content for 3D drawer
        setNavContent()

        //initialize wifi manager
        wifiManager =
            requireContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        alertLocationDialog()

        val lm: LocationManager =
            requireContext().getSystemService(LOCATION_SERVICE) as LocationManager


        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
        }

        //if ocation is turned on the scan for near by wifi is started in startScanning method
        if (gps_enabled && network_enabled) {
            if (startScanning()) {
                //line chart is set for signal strength of nearby wifi
                setChainChart(binding.chartCore0)
            }
        } else {
            Toast.makeText(requireContext(), "Please turn on Location!", Toast.LENGTH_SHORT).show()
        }

        binding.cardList.setOnClickListener {
            val lm: LocationManager =
                requireContext().getSystemService(LOCATION_SERVICE) as LocationManager


            try {
                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
            } catch (ex: Exception) {
            }

            try {
                network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            } catch (ex: Exception) {
            }

            if (gps_enabled && network_enabled) {
                startActivity(
                    Intent(
                        requireContext(),
                        WifiListActivity::class.java
                    ).putExtra("currentSSID", currentSSID)
                )
            } else {
                Toast.makeText(requireContext(), "Please turn on Location!", Toast.LENGTH_SHORT)
                    .show()
            }

        }

        //start scanning again and set chart values again when screen refreshed
        binding.pullToRefresh.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            if (startScanning()) {
                setChainChart(binding.chartCore0)
            }
            binding.pullToRefresh.setRefreshing(false)
        })

        binding.cardSignalStrength.setOnClickListener {
            startActivity(Intent(requireContext(), SignalStrengthActivity::class.java))

        }

        binding.cardLinkedDevices.setOnClickListener {
            startActivity(Intent(requireContext(), LinkedDevicesActivity::class.java))

        }

        binding.cardGeneratePassword.setOnClickListener {
            startActivity(Intent(requireContext(), GeneratePasswordActivity::class.java))

        }
        binding.cardWifidetails.setOnClickListener {
            startActivity(
                Intent(requireContext(), WifiDetailsActivity::class.java)
                    .putExtra("currentSSID", currentSSID)
            )

        }

        return binding.root
    }


    private fun openNavDrawer() {
        binding.menuIcon.setOnClickListener(View.OnClickListener {
            binding.drawer.openDrawer(Gravity.LEFT)
            binding.drawer.setViewRotation(GravityCompat.START, 15f)
        })
    }

    private fun setNavContent() {
        binding.navView.setNavigationItemSelectedListener(
            { item ->
                when (item.itemId) {
                    R.id.rate_us -> try {
                        val marketUri = Uri.parse("market://details?id=packageName"+requireContext().packageName)
                        val marketIntent = Intent(Intent.ACTION_VIEW, marketUri)
                        startActivity(marketIntent)
                    } catch (e: ActivityNotFoundException) {
                        val marketUri =
                            Uri.parse("https://play.google.com/store/apps/dev?id="+requireContext().packageName)
                        val marketIntent = Intent(Intent.ACTION_VIEW, marketUri)
                        startActivity(marketIntent)
                    }
                    R.id.share -> try {
                        val shareIntent = Intent(Intent.ACTION_SEND)
                        shareIntent.type = "text/plain"
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Wifi Key Master")
                        var shareMessage = "\nLet me recommend you this application\n\n"
                        shareMessage =
                            """
                    ${shareMessage}https://play.google.com/store/apps/dev?id=${BuildConfig.APPLICATION_ID}
                  
                    """.trimIndent()
                        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                        startActivity(Intent.createChooser(shareIntent, "choose one"))
                    } catch (e: Exception) {
                    }
                    R.id.feedback -> {
                        val feedbackIntent = Intent(Intent.ACTION_SEND)
                        val recipients = arrayOf("smartianztech@gmail.com")
                        feedbackIntent.putExtra(Intent.EXTRA_EMAIL, recipients)
                        feedbackIntent.putExtra(
                            Intent.EXTRA_SUBJECT,
                            "Feedback : " + R.string.app_name
                        )
                        feedbackIntent.type = "text/html"
                        feedbackIntent.setPackage("com.google.android.gm")
                        startActivity(Intent.createChooser(feedbackIntent, "Send mail"))
                    }
                    R.id.privacy -> {
                        val uri =
                            Uri.parse("https://smartianztech.blogspot.com/2023/01/welcome-to-smartianz-tech-inc.html")
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        startActivity(intent)
                    }
                    R.id.moreApps -> {
                        val moreAppUri =
                            Uri.parse("https://play.google.com/store/apps/developer?id=Smartianz+Tech")
                        val moreApp = Intent(Intent.ACTION_VIEW, moreAppUri)
                        startActivity(moreApp)
                    }
                    else -> {}
                }
                binding.drawer.closeDrawer(GravityCompat.START)
                true
            })
    }

    //this function check whether location is on or not, because location is required for this application
    fun alertLocationDialog() {
        val lm: LocationManager =
            requireContext().getSystemService(LOCATION_SERVICE) as LocationManager


        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
        }

        //if location is not turned on
        if (!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder(requireContext())
                .setMessage("Please turn on location to search for nearby networks.")
                .setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                    override fun onClick(paramDialogInterface: DialogInterface?, paramInt: Int) {
                        requireContext().startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                        startActivity(Intent(requireContext(), WifiListActivity::class.java))
                    }
                })
                .setNegativeButton("No", null)
                .show()
        }
    }

    fun startScanning(): Boolean {
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(
            broadcastReceiver,
            IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        )
        wifiManager.startScan()
//        Handler().postDelayed({
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(requireContext(), "Enable location", Toast.LENGTH_SHORT).show()
            return false
        } else {
            list = wifiManager.scanResults as ArrayList
            Log.d("TAG", "startScanning: " + list.toString())
            currentSSID = getConnectedWifiInfo()
            for (i in list.indices) {
//                Log.d("TAG", "startScanning: " + list.get(i).capabilities.toString())

                if (list.get(i).SSID.equals(currentSSID)) {
                    if (!list.get(i).SSID.isEmpty()) {
                        swap(list, 0, i)
                    }
                }
            }

            for (i in list.indices) {
                if (list.get(i).SSID.isEmpty()) {
                    list.removeAt(i)
                    break
                }
            }

            Paper.book().write("wifiList", list)


        }
        return true
//        }, 5000)
    }


    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE
            ),
            BluetoothGattCharacteristic.PERMISSION_READ
        )
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
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setMessage("You need to allow access to both the permissions")
            .setPositiveButton("OK", onClickListener)
            .setNegativeButton("Cancel", onClickListener)
            .create()
            .show()

    }

    fun getConnectedWifiInfo(): String {
        var ssid: String = ""
        val connManager: ConnectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)!!
        if (networkInfo.isConnected()) {
            val wifiManager = requireContext().getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo: WifiInfo = wifiManager.connectionInfo
            wifiInfo.getSSID()
            ssid = wifiInfo.getSSID().toString()
            ssid = ssid.substring(1, ssid.length - 1)
            Log.d("TAG", "getConnectedWifiInfo: " + ssid)
        }
        return ssid
    }

    fun swap(list: MutableList<ScanResult>, firstPosition: Int, secondPosition: Int) {
        Collections.swap(list, firstPosition, secondPosition)
    }

    fun getsignalLevel(item: ScanResult): Int {
        var scanResult: ScanResult = item
        val levell: Int = WifiManager.calculateSignalLevel(scanResult.level, 5)
        return levell

    }

    fun setChainChart(chartCore: ChartCoreView) {
        chartCore.visibility = View.VISIBLE

        //set default data to chart
        var labelsList0 = kotlin.collections.ArrayList<String>()
        var listDouble0 = kotlin.collections.ArrayList<Double>()

        labelsList0.add("No Data")
        listDouble0.add(0f.toDouble())

        var coreData0 = ChartData()
            .addDataset(
                ChartNumberDataset()
                    .data(listDouble0)
                    .label("Wifi Netwrok - Signals Strength")
                    .offset(10)
                    .borderColor("#8758E7")
            )
            .labels(labelsList0)

        var chartOptions0 = ChartOptions()
            .plugin(
                Plugin()
                    .tooltip(
                        Tooltip(true)
                    )
                    .customCanvasBackgroundColor(BackgroundColor("#8758E7"))
            )
            .elements(
                Elements()
                    .line(
                        com.github.chartcore.data.option.elements.Line().tension(0.05f)
                            .borderColor("#8758E7")
                    )
            )


        var chartModel0 = ChartCoreModel()
            .type(ChartTypes.LINE)
            .data(coreData0)
            .options(chartOptions0)

        chartCore.draw(chartModel0)


        //set data to chart

        var labelsList = kotlin.collections.ArrayList<String>()
        var listDouble = kotlin.collections.ArrayList<Double>()

        for (i in list) {
            labelsList.add(i.SSID.toString())
            var level = getsignalLevel(i)
            listDouble.add(level.toDouble())
        }

        var coreData = ChartData()
            .addDataset(
                ChartNumberDataset()
                    .data(listDouble)
                    .label("Wifi Netwrok - Signals Strength")
                    .offset(10)
                    .borderColor("#8758E7")
            )
            .labels(labelsList)

        var chartOptions = ChartOptions()
            .plugin(
                Plugin()
                    .tooltip(
                        Tooltip(true)
                    )
                    .customCanvasBackgroundColor(BackgroundColor("#8758E7"))
            )
            .elements(
                Elements()
                    .line(
                        com.github.chartcore.data.option.elements.Line().tension(0.05f)
                            .borderColor("#8758E7")
                    )
            )

        var chartModel = ChartCoreModel()
            .type(ChartTypes.LINE)
            .data(coreData)
            .options(chartOptions)

        chartCore.draw(chartModel)


    }

}
