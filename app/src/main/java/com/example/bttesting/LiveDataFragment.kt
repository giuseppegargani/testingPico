package com.example.bttesting

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.bttesting.databinding.LiveDataFragmentBinding
import com.example.bttesting.utils.NavigToDialog
import com.example.bttesting.viewModels.BluetoothViewModel

/* OGNI SCHERMATA DOVREBBE AVERE UN RIEPILOGO INIZIALE DELLE VARIABILI E DEI METODI (cosa fanno e il senso)
    PERMETTE DI APPLICARE PROGRAMMAZIONE FUNZIONALE NEL VERO SENSO (riciclo delle funzioni)
    CON SPIEGAZIONE!!!
    SINTESI E RIUTILIZZO (una funzione per scomporre e una per scomporre i dati)
    IN MODO DA POTER VERAMENTE APPLICARE MVVM E OSSERVARE THREADS!!!!!
    FUNZIONI ANCHE SU THREADS!!!!! delle funzioni che lanciano threads scritte da altre parti!!!!!
    IN VIEWMODEL NON CI DOVREBBERO ESSERE FUNZIONI CHE FANNO COSE!!! ma solo le invocazioni delle stesse!!!
 */

/* Si puo' modificare direttamente qualche elemento di ViewModel? oppure si deve usare encapsulation? con funzioni

 */

/*SI DEVE VERIFICARE CHE VENGANO INIZIALIZZATI I VALORI del liquido e delle perdite aeree

 */

/*VERIFICARE I VALORI DI ASPIRAZIONE DELLA BARRA DI PRESSIONE

 */

/*VERIFICARE CHE CUSTOMVIEW NON RALLENTI TUTTO

 */

/*TOGLIERE FINDVIEWBYID E METTERE BINDING

 */

/*SEMPLIFICARE AIRLEAKAGESDATA e togliere la variabile in Viewmodel - richiedi dato e modifica la variabile in View

 */

/* DA METTERE COLLEGAMENTO A VIEWMODEL in Databinding
    Collegare LiveData con ViewModel
 */

/* QUANDO SI CLICCA SU UN PULSANTE
    in ViewModel quando riceve dei dati verifica che sia attivata la visualizzazione e poi invia
 */

/* APERTURA DI UN LAYOUT DRAWER da fragment
    https://stackoverflow.com/questions/17821532/how-to-open-navigation-drawer-with-no-actionbar-open-with-just-a-button
 */

/* MODIFICA LE VARIABILI E RICHIEDI VALORI SULLA BASE DELLA VARIABILE!!! visualizza dati sulla base della variabile!!!!!!!!!

 */

/* CONCETTI INTERESSANTI:
    - SHARED VIEWMODEL E DATABINDING: https://developer.android.com/codelabs/basic-android-kotlin-training-shared-viewmodel#3
 */

/* VERIFICA LOG E IMPORTAZIONI

 */

class LiveDataFragment : Fragment() {

    //delegato che indica che il ciclo di vita e' quello della Activity correlata e non del fragment
    private val btViewModel: BluetoothViewModel by activityViewModels()

    private var nomeDevice: String = ""
    private lateinit var pressBar: PressBar
    //variabile per la visualizzazione delle airleaks ore o minuti - all'inizio mostra i minuti
    private var airLeaksOra:Boolean = false
    private var liquidLevelGiorno:Boolean = false
    var downDialOpen:Boolean = false
    private lateinit var binding: LiveDataFragmentBinding

    //Stringa corrispondente alla raccolta liquidi giornaliera "F012" che riceve i dati da LiveData
    private var raccoltaGiorno: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d("giuseppeLiveFragment", "valori airkleaksOra ${airLeaksOra} e liquilevelGiorno ${liquidLevelGiorno}")
        binding = DataBindingUtil.inflate<LiveDataFragmentBinding>(inflater, R.layout.live_data_fragment, container, false)

        nomeDevice = btViewModel.btService.btDevice?.name?.drop(4)  ?: "-----"

        binding.userNumberLive.text = nomeDevice

        pressBar = binding.pressbarLive

        liquidLevelGiorno=false

