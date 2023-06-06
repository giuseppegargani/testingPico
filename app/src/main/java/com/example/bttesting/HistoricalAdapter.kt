package com.example.bttesting

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.bttesting.databinding.HistoricalSingleItemBinding
import com.example.bttesting.databinding.RecentHistoricalSingleItemBinding
import java.io.File

/* Concetti interessanti:
    - Perche' usare ExecutePendingBinding su RecyclerView? https://stackoverflow.com/questions/53043412/android-why-use-executependingbindings-in-recyclerview
    - SetOnClick in onBindViewHolder: holder.itemView.setOnClickListener { Log.d("giuseppeListener", "SETONCLICKDIRETTO: dentro onBind e elemento ${item.name}") }
 */

/* SELEZIONE MULTIPLA E CHIUSURA MENU

 - Si deve fare una lista dei selezionati e se vuota chiude il menu!!!

 */

class HistoricalAdapter(val clickListener: HistoricalListener): RecyclerView.Adapter<HistoricalAdapter.ViewHolder>()  {

    //per modificare sfondo per il click veloce
    var selectedItemPosition: Int = -1

    //modifica la lista dei selezionati (per click lungo)
    var listaSelezionati: MutableList<Int> = mutableListOf()
    fun modificaListaSelezionati(elemento: Int){
        if (listaSelezionati.contains(elemento)){listaSelezionati.remove(elemento)}
        else{listaSelezionati.add(elemento)}
        Log.d("giuseppeLista", "LISTA ELEMENTI: ${listaSelezionati}")
    }

    var data = listOf<File>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val item = data[position]
        holder.bind(clickListener, item, position, selectedItemPosition, listaSelezionati)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: HistoricalSingleItemBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(clickListener: HistoricalListener, item: File, position: Int, selected:Int, selectedList: MutableList<Int>) {
            val res = itemView.context.resources
            val nome:String? = item.name
            binding.file=item
            binding.clickListener = clickListener
            binding.fileTextHistorical.text = convertiNome(nome!!)
            binding.executePendingBindings()
            //il testo con la data
            nome.let{ if(nome!!.length>15){binding.dateTextHistorical.text = "${nome.substring(12,14)}/${nome.substring(10,12)}/${nome.substring(6,10)}"} }
            if((selectedList.contains(position))||(selected==position)){
                binding.constraintHistoricalSingle.backgroundTintList = ContextCompat.getColorStateList(itemView.context, R.color.orange)
                binding.folderHistoricalSingle.imageTintList=ContextCompat.getColorStateList(itemView.context, R.color.azzurro)
            }
            else{binding.constraintHistoricalSingle.backgroundTintList = ContextCompat.getColorStateList(itemView.context, R.color.white)
                binding.folderHistoricalSingle.imageTintList=ContextCompat.getColorStateList(itemView.context, R.color.orange)
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = HistoricalSingleItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

        //si puo' mettere anche un bindingAdapter (funzione invocata direttamente in databinding)
        fun convertiNome(stringa: String): String{
            var nome = ""
            if(stringa.length>21){ nome = stringa.drop(18).dropLast(4) }
            else{nome = stringa.dropLast(4)}
            return nome
        }
    }
}

class RecentHistoricalAdapter(val clickListener: HistoricalListener): RecyclerView.Adapter<RecentHistoricalAdapter.ViewHolder>()  {

    //per modificare sfondo per il click veloce
    var selectedItemPosition: Int = -1

    //modifica la lista dei selezionati (per click lungo)
    var listaSelezionati: MutableList<Int> = mutableListOf()
    fun modificaListaSelezionati(elemento: Int){
        if (listaSelezionati.contains(elemento)){listaSelezionati.remove(elemento)}
        else{listaSelezionati.add(elemento)}
        Log.d("giuseppeLista", "LISTA ELEMENTI: ${listaSelezionati}")
    }

    var data = listOf<File>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val item = data[position]
        holder.bind(clickListener, item, position, selectedItemPosition, listaSelezionati)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: RecentHistoricalSingleItemBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(clickListener: HistoricalListener, item: File, position: Int, selected:Int, selectedList: MutableList<Int>) {
            val res = itemView.context.resources
            val nome:String? = item.name
            binding.file=item
            binding.clickListener = clickListener
            binding.fileTextHistorical.text = convertiNome(nome!!)
            binding.executePendingBindings()
            //il testo con la data
            nome.let{ if(nome!!.length>15){binding.dateTextHistorical.text = "${nome.substring(12,14)}/${nome.substring(10,12)}/${nome.substring(6,10)}"} }
            if((selectedList.contains(position))||(selected==position)){
                binding.constraintHistoricalSingle.backgroundTintList = ContextCompat.getColorStateList(itemView.context, R.color.orange)
                binding.folderHistoricalSingle.imageTintList=ContextCompat.getColorStateList(itemView.context, R.color.azzurro)
            }
            else{binding.constraintHistoricalSingle.backgroundTintList = ContextCompat.getColorStateList(itemView.context, R.color.white)
                binding.folderHistoricalSingle.imageTintList=ContextCompat.getColorStateList(itemView.context, R.color.orange)
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RecentHistoricalSingleItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

        //si puo' mettere anche un bindingAdapter (funzione invocata direttamente in databinding)
        fun convertiNome(stringa: String): String{
            var nome = ""
            if(stringa.length>21){ nome = stringa.drop(18).dropLast(4) }
            else{nome = stringa.dropLast(4)}
            return nome
        }
    }
}

//clickListener e' una lambda che ha dei parametri e in questo caso le lambda sono due e una restituisce un valore (cioe' true or false)
class HistoricalListener(val clickListener: (file: File) -> Unit, val longClickListener: (file: File)->Boolean) {
    fun onClick(file: File) = clickListener(file)
    fun onLongClick(file:File) = longClickListener(file)
}