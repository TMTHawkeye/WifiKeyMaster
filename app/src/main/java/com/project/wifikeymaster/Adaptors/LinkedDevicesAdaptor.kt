package com.project.wifikeymaster.Adaptors

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.wifikeymaster.Activities.LinkedDevicesActivity
import com.project.wifikeymaster.ModelClasses.ConnectedDevicesModelClass
import com.project.wifikeymaster.R
import com.project.wifikeymaster.databinding.RowForRecyclerViewBinding
import com.project.wifikeymaster.databinding.RowForSavedListBinding

class LinkedDevicesAdaptor(
    var contextLinkedDevicesActivity: LinkedDevicesActivity,
    var devicesList: ArrayList<ConnectedDevicesModelClass>
) : RecyclerView.Adapter<LinkedDevicesAdaptor.viewHolder>() {

    lateinit var binding:RowForRecyclerViewBinding
    class viewHolder(var binding: RowForRecyclerViewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        binding= RowForRecyclerViewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return viewHolder(binding)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {

        holder.binding.wifiIcon.setImageResource(R.drawable.wifi_connected_svg)  //icon of connected device
        holder.binding.fileNameTV.text=devicesList.get(position).vendorName     //name of connected device
        holder.binding.status.text=devicesList.get(position).ipAddress          //ip address of connected wifi
    }

    override fun getItemCount(): Int {
        return devicesList.size
    }
}