        //questi sono i listeners del menu laterale
        binding.patientLogoLive.setOnClickListener {
            this.findNavController().navigate(R.id.connectFragment)
        }
        binding.historicalLogoLive.setOnClickListener {
            this.findNavController().navigate(R.id.historicalFragment)
        }
        binding.settingsLogoLive.setOnClickListener {
            this.findNavController().navigate(R.id.settingsFragment)
        }
        binding.helpLogoLive.setOnClickListener {
            this.findNavController().navigate(R.id.helpFragment)
        }
        binding.backBtnLive.setOnClickListener {
            this.findNavController().navigate(R.id.connectFragment)
        }

        //quando clicchi sulla icona del grafico lancia una funzione che dopo aver cancellato su attiva la coroutine di richiesta dati permette di navigare
        binding.graphIconLiveRelative.setOnClickListener { btViewModel.cancellaRichLive() }

        //quando viene cancellato il thread di LiveData si naviga automaticamente
        btViewModel.navigaGraph.observe(viewLifecycleOwner, Observer {
            if(it==true) {findNavController().navigate(LiveDataFragmentDirections.actionLiveDataFragmentToGraphFragment(false, null))}
        })

        binding.airConstraintLive.setOnClickListener {
            airLeaksOra = !airLeaksOra //cambia il valore
            //fai richiesta sulla base del valore di airLeaksOra
            if(airLeaksOra){btViewModel.scrivi("f00300000003f1")}
            else{btViewModel.scrivi("f00200000002f1")}
        }

        //Listener sul riquadro dei liquidi (adesso il dato arriva direttamente "F012")
        //quando si clicca aggiorna il booleano (liquidiLevelGiorno) relativo al tipo di dato richiesto (e invoca metodo)
        binding.liquidConstraintLive.setOnClickListener {
            Log.d("giuseppeLiquid", "entrato dentro fragment")
            liquidLevelGiorno = !liquidLevelGiorno //cambia il valore richiesto BOOLEANO

            //NUOVO MECCANISMO - richiedi direttamente il dato e, se presente visualizza il valore temporaneo
            if(liquidLevelGiorno){
                //se raccoltaGiorno e' presente intanto aggiorna con il valore presente e poi richiedi di nuovo
                raccoltaGiorno?.let { updateLiquidView(it, true)}
                btViewModel.scrivi("f01200000012f1")
            }
            else{ btViewModel.scrivi("f00700000007f1")}

            //DA RIMETTERE QUESTE DUE RIGHE E BASTA SE NON FUNZIONA AGGIORNAMENTO TRAMITE F12
            //if(liquidLevelGiorno){btViewModel.liquidiGiornoLive()}
            //else{btViewModel.scrivi("f00700000007f1")}
        }

        //observer DA METTERE IN DATABINDING!!!!!!!!!!!!!!!!!!!!!!!!! direttamente
        btViewModel.aspirazione.observe(viewLifecycleOwner, Observer {
            if(getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
                binding.aspirationLiveData.text = it
                Log.d("giuseppeThread","ASPIRAZIONE DENTRO UI $it")
            }
        })

        //observer DA METTERE IN DATABINDING!!!!!!!!!!!!!!!!!!!!!!!!! direttamente
        btViewModel.ore.observe(viewLifecycleOwner, Observer {
            if(getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
                Log.d("giuseppeThread","ORE DENTRO UI $it")
                binding.hoursLiveData.text = it
            }
        })

        btViewModel.intraPress.observe(viewLifecycleOwner, Observer {
            //valori massimo, medio, minimo come int
            Log.d("giuseppeBarra", "valori in LiveData in fragment ${it[0]} ${it[1]} ${it[2]}")
            pressBar.cambiaValori(it)
            pressBar.invalidate()
            //verifDati()
        })

        btViewModel.airLeaksLiveData.observe(viewLifecycleOwner, Observer {
            //se richiesta di valori al minuto e i dati in arrivo sono minuti cambia la scritta
            Log.d("giuseppeRaccolta","arrivato valore AirLeaks in LiveFragment ${it} e variabile airleaksOra $airLeaksOra")
            if(getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
                if (!airLeaksOra && (it.first == 1)) {
                    //binding.airLeaksTextLive.text = "1 MINUTE";
                    binding.airLeaksTextLive.text = getString(R.string.minute_air_live)
                    binding.airLeakagesData.text = it.second
                }
                if (airLeaksOra && (it.first == 2)) {
                    //binding.airLeaksTextLive.text = "AVG 1 HOUR";
                    binding.airLeaksTextLive.text = getString(R.string.average_air_live)
                    binding.airLeakagesData.text = it.second
                }
            }
        })

