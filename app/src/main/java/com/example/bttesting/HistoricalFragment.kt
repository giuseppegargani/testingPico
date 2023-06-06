package com.example.bttesting

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.bttesting.databinding.HistoricalFragmentBinding
import kotlinx.coroutines.selects.select
import java.io.*

// Si dovrebbe cambiare csv e mettere in modo che possa visualizzare oppure si può usare un programma esterno che traspone la matrice

/* Concetti interessanti:

    - Context Menu:     https://www.youtube.com/watch?v=IVDKyIOVrBU
    - Dilemma tra Viewmodels grossi oppure microservices

    - Google Apps documenti:
    https://stackoverflow.com/questions/11412497/what-are-the-google-apps-mime-types-in-google-docs-and-google-drive
    https://developers.google.com/drive/api/v3/mime-types

    - intent chooser: https://stackoverflow.com/questions/8723738/open-folder-intent-chooser

    - Document: https://developer.android.com/reference/android/provider/DocumentsContract.Document#MIME_TYPE_DIR
    https://developer.android.com/reference/android/provider/DocumentsContract.Root

    - Documentazione interessante su Intent: https://developer.android.com/reference/android/content/Intent.html?hl=zh-CN#ACTION_PASTE

    - Mime type for directory: https://stackoverflow.com/questions/4749593/mime-type-for-directories-in-android   !!!!

    - CREARE DOCUMENT PROVIDER: https://trendoceans.com/how-to-fix-exposed-beyond-app-through-clipdata-item-geturi/

    - rename a file: https://stackoverflow.com/questions/10424997/android-how-to-rename-a-file
        https://stackoverflow.com/questions/56490925/kotlin-file-rename
 */

/* SE SI VUOLE IL MENU' ANDROID NATIVO:
    val scheme = "content://com.android.externalstorage.documents/document/primary%3AAndroid%2Fdata%2Fcom.example.bttesting%2Ffiles%2FDocuments"
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(Uri.parse(scheme), "vnd.android.document/root")
                startActivity(intent)
 */

/* Si puo' usare la stessa classe di Adapter che deve avere lo stesso layout del singolo elemento
    e si cambia il layout manager e la posizione!! e chiaramente il contenuto della lista
    Se ci vuole una lista generale che ripeta GLI STESSI PRIMI SEI, si deve creare una lista File con indicazione recycler
    Int, Int
    in questo modo se si selezione un elemento già selezionato in un altro recycler non lo aggiunge di nuovo
    ma si seleziona un elemento già selezionato nel solito recycler allora deseleziona!!!!
 */

/* Si deve mettere Invisible or Gone sui due recycler??

 */

class HistoricalFragment : Fragment() {

    //i due adapter
    private lateinit var adapter: HistoricalAdapter
    private lateinit var recentAdapter:RecentHistoricalAdapter

    private var selectedItem: File? = null
    //Con due recyclers deve riconoscere anche da quale e' stato premuto in precedenza (per deselezionare o meno)
    //true = recentAdapter, false = Adapter normale
    private var selectedListFragment: MutableList<Pair<File, Boolean>> = mutableListOf()
    private lateinit var binding: HistoricalFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate<HistoricalFragmentBinding>(inflater, R.layout.historical_fragment, container, false)

        binding.actionsIconsHistorical.setVisible(false)

        //I due layout managers dei recyclerViews
        val managerRecente = GridLayoutManager(activity, 3)
        val manager = GridLayoutManager(activity,6)
        //Inizialmente per i due recycler il layour manager e' a griglia con tre elementi per riga
        binding.recentRecyclerHistorical.layoutManager=managerRecente
        binding.recyclerHistorical.layoutManager=manager

        //ci sono due lambda (la seconda per il long click che rende visibile il menu delle azioni
        adapter = HistoricalAdapter( HistoricalListener (
            //per semplice click
            {
                //Questo Uri del file per
                val uri = it.toUri().toString();
                //se si vuole che aggiorni il colore anche di quello cliccato
                val posizione = adapter.data.indexOf(it)
                //SI SELEZIONA COMUNQUE COME SFONDO
                adapter.selectedItemPosition=posizione
                adapter.notifyDataSetChanged()
                this.findNavController().navigate(HistoricalFragmentDirections.actionHistoricalFragmentToGraphFragment(true, uri))
            },
            //per click lungo!!
            {
                var posizione = adapter.data.indexOf(it)
                //si aggiunge o rimuove elemento
                //adapter.selectedItemPosition = posizione;
                adapter.modificaListaSelezionati(posizione)
                adapter.notifyDataSetChanged()

               //Selected Item rimane per la funzione rinomina!! per il resto usa una lista!!!
                selectedItem=it
                //modificato aggiungendo il File alla lista
                modifySelectedList(it, false)
                //rende visibile le tre icone onLongClick a seconda se la lista e' piena o vuota
                if(selectedListFragment.isEmpty()) { binding.actionsIconsHistorical.setVisible(false) }
                else{ binding.actionsIconsHistorical.setVisible(true) }
                Log.d("giuseppeLista", "FRAGMENT: LISTA SELEZIONATI ${selectedListFragment}")
                true}))

