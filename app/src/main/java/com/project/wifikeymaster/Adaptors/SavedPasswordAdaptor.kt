package com.project.wifikeymaster.Adaptors

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.text.Editable
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.project.wifikeymaster.Fragments.SavePasswordFragment
import com.project.wifikeymaster.Interface.SavedPasswordInterface
import com.project.wifikeymaster.ModelClasses.SavedDetailsModelClass
import com.project.wifikeymaster.R
import com.project.wifikeymaster.databinding.RowForSavedListBinding
import io.paperdb.Paper

class SavedPasswordAdaptor(
    var contextSavedPass: FragmentActivity,
    var list: ArrayList<SavedDetailsModelClass>,
    var savePasswordFragment: SavePasswordFragment
) : RecyclerView.Adapter<SavedPasswordAdaptor.viewHolder>() {

    lateinit var binding: RowForSavedListBinding
    lateinit var connectedNetwork:String
    lateinit var savedInterface:SavedPasswordInterface

    class viewHolder(var binding: RowForSavedListBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        binding = RowForSavedListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        connectedNetwork=getConnectedWifiInfo()
        return viewHolder(binding)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        Log.d("TAG", "pas: " + list.get(position).networkDetails.SSID.toString()+" "+connectedNetwork)
        var level = getsignalLevel(list.get(position).networkDetails)  //obtain signal level out of 5

        holder.binding.fileNameTV.text = list.get(position).networkDetails.SSID.toString()  //saved wifi network name
        holder.binding.editPassId.setOnClickListener {
            showDialogBox(holder.binding.fileNameTV.text.toString(), position)
        }
        holder.binding.status.text="Password : "+list.get(position).password   //set saved password on textview


        //set content for connected wifi
        if(connectedNetwork.equals(list.get(position).networkDetails.SSID)){
            when (level) {
                5->{
                    holder.binding.wifiIcon.setImageResource(R.drawable.wifi_connected_svg)
                }
                4->{
                    holder.binding.wifiIcon.setImageResource(R.drawable.connected_level4_svg)
                }
                3->{
                    holder.binding.wifiIcon.setImageResource(R.drawable.connected_level2or3_png)

                }
                2->{
                    holder.binding.wifiIcon.setImageResource(R.drawable.connected_level2or3_png)

                }
                1->{
                    holder.binding.wifiIcon.setImageResource(R.drawable.connected_level1or0_svg)

                }
                0->{
                    holder.binding.wifiIcon.setImageResource(R.drawable.connected_level1or0_svg)

                }
            }
        }
        else{  //set content for locked wifi networks
            setIcon(holder,level)
        }

        holder.binding.deleteId.setOnClickListener{
            showDeleteAlertDialog(position)
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

    private fun setIcon(holder: viewHolder, level: Int) {
        when (level) {
            5->{
                holder.binding.wifiIcon.setImageResource(R.drawable.locked_wifi_svg)
            }
            4->{
                holder.binding.wifiIcon.setImageResource(R.drawable.locked_level_4_svg)
            }
            3->{
                holder.binding.wifiIcon.setImageResource(R.drawable.locked_level3or2_svg)

            }
            2->{
                holder.binding.wifiIcon.setImageResource(R.drawable.locked_level3or2_svg)

            }
            1->{
                holder.binding.wifiIcon.setImageResource(R.drawable.locked_level1or0_svg)

            }
            0->{
                holder.binding.wifiIcon.setImageResource(R.drawable.locked_level1or0_svg)

            }
        }
    }



    fun showDialogBox(nameNetwork: String, position: Int) {
        val alert = Dialog(contextSavedPass)
        alert.setCancelable(false)
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

        name.text = nameNetwork
        var text =
            Editable.Factory.getInstance().newEditable(list.get(position).password.toString())
        pass.text = text



        cancel.setOnClickListener {
            alert.dismiss()
        }

        save.setOnClickListener {
            var savedPasswords =
                Paper.book().read("saved_password_db", ArrayList<SavedDetailsModelClass>())
            savedPasswords!!.get(position).password=pass.text.toString()
            Paper.book().write("saved_password_db",savedPasswords)
            alert.dismiss()
            notifyItemChanged(position)
            notifyDataSetChanged()
        }
        alert.show()

    }

    private fun deleteFromSaved(position: Int) {
        var savedPasswords =
            Paper.book().read("saved_password_db", ArrayList<SavedDetailsModelClass>())
        savedPasswords!!.remove(savedPasswords.removeAt(position))
        Paper.book().write("saved_password_db",savedPasswords)
        list.remove(list.get(position))

        if(list.size==0){
            savedInterface=savePasswordFragment
            savedInterface.noFileImageView(true)
        }
        notifyDataSetChanged()
    }

    fun getConnectedWifiInfo():String{
        var ssid :String=""
        val connManager: ConnectivityManager =
            contextSavedPass.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)!!
        if (networkInfo.isConnected()) {
            val wifiManager =contextSavedPass.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo: WifiInfo = wifiManager.connectionInfo
            wifiInfo.getSSID()
            ssid =wifiInfo.getSSID().toString()
            ssid=ssid.substring(1,ssid.length-1)
            Log.d("TAG", "getConnectedWifiInfo: "+ssid)
        }
        return ssid
    }

    //dialog before deleting saved wifi info from list
    fun showDeleteAlertDialog(position: Int) {
        val builder1: AlertDialog.Builder = AlertDialog.Builder(contextSavedPass)
        builder1.setMessage("Are you sure you want to delete this item?")
        builder1.setCancelable(true)

        builder1.setPositiveButton(
            "Yes"
        ) { dialog, _ ->
            deleteFromSaved(position)

        }

        builder1.setNegativeButton(
            "No"
        ) { dialog, id -> dialog.cancel() }

        val alert11: AlertDialog = builder1.create()
        alert11.show()
    }

}