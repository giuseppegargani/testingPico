package com.example.bttesting

import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.example.bttesting.databinding.PairFragmentBinding
import com.example.bttesting.viewModels.BluetoothViewModel

/* mettere il Databinding su Alertdialog!!
    https://stackoverflow.com/questions/34967868/how-to-use-data-binding-in-dialog
 */

class PairFragment : Fragment() {

    val btViewModel: BluetoothViewModel by activityViewModels()

    private var noDeviceFound: NoDeviceFound = NoDeviceFound()
    var mAdapter: BluetoothAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: PairFragmentBinding = PairFragmentBinding.inflate(inflater, container, false)

        mAdapter = BluetoothAdapter.getDefaultAdapter()

        btViewModel.inizioRicerca()

        //qui vengono messi i clicklistener METTERE LISTENER DIVERSI
        val pairedAdapter= PairedAdapter(PairListener {
            if(it.paired){ unpairConfirmDialog(it) }
            else{btViewModel.pair(it)}
        })
        val unpairedAdapter= PairedAdapter(PairListener {
            if(it.paired){ unpairConfirmDialog(it) }
            else{btViewModel.pair(it)}
        })
        binding.recyclerViewPair.adapter = pairedAdapter
        binding.recyclerViewUnpair.adapter = unpairedAdapter

        //questi sono i listeners del menu laterale
        binding.patientLogoPair.setOnClickListener {
            this.findNavController().navigate(R.id.connectFragment)
        }
        binding.historicalLogoPair.setOnClickListener {
            this.findNavController().navigate(R.id.historicalFragment)
        }
        binding.settingsLogoPair.setOnClickListener {
            //this.findNavController().navigate(R.id.settingsFragment)
        }
        binding.helpLogoPair.setOnClickListener {
            this.findNavController().navigate(R.id.helpFragment)
        }

        binding.backBtnPair.setOnClickListener {
            this.findNavController().navigate(R.id.settingsFragment)
        }

        //aggiorna la lista
        btViewModel.lista.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {
                val pairedList = it.filter { dev -> dev.paired }
                val unpairedList = it.filter { dev -> !dev.paired }
                pairedAdapter.data = pairedList
                unpairedAdapter.data = unpairedList
            }
        })

        btViewModel.inRicerca.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
                if (it) {
                    binding.updateIconPair.setImageResource(R.drawable.ic_baseline_update_100_during)
                } else {
                    binding.updateIconPair.setImageResource(R.drawable.ic_baseline_update_100)
                    //se quando termina la ricerca il data e' vuoto - Si dovrebbe mettere come lista - ma si puo' mettere anche cosi
                    if ((pairedAdapter.data.isEmpty())&&(unpairedAdapter.data.isEmpty())) {
                        //noRedLineDialog()
                        noDeviceFound.showNoDeviceDialogue( this)
                    } //da mettere alertdialog personalizzato
                    else {
                        //RIDONDANTE!!!! si puo' anche togliere!!!!!!!!!!!
                        val dimen: String = (unpairedAdapter.data.filter { !it.paired }.size).toString();
                        //binding.unpairedTextviewPair.text = "FOUND $dimen NEW DEVICES"
                        binding.unpairedTextviewPair.text = getString(R.string.found_pairing, dimen )
                    }
                }
            }
        })
        //da mettere sulla base del valore di ricerca
        binding.updateRelativePair.setOnClickListener {
            //binding.newDevicesTextPair.text= "SEARCHING..."
            btViewModel.inizioRicerca()
        }

        btViewModel.connesso.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it){ binding.btIconPair.setImageResource(R.drawable.ic_baseline_bluetooth_100_connected) }
            else{ binding.btIconPair.setImageResource(R.drawable.ic_baseline_bluetooth_100) }
        })

        return binding.root
    }

    //DA PERSONALIZZARE E FA COMPARIRE UN MESSAGGIO DI CONFERMA PRIMA DI EFFETTUARE L'OPERAZIONE
    fun unpairConfirmDialog(device:DeviceData) {
        val dialog: AlertDialog = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.title_unpairing_dialog, device.deviceName))
            .setMessage(getString(R.string.description_unpairing_dialog, device.deviceName))
            .setPositiveButton(getString(R.string.positive_unpairing_dialog),
                DialogInterface.OnClickListener { dialog, which ->
                    btViewModel.unpair(device)
                })
            .setNegativeButton(getString(R.string.negative_unpairing_dialog), null)
            .create()
        dialog.show()
    }
}

class NoDeviceFound {
    fun showNoDeviceDialogue(pairFragment: PairFragment?) {
        val dialog = Dialog(pairFragment!!.requireActivity()!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.no_silverline_found)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dialogBtn_relaunch = dialog.findViewById<ConstraintLayout>(R.id.relaunchNoRedlineCon)
        dialogBtn_relaunch.setOnClickListener {
            pairFragment.btViewModel.inizioRicerca()
            dialog.dismiss()
        }
        dialog.show()
    }
}