        recentAdapter = RecentHistoricalAdapter( HistoricalListener (
            //per semplice click
            {
                //Questo Uri del file per
                val uri = it.toUri().toString();
                //se si vuole che aggiorni il colore anche di quello cliccato
                val posizione = recentAdapter.data.indexOf(it)
                //SI SELEZIONA COMUNQUE COME SFONDO
                recentAdapter.selectedItemPosition=posizione
                recentAdapter.notifyDataSetChanged()
                this.findNavController().navigate(HistoricalFragmentDirections.actionHistoricalFragmentToGraphFragment(true, uri))
            },
            //per click lungo!!
            {
                var posizione = recentAdapter.data.indexOf(it)
                //si aggiunge o rimuove elemento
                //adapter.selectedItemPosition = posizione;
                recentAdapter.modificaListaSelezionati(posizione)
                recentAdapter.notifyDataSetChanged()

                //Selected Item rimane per la funzione rinomina!! per il resto usa una lista!!!
                selectedItem=it
                //modificato aggiungendo il File alla lista
                modifySelectedList(it,true)
                //rende visibile le tre icone onLongClick a seconda se la lista e' piena o vuota
                if(selectedListFragment.isEmpty()) { binding.actionsIconsHistorical.setVisible(false) }
                else{ binding.actionsIconsHistorical.setVisible(true) }
                Log.d("giuseppeLista", "FRAGMENT: LISTA SELEZIONATI ${selectedListFragment}")
                true}))

        binding.recentRecyclerHistorical.adapter = recentAdapter
        binding.recyclerHistorical.adapter=adapter
        adapter.data=showFiles()
        recentAdapter.data=showRecentFiles()

        binding.renameRelativeHistorical.setOnClickListener {
            selectedItem?.let { rinominaFileDialog(requireContext(), selectedItem!!) }
        }

        binding.deleteRelativeHistorical.setOnClickListener {
            //Rimettere se non si vuole la cancellazione bulk!!!
            //selectedItem?.let { eliminaFileDialog(requireContext(), selectedItem!!) }
            if(selectedListFragment.isNotEmpty()){ eliminaFileDialog(requireContext(), selectedListFragment)}
        }

        binding.sendRelativeHistorical.setOnClickListener {
            //Rimettere se non si vuole invio multiplo!!!
            //selectedItem?.let { inviaFileDialog(requireContext(), selectedItem!!) }
            //Adesso accetta invio multiplo
            if(selectedListFragment.isNotEmpty()){ inviaFileDialog(requireContext(), selectedListFragment)}
        }

        //listeners per pulsanti laterali
        binding.patientLogHistorical.setOnClickListener {
            this.findNavController().navigate(R.id.connectFragment)
        }

        binding.historicalLogoHistorical.setOnClickListener {
            //questo non fa' niente perche' gia' su questo schermo
        }

        binding.settingsLogoHistorical.setOnClickListener {
            this.findNavController().navigate(R.id.settingsFragment)
        }

        binding.helpLogoHistorical.setOnClickListener {
            this.findNavController().navigate(R.id.helpFragment)
        }

        binding.backBtnHistorical.setOnClickListener {
            this.findNavController().navigate(R.id.homeFragment)
        }

        //condizioni di visibilità iniziale dei recycler!!!
        //Meglio mettere Invisible or Gone ???
        binding.recyclerHistorical.visibility=View.INVISIBLE
        binding.recentRecyclerHistorical.visibility=View.VISIBLE

        //listener per la scelta della visualizzazione del recycler
        binding.visualizationRelativeLayout.setOnClickListener {
            //se visibile quello delle recenti
            if(binding.recentRecyclerHistorical.isVisible) {
                binding.recentRecyclerHistorical.visibility = View.INVISIBLE
                binding.recyclerHistorical.visibility = View.VISIBLE
                Toast
                    .makeText(requireActivity(),
                        "cambiata visualizzazione recent: ${binding.recentRecyclerHistorical.visibility} historical: ${binding.recyclerHistorical.visibility} ",
                        Toast.LENGTH_SHORT)
                    .show()
            }
            else {
                binding.recentRecyclerHistorical.visibility = View.VISIBLE
                binding.recyclerHistorical.visibility = View.INVISIBLE
                Toast
                    .makeText(requireActivity(),
                        "cambiata visualizzazione recent: ${binding.recentRecyclerHistorical.visibility} historical: ${binding.recyclerHistorical.visibility} ",
                        Toast.LENGTH_SHORT)
                    .show()
            }
        }

