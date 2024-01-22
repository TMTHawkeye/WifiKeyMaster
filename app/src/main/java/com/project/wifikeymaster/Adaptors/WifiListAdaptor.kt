package com.project.wifikeymaster.Adaptors

import android.app.Dialog
import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.project.wifikeymaster.ModelClasses.SavedDetailsModelClass
import com.project.wifikeymaster.R
import com.project.wifikeymaster.databinding.RowForRecyclerViewBinding
import io.paperdb.Paper


class WifiListAdaptor(
    var contextWifi: Context,
    var list: ArrayList<ScanResult>,
    var currentSSID: String
) : RecyclerView.Adapter<WifiListAdaptor.viewHolder>() {

    lateinit var binding: RowForRecyclerViewBinding

    class viewHolder(var binding: RowForRecyclerViewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        binding =
            RowForRecyclerViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return viewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.binding.fileNameTV.text = list.get(position).SSID.toString() //wifi network name
        var level = getsignalLevel(list.get(position))  //get signal strength out of 5 for holder item
//        Log.d("TAG", "onBindViewHolder: " + currentSSID + " " + list.get(position).SSID)
        val wifi = contextWifi.getSystemService(WIFI_SERVICE) as WifiManager?

        if (wifi!!.isWifiEnabled) {              //setting content for position 0 item i.e. connected wifi

            if (position == 0) {
                holder.binding.status.text = "Connected"
                holder.binding.moreOptions.visibility = View.VISIBLE

                holder.binding.moreOptions.setOnClickListener {
                    contextWifi.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS));

                }

                when (level) {
                    5 -> {
                        holder.binding.wifiIcon.setImageResource(R.drawable.wifi_connected_svg)
                    }
                    4 -> {
                        holder.binding.wifiIcon.setImageResource(R.drawable.connected_level4_svg)
                    }
                    3 -> {
                        holder.binding.wifiIcon.setImageResource(R.drawable.connected_level4_svg)

                    }
                    2 -> {
                        holder.binding.wifiIcon.setImageResource(R.drawable.connected_level2or3_png)

                    }
                    1 -> {
                        holder.binding.wifiIcon.setImageResource(R.drawable.connected_level1or0_svg)

                    }
                    0 -> {
                        holder.binding.wifiIcon.setImageResource(R.drawable.connected_level1or0_svg)
                    }
                }

            } else {
                setIcon(holder, level)
            }
        }
        else{                           //setting content for remaining items in the list
            setIcon(holder, level)

        }

        holder.itemView.setOnClickListener {
            //show password dialog for selected item in list
            showDialogBox(holder.binding.fileNameTV.text.toString(), position)
        }


    }

    //set drawable according to the signal strength
    private fun setIcon(holder: viewHolder, level: Int) {
        holder.binding.status.text = ""
        holder.binding.moreOptions.visibility = View.GONE
        when (level) {
            5 -> {
                holder.binding.wifiIcon.setImageResource(R.drawable.locked_wifi_svg)
            }
            4 -> {
                holder.binding.wifiIcon.setImageResource(R.drawable.locked_level_4_svg)
            }
            3 -> {
                holder.binding.wifiIcon.setImageResource(R.drawable.locked_level_4_svg)

            }
            2 -> {
                holder.binding.wifiIcon.setImageResource(R.drawable.locked_level3or2_svg)

            }
            1 -> {
                holder.binding.wifiIcon.setImageResource(R.drawable.locked_level1or0_svg)

            }
            0 -> {
                holder.binding.wifiIcon.setImageResource(R.drawable.locked_level1or0_svg)

            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun getsignalLevel(item: ScanResult): Int {
        var scanResult: ScanResult = item
        val levell: Int = WifiManager.calculateSignalLevel(scanResult.level, 5)
        return levell

    }

    fun showDialogBox(nameNetwork: String, position: Int) {
        val alert = Dialog(contextWifi)
        alert.setCancelable(true)
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alert.setContentView(R.layout.connect_to_network_dialog)
        alert.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alert.getWindow()!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        );

        var name = alert.findViewById<TextView>(R.id.network_name_id)
        var pass = alert.findViewById<EditText>(R.id.editText_password)
        var save = alert.findViewById<CardView>(R.id.save_password_card)
        var cancel = alert.findViewById<CardView>(R.id.cancel)
        var scan = alert.findViewById<ImageView>(R.id.qr_scan_id)

        name.text = nameNetwork


        cancel.setOnClickListener {
            alert.dismiss()
        }

        scan.setOnClickListener{
            //wifi intent to settings
            contextWifi.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS));
        }

        save.setOnClickListener {
            //save wifi information in paper database for saved password fragment
            addToSaved(list.get(position), position, pass)
            alert.dismiss()
        }
        alert.show()
    }

    private fun addToSaved(item: ScanResult, position: Int, pass: EditText) {
        var savedPasswords =
            Paper.book().read("saved_password_db", ArrayList<SavedDetailsModelClass>())

        for (i in savedPasswords!!.indices) {
            if (savedPasswords.get(i).networkDetails.SSID.equals(item.SSID)) {
                savedPasswords.remove(savedPasswords.get(i))
                break
            }
        }
        savedPasswords!!.add(SavedDetailsModelClass(list.get(position), pass.text.toString()))
        Paper.book().write("saved_password_db", savedPasswords)
        Log.d("TAG", "showDialogBox: " + savedPasswords.size.toString())
    }


}