       /* AGGIORNA I DATI SULLA BASE DEL TIPO ARRIVATO (it.first) e richiesto (liquidLevelGiorno)!!!
        AGGIUSTAMENTO POSTICCIO IN ATTESA DI CORREZIONE SULLA SCHEDA
        dato di tipo 1 (istantaneo) tipo 2 (giornaliero)
        ADESSO NON C'è BISOGNO DI CALCOLARE!!! e arriva direttamente il valore daily
        LIQUIDLEVELGIORNO E' la variabile booleana di questo fragment
        LIVE DATA DI UNA COPPIA E IL PRIMO VALORE E' il tipo di valore (1=totale, 2=giornaliero)*/
        btViewModel.currentLiquidLevel.observe(viewLifecycleOwner, Observer {
            //binding.liquidInstantDrainage.text = it
            Log.d("giuseppeRaccolta", "arrivato valore in currentLiquid in liveFragment ${it} e variabile liquidLevelGiorno $liquidLevelGiorno")
            if(getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
                //il primo valore indica il tipo (1=raccolta totale) - liquid level giorno e' il booleano della richiesta
                if (!liquidLevelGiorno && (it.first == 1)) {
                    //binding.currentLiquidTextLive.text = "ml";binding.liquidInstantDrainage.text = it.second
                    updateLiquidView(it.second, false)
                    Log.d("giuseppeLiquid", "ARRIVATO VALORE currentLiquidLevel di tipo 1 ")
                }
                //PER IL MOMENTO NON AGGIORNA PIU' IN QUESTO MODO MA DIRETTAMENTE CON F12!!!
                if (liquidLevelGiorno && (it.first == 2)) {
                    //binding.currentLiquidTextLive.text = "ml/24h";binding.liquidInstantDrainage.text = it.second
                    //updateLiquidView(it.second, true)
                }
            }
        })

        btViewModel.dayLiquidLevel.observe(viewLifecycleOwner, Observer {
            if(getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
                binding.totalLiquidDrainageLive.text = it
            }
        })

        /*AGGIUNTA NUOVA - valore diretto dei liquidi delle ultime 24 ore
        in realtà si deve modificare anche la view mostrata con i dati
        ONLICK LISTENER - INVIA RICHIESTA!! quando arriva dato se richiesto DAILY CAMBIA UNITà DI MISURA
        come in precedenza fatto prima
        viewmodel = metodo richiesta dati (che utilizza write) e emetti;
         fragment: VARIABILI VIEW DI RICHIESTA e quando riceve cambia visualizzazione sulla base variabili
        RICHIESTA NON AUTOMATICA DEL VALORE (DATO CHE "NASCOSTO" MA CON CLICK LISTENER)
        ClickListener: se presente cambia subito? oppure richiedi sempre il valore?  REATTIVO!!!
        arriva prima il valore in ViewModel e poi in fragment*/
        btViewModel.directDailyLiquid.observe(viewLifecycleOwner, Observer {
            //aggiorna il seguente valore
            raccoltaGiorno=it.toString()
            Log.d("giuseppeLiquid", "LIVE valore liquidi 24 ore $it e RACCOLTA: $raccoltaGiorno")
        } )