        return binding.root
    }

    //si può anche togliere ma si può lasciare nel caso che vengano  compiute azioni quando l'app e' in background (con altre app)
    override fun onResume() {
        super.onResume()
        Log.d("giuseppeLista", "LANCIATO ONRESUME")
        selectedListFragment= mutableListOf()
        adapter.data=showFiles()
        recentAdapter.data=showRecentFiles()
    }

    //verifica se contiene o meno il file (secondo parametro: 0 -> recentAdapter, 1 ->adapter)
    private fun modifySelectedList(file: File, adapter:Boolean){

        val pair = Pair<File,Boolean>(file, adapter)
        val altroPair = Pair(file, !adapter)
        Log.d("giuseppeLettura", "ECCOMI $pair  COPPIA $altroPair" )
        Log.d("giuseppeLettura", "CONTIENE ${selectedListFragment.contains(pair)}")
        Log.d("giuseppeLettura", "NON CONTIENE ALTRO ${!selectedListFragment.contains(altroPair)}")
        //se contiene lo stesso elemento dallo stesso adapter rimuovilo dalla lista dei prescelti
        if(selectedListFragment.contains(pair)) {selectedListFragment.remove(pair); return}
        //altrimenti se non e' stato inserito da altro adapter, aggiungi elemento
        //if(!selectedListFragment.contains(altroPair)){ selectedListFragment.add(pair)}
        else{ selectedListFragment.add(pair); }
        Log.d("giuseppeLettura", "LISTA ${selectedListFragment}")
    }

    //CREA LISTA FILES - deve mostrare i dati relativi alla directory Documents (numero files etc...)
    private fun showFiles():MutableList<File>{
        var fileDirectory = File((requireContext().getExternalFilesDir("Documents"))!!.path)   //da mettere come field!!

        var listaFiles = fileDirectory.listFiles().toMutableList()
        //E TOGLIERE LA SEGUENTE
        /*if(fileDirectory.listFiles().isNotEmpty()) {
            for (i in 1..3) {
                listaFiles.addAll(fileDirectory.listFiles())
            }
        }*/
        listaFiles.sortByDescending { it.lastModified() }
        return listaFiles
    }

    //Deve mostrare i sei files piu' recenti
    //fai una lista di files unici, ordinali e prendi i primi sei
    private fun showRecentFiles():MutableList<File>{
        var fileDirectory = File((requireContext().getExternalFilesDir("Documents"))!!.path)   //da mettere come field!!
        var listaFiles = fileDirectory.listFiles().sortedByDescending { it.lastModified() }.toMutableList()

        //Set di devices presenti!!
        var listaDevices: MutableSet<String> = mutableSetOf()
        var lastDevices: MutableList<File> = mutableListOf()
        listaFiles.map {
            val nome=it.name.take(5)
            if(listaDevices.contains(nome)){ Log.d("beppeLettura", "NON E' LA DEVICES PIU' RECENTE") }
            else{
                Log.d("beppeLettura", "E' LA DEVICES per ${nome} PIU' RECENTE: ${it.name}")
                listaDevices.add(nome)
                lastDevices.add(it)
            }
        }

        //se maggiore di sei prendi solo le piu' recenti
        if(lastDevices.size>6){ lastDevices = lastDevices.take(6).toMutableList()}

        //listaFiles.forEach { Log.d("beppeLettura", " FILE ${it.name} MODIFICATO ${it.lastModified()}") }

        return lastDevices
    }

    //DEVE SVUOTARE GLI ELEMENTI SELEZIONATI IN RECYCLER UNA VOLTA CHIUSO
    fun rinominaFileDialog(c: Context, file:File) {
        val folder = requireActivity().getExternalFilesDir("Documents")
        val nomeAttuale = file.toString().replace("$folder/", "").take(17) //pulito da eventuali altri nomi precedenti

        val taskEditText = EditText(c)
        val dialog: AlertDialog = AlertDialog.Builder(c)
            .setTitle(getString(R.string.rename_title_historical))
            .setMessage(getString(R.string.rename_description_historical))
            .setView(taskEditText)
            .setPositiveButton(getString(R.string.rename_positive_historical),
                DialogInterface.OnClickListener { dialog, which ->
                    var nomeFile = taskEditText.text.toString()
                    if(nomeFile!=""){nomeFile="-$nomeFile"} //se si inserisce una stringa bianca alla fine ritorna al nome originale
                    var fileFinale = File("${folder}/${nomeAttuale}${nomeFile}.txt")
                    file.renameTo(fileFinale)
                    binding.actionsIconsHistorical.setVisible(false)
                    //SVUOTA LISTA
                    //adapter.selectedItemPosition=-1
                    adapter.listaSelezionati= mutableListOf()
                    adapter.data=showFiles()
                    recentAdapter.data = showRecentFiles()
                })
            .setNegativeButton(getString(R.string.rename_negative_historical),  DialogInterface.OnClickListener { dialog, which -> binding.actionsIconsHistorical.setVisible(false);  dialog.dismiss() })
            .create()
        dialog.show()
    }

    //SI PUO' ANCHE SCEGLIERE DI SCRIVERE IL NOME DEI FILES SE SOTTO AD UNA CERTA QUANTITà!!!!!!!!!!!!!!
    fun eliminaFileDialog(c: Context, listaFile: MutableList<Pair<File, Boolean>>) {
        var stringaElencoFile: String = ""
        for (i in listaFile){ stringaElencoFile+=" ${i.first.name},"}
        //rimuovi ultima virgola
        val stringaFinale=stringaElencoFile.dropLast(1)

        val dialog: AlertDialog = AlertDialog.Builder(c)
            .setTitle(getString(R.string.delete_title_historical))
                //da mostrare solo una parte del nome che visibile in recycler!!!!
            .setMessage(getString(R.string.delete_description_historical, stringaFinale))
            .setPositiveButton(getString(R.string.delete_positive_historical),
                DialogInterface.OnClickListener { dialog, which ->
                    //VERIFICA CHE IL FILE ESISTA PRIMA DI CANCELLARLO
                    for (x in listaFile) {
                        if (x.first.exists()) {
                            x.first.delete()
                        }
                    }
                    binding.actionsIconsHistorical.setVisible(false)
                    //SVUOTA LISTA SELEZIONATI
                    //adapter.selectedItemPosition=-1
                    adapter.listaSelezionati= mutableListOf()
                    recentAdapter.listaSelezionati= mutableListOf()
                    adapter.data=showFiles()
                    recentAdapter.data=showRecentFiles()
                    selectedListFragment= mutableListOf()
                })
            .setNegativeButton(getString(R.string.delete_negative_historical),  DialogInterface.OnClickListener {
                    dialog, which -> binding.actionsIconsHistorical.setVisible(false);
                    dialog.dismiss()
                    adapter.listaSelezionati= mutableListOf()
                    recentAdapter.listaSelezionati= mutableListOf()
                    adapter.data=showFiles()
                    recentAdapter.data=showRecentFiles()
                    selectedListFragment= mutableListOf()
            })
            .create()
        dialog.show()
    }

    /* SI DOVREBBE POTER SCEGLIERE IL MESSAGGIO e poi invio viene con localizzazione in locale!!!!!!!!!!!!!!!!!!!!!!!!
    */
    fun inviaFileDialog(c: Context, listaFile: MutableList<Pair<File, Boolean>>){
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.type = "text/plain"
        //emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("indirizzo@gmail.com"))    // si puo' mettere anche un indirizzo email in automatico!!!
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Invio dei dati Bluetooth")
        emailIntent.putExtra(Intent.EXTRA_TEXT, "In allegato il file con i dati del Bluetooth")

        //var listaUri: MutableList<Uri> = mutableListOf()
        var listaUri: ArrayList<Uri> = arrayListOf()

        //crea una lista di Uri (da quella di File)
        if(listaFile.isNotEmpty()){
            for (i in listaFile){
                val uriFile = FileProvider.getUriForFile(c, BuildConfig.APPLICATION_ID + "." + "MainActivity" + ".provider", i.first)
                listaUri.add(uriFile)
            }
        }
        //val uriFile = FileProvider.getUriForFile(c, BuildConfig.APPLICATION_ID + "." + "MainActivity" + ".provider", file)

        emailIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        //emailIntent.putExtra(Intent.EXTRA_STREAM, uriFile)
        //emailIntent.putExtra(Intent.EXTRA_STREAM, listaUri)
        emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, listaUri);
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        //SVUOTA LA LISTA DEGLI EVIDENZIATI IN ADAPTER
        //adapter.selectedItemPosition=-1
        adapter.listaSelezionati= mutableListOf()
        recentAdapter.listaSelezionati= mutableListOf()
        //SVUOTA ANCHE ALTRA LISTA SELEZIONATI
        selectedListFragment= mutableListOf()

        binding.actionsIconsHistorical.setVisible(false)

        //SI DEVE CUSTOMIZZARE IL TITOLO!!!!!!
        //startActivity(Intent.createChooser(emailIntent, "Scegli un provider email"))
        startActivity(Intent.createChooser(emailIntent, getString(R.string.send_intent_message)))
    }

    //extension function per far apparire e scomparire il layout con le tre icone in alto a destra
    //FORSE SI DOVREBBE METTERE DENTRO UNA CLASSE DI FUNZIONI SEPARATE!!!!!! e si utilizza da altre parti!!!! oppure internamente alla sc
    fun View.setVisible(visible: Boolean) {
        visibility = if (visible) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

}
