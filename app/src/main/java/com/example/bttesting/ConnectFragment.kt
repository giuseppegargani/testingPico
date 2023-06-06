package com.example.bttesting

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import com.example.bttesting.databinding.ConnectFragmentBinding
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.bttesting.viewModels.BluetoothViewModel

class ConnectFragment : Fragment() {

    private val btViewModel: BluetoothViewModel by activityViewModels()

    lateinit var btImmagine: ImageView
    lateinit var btIcon: ImageView
    lateinit var adapter: DevicesListAdapter

    var mAdapter: BluetoothAdapter? = null

    companion object {
        private val REQUEST_ENABLE_BT = 123 //codice arbitrario per distinguere onActivityResult
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = DataBindingUtil.inflate<ConnectFragmentBinding>(inflater, R.layout.connect_fragment, container, false)

        btIcon = binding.btIconConnect

        // Adapter con eventListener: cambia colore di sfondo; quando si e' gia' connessi ad un device naviga semplicemente dentro alla schermata di livedata, altrimenti prova a connettersi
        adapter = DevicesListAdapter(DeviceDataListener {
            if((btViewModel.connesso.value==true)&&(it.deviceHardwareAddress==btViewModel.btService.connectedDevice?.address)){this.findNavController().navigate(R.id.action_connectFragment_to_liveDataFragment)}
            else{
                //ordina a ViewModel di cambiare il valore selected della lista
                btViewModel.cambiaListaConnessi(it)
                btViewModel.connect(it);
            }
        })

        binding.deviceslistConnectRecyclerView.adapter = adapter

        //layout manager per grigla a tre elementi
        val manager = GridLayoutManager(activity, 3)
        binding.deviceslistConnectRecyclerView.layoutManager = manager

        btImmagine = binding.btIconConnect

        //verifica che bluetooth sia abilitato oppure richiedi consenso ed attiva (in fragment iniziale oppure in Activity)
        mAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mAdapter?.isEnabled == false) {
            btIcon.setImageResource(R.drawable.ic_baseline_bluetooth_disabled_100)
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(intent, REQUEST_ENABLE_BT)
        }

        //listeners dei pulsanti delle icone del menu' laterale
        binding.patientLogConnect.setOnClickListener {
            //al momento non fa' niente con questo pulsante
        }
        binding.historicalLogoConnect.setOnClickListener {
            this.findNavController().navigate(R.id.historicalFragment)
        }
        binding.settingsLogoConnect.setOnClickListener {
            this.findNavController().navigate(R.id.settingsFragment)
        }
        binding.helpLogoConnect.setOnClickListener {
            this.findNavController().navigate(R.id.helpFragment)
        }
        binding.backBtnConnect.setOnClickListener {
            this.findNavController().navigate(R.id.homeFragment)
        }

        binding.updateRelativeConnect.setOnClickListener {
            btViewModel.inizioRicerca()
        }
        //se si tiene premuto icona bluetooth si disconnette
        binding.btIconConnectRelative.setOnLongClickListener {
            btViewModel.disconnect()
            false
        }

        btViewModel.lista.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {
                adapter.data =it; }
        })

        btViewModel.connesso.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                if (it) { btIcon.setImageResource(R.drawable.ic_baseline_bluetooth_100_connected) }
                else { btIcon.setImageResource(R.drawable.ic_baseline_bluetooth_100) }
            //quando si connette naviga automaticamente (ma questa funzione si attiva solamente dopo lo stato resumed (inizio interazione utente)
            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
                if(it){this.findNavController().navigate(ConnectFragmentDirections.actionConnectFragmentToLiveDataFragment())}
            }
        })

        btViewModel.inRicerca.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it) {binding.updateIconConnect.setImageResource(R.drawable.ic_baseline_update_100_during)}
            else {binding.updateIconConnect.setImageResource(R.drawable.ic_baseline_update_100)}
        })

        //oltre ad agganciare listener avvia anche effettivamente la ricerca
        btViewModel.inizioRicerca()

        return binding.root
    }

    //rimane in fragment o si mette in Activity
    override fun onActivityResult(
        requestCode: Int, resultCode: Int, resultData: Intent?) {
        //richiesta di abilitare
        if (requestCode == 123 && resultCode == Activity.RESULT_OK) {
            mAdapter?.enable()
            btIcon.setImageResource(R.drawable.ic_baseline_bluetooth_100)
        }
    }

}