        btViewModel.connesso.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it){ binding.btIconLiveData.setImageResource(R.drawable.ic_baseline_bluetooth_100_connected) }
            else{
                binding.btIconLiveData.setImageResource(R.drawable.ic_baseline_bluetooth_100);
                /*binding.userNumberLive.text="----"
                binding.hoursLiveData.text = "---"
                binding.aspirationLiveData.text = "-"
                binding.airLeakagesData.text="--"
                binding.liquidInstantDrainage.text="--"
                binding.totalLiquidDrainageLive.text="--"*/
            }
        })

        btViewModel.complScarico.observe(viewLifecycleOwner, Observer {
            //Log.d("giuseppeDownload2", "valore ciclo di vita step ${viewLifecycleOwner.lifecycle.currentState}}")
            Log.d("giuseppeTimer", " primo numero ${it.first} finestra aperta ${findNavController().currentDestination} e backStack ${findNavController().currentBackStackEntry}")

            //if(findNavController().currentDestination!!.label=="DownloadChartDialogue") { Log.d("giuseppeTimer2", " attualmente aperta la schermata di dialogo di download") }
            //se il primo numero che indica la navigazione e' uno si puo' navigare a download Dialog
            if((it.first== NavigToDialog.MOSTRA)&&(findNavController().currentDestination!!.label!="DownloadChartDialogue")) { this.findNavController().navigate(LiveDataFragmentDirections.actionLiveDataFragmentToDownloadChartDialogue3()) }

            //il seguente e' solo per la comparsa del toast e non prende il considerazione il valore iniziale di ViewModel
            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
                Log.d("giuseppeSave2", "valore in entrata di complScarico $it e downDialOpen $downDialOpen")

                //non riceve lo zero iniziale perche' onResume ma successivamente quando riceve invia messaggio che indica 0 task da compiere invia Toast (riferimento al numero di tasks - second); ma non riceve il valore iniziale da ViewModel
                if ((it.second == 7)) { Toast.makeText(requireActivity(), "Scaricamento dei dati completato", Toast.LENGTH_SHORT).show()
                    !downDialOpen //se torna due volte zero solo una volta cambia di valore
                } else {
                    Log.d("giuseppeDownload", "Task da compiere ${it}")
                    //OCCHIO CHE CONTINUA AD AGGIORNARE IL VALORE!!! per fortuna!!!! e quindi ricrerebbe un altro AlertDialogue!! e da' messaggio che non trova la destinazione
                    /*if(!downDialOpen){
                        Log.d("giuseppeSave2"," entrato dentro navigazione e naviga")
                        this.findNavController().navigate(LiveDataFragmentDirections.actionLiveDataFragmentToDownloadChartDialogue3()); downDialOpen=true}*/
                    //this.findNavController().navigate(GraphFragmentDirections.actionGraphFragmentToDownloadChartDialogue3())
                }
            }
        })

        return binding.root
    }

    //Questa funzione modifica la visualizzazione dei dati in raccolta liquidi (totale/ultime24ore) ed ha stringa come parametro
    //METODO DI VIEW (che aggiorna un valore di View, a seconda del dato richiesto e invia richiesta al ViewModel)
    fun updateLiquidView(valore: String, daily: Boolean) {
        if(daily==true){
            binding.currentLiquidTextLive.text = "ml/24h";
            binding.liquidInstantDrainage.text = valore
            binding.totTextViewLive.visibility = View.INVISIBLE
            binding.totalLiquidDrainageLive.visibility = View.INVISIBLE
            binding.unitLiquidLive.visibility = View.INVISIBLE
            binding.tot24hLiquidLive.visibility = View.VISIBLE
        }
        else{
            binding.currentLiquidTextLive.text = "ml";
            binding.liquidInstantDrainage.text = valore
            binding.totTextViewLive.visibility = View.VISIBLE
            binding.totalLiquidDrainageLive.visibility = View.VISIBLE
            binding.unitLiquidLive.visibility = View.VISIBLE
            binding.tot24hLiquidLive.visibility = View.INVISIBLE
        }
    }

    //SEPARARE LE DUE CONDIZIONI COME DENTRO VIEWMODEL
    override fun onStart() {
        super.onStart()
        //Log.d("giuseppeSave5", "lanciato metodo onStart")
        //se i thread sono attivi (Live oppure Save) non riavviare di nuovo
        //if(!(btViewModel.jobSaveDb.isActive)&&!(btViewModel.jobRichiesta.isActive)) {
        if(!(btViewModel.jobSaveDb.isActive)) {
            if(btViewModel.jobRichiesta.isActive) {
                Log.d("giuseppeRoutine", "UI: trovato thread iniziale attivo")
                btViewModel.jobRichiesta.cancel()
                Log.d("giuseppeSave","verifica chiusura thread ${btViewModel.jobRichiesta.isActive}")
            }
            if(!btViewModel.jobRichiesta.isActive) {
                Log.d("giuseppeRoutine", "UI: non ci sono thread attivi")
                btViewModel.liveRimasti = mutableListOf("01", "02", "03", "07", "08", "09")
                btViewModel.lanciaStringaIniziale()
            }
        }
    }
}