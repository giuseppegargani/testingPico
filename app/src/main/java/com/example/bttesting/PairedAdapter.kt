package com.example.bttesting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bttesting.databinding.PairlistSingleItemBinding

class PairedAdapter(val clickListener: PairListener): RecyclerView.Adapter<PairedAdapter.ViewHolder>() {

    var data = listOf<DeviceData>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(clickListener, item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: PairlistSingleItemBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(clickListener: PairListener, item: DeviceData) {
            val res = itemView.context.resources
            binding.device = item
            binding.singleDeviceUnpairTextView.text = item.deviceName.toString()
            binding.clickListener = clickListener
            binding.executePendingBindings()
            /*if (item.paired == false) {
                itemView.setBackgroundResource(R.color.orange)
            } else {
                itemView.setBackgroundResource(R.color.full_trasparent)
            }*/
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = PairlistSingleItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}
class PairListener(val clickListener: (btdev: DeviceData) -> Unit){
    fun onClick(device: DeviceData) = clickListener(device)
}