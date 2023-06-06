package com.example.bttesting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.bttesting.databinding.DeviceslistSingleItemBinding

/* RIFATTORIZZARE ONBINDVIEWHOLDER E ONCREATEVIEWHOLDER
 */

/*RIFATTORIZZARE QUANDO SI FA' DATABINDING
 */

/*BINDING ADAPTERS SE SI DEVONO COMPIERE DEI CALCOLI COMPLICATI
    https://classroom.udacity.com/courses/ud9012/lessons/ee5a525f-0ba3-4d25-ba29-1fa1d6c567b8/concepts/de8e9f87-6cd0-4ff4-a20b-da5ffae7279c
 */

class DevicesListAdapter(val clickListener: DeviceDataListener): RecyclerView.Adapter<DevicesListAdapter.ViewHolder>() {

    companion object {
        var last_position = 0
    }

    var data = listOf<DeviceData>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(clickListener,item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: DeviceslistSingleItemBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(clickListener: DeviceDataListener, item: DeviceData) {
            val res = itemView.context.resources
            binding.device = item
            binding.singleItemTextview.text = item.deviceName
            binding.clickListener = clickListener
            binding.executePendingBindings()
            if(item.selected==true){binding.constraintLayout.backgroundTintList = ContextCompat.getColorStateList(itemView.context, R.color.orange)}
            else{binding.constraintLayout.backgroundTintList = ContextCompat.getColorStateList(itemView.context, R.color.white)}
        }

        //perchè non e' un metodo di una istanza ma della classe si può mettere in companion object
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = DeviceslistSingleItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}
//DiffUtil che e' il metodo piu' efficiente per aggiornare la lista
class DevicesListDiffCallBack: DiffUtil.ItemCallback<DeviceData>(){

    //per verificare se due elementi in posizione diversa rappresentano in realta' lo stesso elemento
    override fun areItemsTheSame(oldItem: DeviceData, newItem: DeviceData): Boolean {
        return oldItem.deviceHardwareAddress == newItem.deviceHardwareAddress
    }

    //per verificare se il contenuto è cambiato
    override fun areContentsTheSame(oldItem: DeviceData, newItem: DeviceData): Boolean {
       return oldItem == newItem
    }
}

//onClickListener
class DeviceDataListener(val clickListener: (btdev: DeviceData) -> Unit){
    fun onClick(device: DeviceData) = clickListener(device)
}
