package com.example.bttesting

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.bttesting.databinding.DownloadingChartsBinding
import com.example.bttesting.utils.NavigToDialog
import com.example.bttesting.viewModels.BluetoothViewModel

class DownloadChartDialogue: DialogFragment() {

    val btViewModel: BluetoothViewModel by activityViewModels()

    //crea un dialogo e restituisce anche creando da una altra classe
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogo = Dialog(requireContext())
        dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogo.setCancelable(false)
        dialogo.setCanceledOnTouchOutside(false)
        val binding = DataBindingUtil.inflate<DownloadingChartsBinding>(LayoutInflater.from(context), R.layout.downloading_charts, null,false)
        dialogo.setContentView(binding.root)
        dialogo.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val image = binding.downloadBarDb.drawable
        image.setLevel(0)

        btViewModel.complScarico.observe(this, Observer {
            //quando il primo numero torna a zero togli il dialog Fragment senza considerare il secondo numero
            Log.d("giuseppeErrorDialog", "DIALOG FRAGMENT ${it}")

            if (it.first == NavigToDialog.NO_DIALOG) { dialogo.dismiss() }
            else if(it.first==NavigToDialog.RIMUOVI) {
                //findNavController().navigateUp()
                this.findNavController().navigate(R.id.liveDataFragment)
                btViewModel.jobSaveDb.cancel() //occorre verificare le condizioni
                findNavController().navigate(LiveDataFragmentDirections.actionLiveDataFragmentToErrorDownloadDialog())
            }
            else {
                when(it.second){
                    0->image.setLevel(1250)
                    1->image.setLevel(2500)
                    2->image.setLevel(3750)
                    3->image.setLevel(5000)
                    4->image.setLevel(6250)
                    5->image.setLevel(7500)
                    6->image.setLevel(8750)
                    7->image.setLevel(10000)
                    else->image.setLevel(7)
                }
            }
        })

        dialogo.show()
        return dialogo
    }
}