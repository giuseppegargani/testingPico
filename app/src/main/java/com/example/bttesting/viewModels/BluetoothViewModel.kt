package com.example.bttesting.viewModels

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.bttesting.DeviceData
import com.example.bttesting.service.BluetoothService
import com.example.bttesting.utils.*
import kotlinx.coroutines.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

/* AGGIUSTAMENTI:
     - SPIEGAZIONE INIZIALE SUL VALORE DI OGNI SINGOLA VARIABILE
     - ISTRUZIONI METODI
     - ESTERNALIZZARE ALCUNI METODI "PURI" (che non toccano variabili ambientali)!!!!!!!!!!!!!!!!!!!!!!
     - VEDERE COME SI POSSONO RENDERE PURI ALTRI METODI
 */

/*MECCANISMO DI VERIFICA DELLA QUANTITA' DEL DB SI BASA SUL NUMERO DI ORE!!!!
    e percio' si deve verificare che arriva la stringa iniziale prima di cominciare a scaricare il Db
 */

/* VERIFICARE LA PRESENZA DI VALORI PRIMA DI LANCIARE IL SALVATAGGIO DEL DB, ALTRIMENTI LANCIA THREAD RICHIESTA STRINGA INIZIALE
    Altrimenti non si puo' nemmeno verificare la quantità!!! Ore etc..
 */


/* AGGIUNGERE SCHERMATA DI DISCONESSIONE MANUALE (con un certo tempo) e verifica (nei rari casi in cui si faccia manualmente)

 */

/* SE SI VUOLE RIPRISTINARE LA CORREZIONE DELLE SERIE LIQUIDI (SIA LIVE CHE GRAFICO)
   cercare i DUE PUNTI!!! IN CUI SI USAVA "COMPARA..."  E RIPRISTINARE!!!!!
 */

/*OGNI SINGOLO DB DOVREBBE AVERE UNA FINESTRA DI SCARICO (SI CONSIDERA VALIDO SOLO QUANDO E' IL SUO TURNO)
Se si riceve un Db quando non e' il suo turno non si considera valido!!!
* */

/* CONDIZIONE PER CUI I THREADS NON PARTONO SE NON SI E' CONNESSI

 */

/* verificare se per SSOT meglio modificare un livedata da ViewModel o se da fragment si può modificare (es. LiveRimasti)

 */

/* Verificare se si puo' rendere piu' reattiva la richiesta della stringa iniziale

 */

/*Per la richiesta dei liquidi da LiveData si può mettere anche un messaggio di attesa o altro o semplicemente cambia visualizzazione

 */

/* SI PUO' CAMBIARE DIRETTAMENTE IL VALORE DI UNA VARIABILE DI VIEWMODEL?
o meglio encapsulation?

 */

/*VERIFICARE CORRETTEZZA PRIMI VALORI PER I DATI SALVATI (se 0 e' corretto)

 */

/* VARIABILE DI RICHIESTA DEL GRAFICO CHE AGGIORNA AD OGNI RICHIESTA
    e compie delle scelte sulla base della richiesta
 */

/* RINCONTROLLARE SE I DATI DEVONO ARRIVARE FLOAT O INT!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    per adesso puo' rimanere Int
 */

/* FILTRARE ALCUNE COSE DA METTERE IN SERVICE PER SSOT      SCRIVI FILES?

 */

/*ORDINARE VIEWMODEL E VEDERE SE SI PUO' SCOMPORRE E SEMPLIFICARE LE FUNZIONI (FUNZIONI CHE FANNO LE STESSE COSE)
    Funzioni che fanno le stesse cose per altre funzioni
    CON LE COROUTINES se calcolo impegnativo
 */

/* USARE LE COROUTINES ESTENSIVAMENTE (prova)

 */

/* STABILIRE NOMENCLATURA VARIABILI

 */

/* DEVE USARE COROUTINES SOSPENDIBILI!!!!!! su thread secondari   potenza di kotlin
    CALCOLI SU THREAD SECONDARI (DI LAVORO) USANDO COROUTINES COME DA DOCUMENTAZIONE UFFICIALE
    Udacity corso    launch {}
    https://classroom.udacity.com/courses/ud9012/lessons/fcd3f9aa-3632-4713-a299-ea39939d6fd7/concepts/7e5d7478-eca3-466c-bc1b-7997dcab696d

    AGGIORNAMENTO DELLE VARIABILI LIVEDATA DEVE AVVENIRE DENTRO UISCOPE (come sappiamo) OPPURE SI PUO' METTERE POSTVALUE!!!
 */

/*SI PUò METTERE IN UISCOPE LA CHIAMATA DI UNA FUNZIONE CHE RESTITUISCE UNA LISTA DI LIVEDATA (senza bisogno di mettere postValue)
    come da esempio!!!
    uiScope.launch {
     tonight.value = getTonightFromDatabase()
    }
    oppure si può mettere PostValue dentro Thread !!!!!!!!!!!!!!!!!!!!! withContext
 */

/* SPOSTARE LE FUNZIONI DI CALCOLO IN UTILS!!!!!!!!
    se possibile
 */

/* AGGIORNAMENTO DELLE VARIABILI LIVEDATA DEVE AVVENIRE DENTRO UISCOPE OPPURE SI PUO' METTERE POSTVALUE()!!!!!!!

 */

/*SI PUO' ANCHE AGGANCIARE UNA VARIABILE LOCALE AD UN PROPRIETA' DELLA ISTANZA DI UNA CLASSE INSERITA COME COSTRUTTORE PRIMARIO E CHE E' LIVEDATA (e testabile)
    var variable = btService.messaggio     dove messaggio e' liveData, c'è un modo particolare?
 */

/* OBSERVERFOREVER  approfondire possibili eccezioni

 */

/*VIEWMODEL SERVE per condividere i dati tra fragment e activity  DA METTERE CON DATA BINDING!!!!!!!!!!!
    SI DEVE METTERE UN SERVICE CHE CONTINUA A LAVORARE INN BACKGROUND!!! per esempio per connessione!!! ma anche successivamente
    per la gestione dei dati!!!
    Qui ci mettiamo tutto quello serve per diversi fragment e activity
    Collegato al service e fornisce i dati per tutti

    - connesso ad activity e deve fornire i dati per tutti i fragment

    - Viewmodel e' distrutto quando il fragment e' staccato oppure quando l'activity e' finita!!

    - prende APPLICATION come parametro (e nel nostro caso puo' essere utile per leaks, ed inoltre mettiamo coroutines)!!!!!
    DA APPROFONDIRE DIFFERENZE TRA VIEWMODELS (e AndroidViewmodel!!!!!!!!)
    CREARE ISTANZA IN ACTIVITY (con UIhandler e coroutines) e riprendere l'istanza in fragment!!! ma c'è bisogno di creare un ViewModeldifferente? (SE SI USANO COROUTINES DENTRO VIEWMODEL NO!!) perche' sono lightweight

    - SECONDO ME SI DEVE PASSARE UNA ISTANZA DI SERVICE DENTRO VIEWMODEL  (istanza da creare in Activity e da passargli quando si crea ViewModel)
    In questo modo siamo sicuri che c'è e che dialogano!!!!! tra di loro

 */

/*NON SI METTONO RIFERIMENTI A CONTEXT (STARTACTIVITY) DENTRO VIEWMODEL!!!!!!
    https://stackoverflow.com/questions/54814536/android-viewmodel-and-startactivity
    Ma si possono mettere LiveData che attivano eventi
    sulla base di LiveData si possono attivare StartActivity
 */

/* TRASFORMATIONS DA LIVEDATA A LIVEDATA!!! E FILTRAGGIO DATI
    pag.613 Carli
 */

/* ANDROID BLUETOOTH AS A SERVICE DI FABIO CHIARANI
    https://proandroiddev.com/android-bluetooth-as-a-service-c39c3d732e56
 */

/* TEXTCHART PROVARE POSTVALUE E VALUE!!!! per vedere come e' meglio

 */

/* Differenza tra Array e Lists in Kotlin:
https://stackoverflow.com/questions/36262305/difference-between-list-and-array-types-in-kotlin
 */

class BluetoothViewModel(val btService : BluetoothService, application: Application, val context: Context) :AndroidViewModel(application) {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main +  viewModelJob)

    //DEVICE SELECTED
    var deviceSel: DeviceData? = null

    //ID RANDOM
    var randomId: String = ""
    var requestRandomId: Boolean = false

    //LIVEDATA
    val aspirazione = MutableLiveData<String>()
    val intraPress = MutableLiveData<MutableList<Int>>()
    val airLeaksLiveData = MutableLiveData<Pair<Int,String>>()
    private val _currentLiquidLevel = MutableLiveData<Pair<Int,String>>()
    val currentLiquidLevel:LiveData<Pair<Int,String>>
        get() = _currentLiquidLevel

    //LIVEDATA (per essere osservati generalmente in Live Fragment ma anche altri connect fragment)
    val directDailyLiquid = MutableLiveData<Int>()
    val dayLiquidLevel = MutableLiveData<String>()
    val ore = MutableLiveData<String>()
    val lista = MutableLiveData<List<DeviceData>>()
    var connesso = MutableLiveData<Boolean>(false)

    //textChar e' la variabile per i grafici ed e' composta di coppia (tipo grafico e Array MutableList)
    private val _textChart= MutableLiveData<Pair<Int, MutableList<MutableList<Float>>>>()
    val textChart:LiveData<Pair<Int, MutableList<MutableList<Float>>>>
        get() = _textChart
    //variabile di richiesta del grafico che aggiorna ad ogni richiesta
    var richiestaDb: Int = 0 //da mantenere per utilizzi futuri

    var uriFile: Uri? = null
    var orePaziente: Int? = null

    //variabili temporanee per salvataggio Db
    var dbLiquidiTemp = mutableListOf<Float>()
    var dbMinimaTemp = mutableListOf<Float>()
    var dbMassimaTemp = mutableListOf<Float>()
    var dbAereeTemp = mutableListOf<Float>()
    var dbLiquidLvlTemp = mutableListOf<Float>()
    var dbLiquidLkgesTemp = mutableListOf<Float>()

    //valori temporanei per grafici (METTERE NOMI MIGLIORI)
    var dbMinimaFinal= mutableListOf<Float>()
    var dbMassimaFinal= mutableListOf<Float>()
    var dbLiquidFinal = mutableListOf<Float>()
    var dbAereeFinal = mutableListOf<Float>()
    var dbLiquidLevelFinal = mutableListOf<Float>()
    var dbLiquidLeakFinal = mutableListOf<Float>()

    var inRicerca = MutableLiveData<Boolean>()

    /*verifica di completamento di salvataggio e lettura
        DI NAVIGAZIONE E VIEW (BARRA)
      il primo numero indica quale tipo di dialogFragment e il secondo indica livello di avanzamento per barra download!!
     */
    private val _complScarico = MutableLiveData< Pair<NavigToDialog,Int>>()
    val complScarico:LiveData<Pair<NavigToDialog,Int>>
        get() = _complScarico

    //variabili utili per funzioni di calcolo
    var ritardoAmmissibile: Long = 3_000_000_000   //impostabile!!!
    var localPair: Pair<String,Long> = Pair<String,Long>("",0)

    //richiesta liquidi da Db per LiveData
    var richLiquidiLive: Boolean = false

    //variabile per comparazione liquidi per verifica valori anomali e funzione comparaRaccoltaLiquidi
    var hourLiquidTemp = mutableListOf<Float>()

    //variabile per richiesta dati iniziali in live data
    var liveRimasti = mutableListOf<String>()
    var jobRichiesta: Job = Job()
    var sospendiRichiesta = true
    private val _navigaGraph = MutableLiveData<Boolean>(false)
    val navigaGraph:LiveData<Boolean>
        get() = _navigaGraph

    //variabile per coroutine per richiesta Db
    var jobSaveDb: Job = Job()
    private var arrivoStrIniziale = false

    //variabile per il controllo dei tempi del singolo Db
    private var timeSingleDb: Long = 0
    private var totalTime: Int = 17
    private var inizioConn: Long = 0


    /* METODI LEGATI AL CICLO DI VITA DI QUESTO VIEWMODEL ED OBSERVERS----------------------------------------------------------------------
        - Occorre verificare memory leaks; ed aggiungere in onCleared
        - spiegazione delle variabili:
        - OBSERVERS!!! :
            1 - Lista devices da service (btDevicesListLive) (MutableList di una DataClass)
            2 - Stringa di testo arrivata da device!!! (piu' importante) (testoLive) (string)
            3 - Connected (Booleano) che indica se connesso o meno
            4 - Discovering (Booleano) che indica se Bluetooth e' in ricerca o meno
            5 - Fine tentativo (DeviceData) indica un fine tentativo di connessione ???

        - EnumClass relative:
            Tipo di dialogFragment e livello di avanzamento (per variabile complScarico)
     */

    init {


        //il secondo numero e' il numero degli step barra download
        _complScarico.value= Pair(NavigToDialog.NO_DIALOG,0)

        //OSSERVA LA LISTA DEI DEVICES (DAL SERVICE!!!) e MODIFICA L'ATTRIBUTO SELECTED (del dataClass) sulla base di deviceSel (device selezionata)
        //ma di dove pesca deviceSel??
        btService.btDevicesListLive.observeForever{
            cambiaListaConnessi(listaValori = it)
        }

        //imposta un observer su LiveData DA METTERE FLOW E ASLIVEDATA - OSSERVA IL SINGOLO DATO DAL SERVICE!!!
        //SU QUALE THREAD?? (value or postValue? - come aggiorna?)
        btService.testoLive.observeForever {
            elaboraMessaggio(it)
        }

        btService.connected.observeForever{
            connesso.value = it
            deviceSel=null
            if(it==true){inizioConn=System.currentTimeMillis(); lanciaSaveDb()}
            if(it==false){arrivoStrIniziale=false; orePaziente=null; randomId=""
                //sono le liste dei grafici
                svuotaListeDb()
                Log.d("giuseppeSave", "valore it $it per disconnessione e valore ore $orePaziente e stringa iniziale $arrivoStrIniziale e thread attivo ${jobSaveDb.isActive} e dbliquidTemp $dbLiquidLevelFinal")

                //questo e' togliere il dialogFragment in caso di sconnessione
                _complScarico.value=Pair(NavigToDialog.NO_DIALOG,0)

                if(jobSaveDb.isActive){jobSaveDb.cancel()}
            }
        }

        btService.discovering.observeForever{
            inRicerca.value=it
        }

        //quando arriva un fine tentativo di connessione
        btService.fineTentativo.observeForever{
            it?.let {
                deviceSel=null
                if(lista.value!=null) { var listatemp = lista.value; listatemp!!.map { el -> if (el.selected == true) { el.selected = false } //cancella tutti i valori dei tentativi
                    lista.value=listatemp
                    }
                }
            }
        }

    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
        //Log.d("giuseppeViewModel", "Viewmodel distrutto!")
    }

    //-------------------------------------------------------------------------------------------------------------------

    /*METODI COLLEGATI AL SERVICE ----------------------------------------------------------------------------------
        Questi metodi utilizzano tutti una istanza di service (BluetoothService) e solo uno cambia una variabile esterna (RichiestaDb)
        Connetti, Disconnetti, inizia ricerca, scrivi un messaggio
        Si possono mettere puri Richiedendo una classe generica oppure service!! come parametro!!!
    */


    /**
     * A function that simply call a method in service to connect to a certain device (as parameter)
     */
    fun connect(deviceData: DeviceData){
        btService.connetti( deviceData)
    }

    /**
     * This simply call a method in the service to disconnect
     */
    fun disconnect(){
        btService.disconnect()
    }

    /**
     * A function in Common ViewModel that launches the discovery of devices in Blueetooth Service
     */
    fun inizioRicerca(){
        btService.iniziaRicerca()
    }

    /* Se RICHIEDE di scrivere un Db, aggiorna una variabile che indica il Db che si sta richiedendo
       1 Variabile ambientale (RichiestaDb)
     */
    /**
     * Call a method in service to send a message through Bluetooth, and if Db updates a value of richiestaDb
     */
    fun scrivi(stringa:String, richiesta:Int=0){
        Log.d("giuseppeScrivi", "DENTRO SCRIVI $stringa e $richiesta")
        btService.scrivi(stringa)
        // qui si deve impostare in modo che aggiorni solamente richiestadb in caso di DB!!!!!!!!!!!!!!!!
        richiestaDb=richiesta
    }

    //lancia funzioni per accoppiare e disaccoppiare devices
    fun pair(deviceData: DeviceData){
        btService.pair(deviceData)
    }
    fun unpair(deviceData: DeviceData){
        btService.unpair(deviceData)
    }


    /**
     * Update the values of the select attribut of the instance of DeviceData (Dataclass) and the local variable (deviceSel).
     * It has two default parameters: one for deviceSel and another for the list of values (it takes the local values)
     */
    fun cambiaListaConnessi(device: DeviceData? = deviceSel, listaValori: List<DeviceData>? = lista.value){
        //if a new list but deviceSel is null just update the list
        listaValori?.let { list ->
            //val listaTemp = listaValori
            device?.let {
                deviceSel=device
                list.map { if(it==device)it.selected=true }
            }
            lista.value=list
        }
    }



    //------------------------------------------------------------------------------------------------------------------
    /* METODI PER LEGGERE E SCRIVERE FILES -----------------------------------------------------------------------------
        SCRITTURA FILES che HA ACCESSO HA DELLE VARIABILI AMBIENTALI (DbTemp)
        Ma si possono mettere in una lista di Liste?? verificare se rallenta
        Quindi questi metodi si possono tranquillamente mettere in un altro file
    */

    /**
     * Method to write that verify if patient is new, reading the first line of file of the most recent device with the same name, and writes lines
     */
    fun scritturaFile(ore: String){

        //si deve mettere come LiveData? INVECE CHE PRENDERE DIRETTAMENTE UNA VARIABILE?
        var numeroUni: String = btService.btDevice!!.name.replace("[^0-9]".toRegex(), "")

        var tempoIniziale: Long = (System.currentTimeMillis())-(ore.toLong(10)*3600000)
        val dateString = SimpleDateFormat("yyyyMMdd-HH").format(Date(tempoIniziale))
        var finale = "$numeroUni-${dateString}.txt"

        //verifica che non c'è già un file con quel nome e altrimenti crealo oppure ritorna
        var fileDirectory = context.getExternalFilesDir("Documents")
        val fileFinale= File(fileDirectory, "${finale}")

        Log.d("giuseppeConteggio", "DENTRO SCRITTURA FILE ${dbLiquidLevelFinal.size}")
        //QUESTA RIGA ACCEDE A VARIABILI AMBIENTALI DELLA CLASSE E DEL SERVICE!!!
        //lista iniziale da salvare
        var listaIniziale = mutableListOf<String>("RandomId: $randomId", "Nome Device: ${btService!!.btDevice!!.name}", "Numero ore: $ore", "Pressione Minima: ${dbMinimaFinal.joinToString(", ")}","Pressione Massima: ${dbMassimaFinal.joinToString(", ")}","Db aspirazione: ${dbLiquidFinal.joinToString(", ")}", "Db perdite: ${dbAereeFinal}", "Raccolta liquidi totale: ${dbLiquidLevelFinal}", "Raccolta liquidi Oraria: ${dbLiquidLeakFinal}")

        //prendi il file piu' recente della stessa unità
        val listFiles = fileDirectory!!.listFiles().filter { it.name.startsWith("$numeroUni") }.sortedDescending()

        //Se ci sono altri file pazienti per questa device verifica aggiuntiva
        if (listFiles.isNotEmpty()) {
            val deviceSaved = fileDirectory!!.listFiles().filter { it.name.startsWith("$numeroUni") }.sortedDescending().first()
            uriFile = deviceSaved.toUri()
            val listaUriFile = ottieniLista(uriFile!!, context)
            //legge il campo randomId (filtrando opportunamente la stringa)
            val randomLetto = listaUriFile[0]
            val valoreLetto = randomLetto.replace("RandomId: ", "")
            //verifica se il campo corrisponde

            Log.d("complemento", "ultima device ${deviceSaved} RandomIdLetto ${randomId} RANDOM $randomLetto e VALORE $valoreLetto")

            //se trova un file con un RandomId uguale salva sul file trovato e esci
            if(randomId==valoreLetto) {
                Log.d("complemento","TROVATO FILE CON RANDOMID UGUALE")
                scriviFile(deviceSaved,listaIniziale )
                return
            }
        }

        //scrivi la lista con il nome file calcolato all'inizio del metodo
        scriviFile(fileFinale,listaIniziale)

        return
    }

    //----------------------------------------------------------------------------------------------------------------------

    /* METODI DI ELABORAZIONE DELLA STRINGA DI TESTO RICEVUTA -------------------------------------------------------------
            Va a cambiare delle variabili di base dei vari campi
            Su un Thread Secondario
     */

    /*elabora il messaggio che viene inviato attraverso una coroutine su Ui Thread (gia' pronto per aggiornare LiveData direttamente da funzione)
        Si può ipotizzare in alcuni casi che modifichi direttamente un liveData su richiesta con Flow o altro
        e in programmazione concorrente (su UI Thread)
     */
    private fun elaboraMessaggio(stringa: String) {
        //coroutine su Ui thread
        uiScope.launch { packetsFilter(stringa) }
    }

    //con coroutines
    private suspend fun packetsFilter(stringa: String){
        return withContext(Dispatchers.Main) {
            //si può restituire un elenco di elementi trovati
            aggiustaStringhe(stringa)
        }
    }

    //funzioni per calcolo
    fun aggiustaStringhe(stringa: String){

        //valore presunta stringa iniziale (della connessione)
        var strInitTemp=false

        //verifica tempo e eventualmente cancella dati locali se i dati della stringa locale sono vecchi
        if ((System.nanoTime()-localPair.second)>ritardoAmmissibile){ localPair= Pair("", System.nanoTime())}

        var stringaCalcolo = ""
        if (localPair.first!=""){ stringaCalcolo = localPair.first+stringa}
        else{stringaCalcolo = stringa}

        //se arriva una stringa che comincia per f009 ed e' lunga piu' di 16 caratteri presumi che sia iniziale (e non Db) solo fino a che non decifra che si tratta del numero ore (Db)
        if((stringaCalcolo.startsWith("f009"))&&(stringaCalcolo.length>16)){strInitTemp=true}

        //cerca sottostringa corrispondente che inizia per f0 e finisce per f1 e verifica che sia maggiore di un certo numero di caratteri
        val valoriPacketId = "(01|02|03|04|05|06|07|08|09|10|11|12|13|0a|0b|0c|0d|0e|0f)"
        val filtroIniziale = "f0${valoriPacketId}[0-9a-z]{8,}f1".toRegex()

        //se contiene una stringa presunta valida come lunghezza e alcuni elementi iniziali adesso verifica lunghezza e checksum
        while(stringaCalcolo.contains(filtroIniziale)) {
            val workString = filtroIniziale.find(stringaCalcolo)!!.value //perche' abbiamo gia' controllato

            val lista = workString.chunked(2)   //dividi in bytes hexa
            var lunghezzaPayload = (lista[2]+lista[3]) //calcola lunghezza
            var packetId:String = lista[1]  //packetID

            //se il payload e' minore continue
            if(lista.size<(lunghezzaPayload.toInt(16)+7)){break}
            var payload = lista.subList(4,4+lunghezzaPayload.toInt(16)).toMutableList() //payload

            //calcolo checksum dopo aver aggiunto packetID
            var payloadConPacketID = (mutableListOf(packetId)+payload).toMutableList() //(packetId)
            var calcoloChecksum = payloadConPacketID.map { it.toInt(16) }.sumBy{it}.toString(16)
            while(calcoloChecksum.length<4){calcoloChecksum = "0"+calcoloChecksum} //aggiungi zero in cima
            //per DB sono le ultime quattro cifre
            //Log.d("giuseppeChecksum", "$packetId checksum prima di essere tagliato e' uguale a ${calcoloChecksum} e ultimo elemento payload e' ${payloadConPacketID.last()}" )
            if (calcoloChecksum.length>4){calcoloChecksum = calcoloChecksum.takeLast(4)}

            //verifica
            val filtroVerifica = "f0${packetId}${lunghezzaPayload}[0-9a-z]{${lunghezzaPayload.toInt(16)*2}}${calcoloChecksum}f1".toRegex()

            if (workString.contains(filtroVerifica)){

                //se la presunta stringa iniziale contiene effettiamente il numero delle ore
                if(packetId=="09"){arrivoStrIniziale=true }
                //RIMETTERE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                convertiTrovate(payloadConPacketID.joinToString(""))
                //Log.d("giuseppeFine", " Trovata una stringa ${filtroVerifica.find(workString)!!.value}")
                stringaCalcolo=stringaCalcolo.replace(filtroVerifica, "")
                continue
            }
            else {break}
        }
        localPair= Pair((stringaCalcolo),System.nanoTime())
    }

    //meccanismo quando si lancia una serie di Db riceve i dati!!!! verifica se i dati sono corretti e lancia un altra richiesta - MECCANISMO SI BASA SU RICHIESTADB
    //DA REINSERIRE
    fun convertiTrovate(packetIDePayload: String){
        //scompone la stringa in due parametri iniziali
        val packetId:String = packetIDePayload[0].toString()+packetIDePayload[1]
        val payload = packetIDePayload.substring(2)

        //ALL'ARRIVO DELLA STRINGA FINEDB (cioe' minore di 4 e con PacketId di 0f)
        if ((packetIDePayload.length<4)&&(packetId=="0f")) {

            //caso: Arriva la stringa di fine Db, sono zero ore e "IL DB RICHIESTO e' vuoto"
            //se arriva una stringa di fine db nella prima ora e tutti e tre sono vuoti aggiungi un valore al db in modo che non siano vuoti, ma DB devono essere lanciati con una richiesta!!!
            if((orePaziente==0)&&(dbMinimaTemp.isEmpty()&&dbMassimaTemp.isEmpty()&&dbAereeTemp.isEmpty()&&dbLiquidiTemp.isEmpty()&&dbLiquidLvlTemp.isEmpty()&&dbLiquidLkgesTemp.isEmpty())){
                //al momento si considera per prudenza solo il db del terzo grafico (se la richiesta DB corrisponde alla sueguente; SI DEVE METTERE UN ELSE?
                when(richiestaDb){
                    5->dbLiquidLvlTemp+=0f
                    6->dbLiquidLkgesTemp+=0f
                }
            }

            if(dbMinimaTemp.isNotEmpty()){
                //verifica la quantità finale
                orePaziente?.let {
                    if(dbMinimaTemp.size>=((it*60)-1)) {
                        dbMinimaFinal= dbMinimaTemp //invia i dati ad una variabile temporanea
                        aggiornaBarraDwnLd(1); //modifica la barra dwnload perche' scaricato il primo db
                        scrivi("f00600000006f1",2)
                    }
                    else {
                        totalTime+=2; //altrimenti aggiungi due secondi al tempo e riprova
                        scrivi("f00500000005f1",1)}
                }
                timeSingleDb = System.currentTimeMillis()
                dbMinimaTemp = mutableListOf()
                Log.d("giuseppeDownload","entrato dentro minimo e ore Paziente $orePaziente e  size ${dbMinimaFinal.size} size maggiore ${dbMinimaFinal.size>=(orePaziente!!*60)} e DbminTemp $dbMinimaFinal ")
            }

            if(dbMassimaTemp.isNotEmpty()) {
                orePaziente?.let {
                    if(dbMassimaTemp.size>=((it*60)-1)) {
                        dbMassimaFinal=dbMassimaTemp
                        aggiornaBarraDwnLd(2);
                        scrivi("f00d0000000df1",3)}
                    else {
                        totalTime+=2;
                        scrivi("f00600000006f1",2)}
                }
                timeSingleDb = System.currentTimeMillis()
                dbMassimaTemp=mutableListOf()
                Log.d("giuseppeDownload","entrato dentro massimo e ore Paziente $orePaziente e size maggiore ${dbMassimaFinal.size>=(orePaziente!!*60)} e DbmaxTemp $dbMassimaFinal")
            }

            if(dbLiquidiTemp.isNotEmpty()) {
                orePaziente?.let {
                    if(dbLiquidiTemp.size>=((it*60)-1)) {
                        dbLiquidFinal=dbLiquidiTemp
                        aggiornaBarraDwnLd(3);
                        scrivi("f00e0000000ef1",4)}
                    else {
                        totalTime+=2;
                        scrivi("f00d0000000df1",3)}
                }
                timeSingleDb = System.currentTimeMillis()
                dbLiquidiTemp = mutableListOf()
                Log.d("giuseppeDownload","entrato dentro liquidi e ore Paziente $orePaziente e size maggiore ${dbLiquidFinal.size>=(orePaziente!!*60)} e DbLiquidTemp $dbLiquidFinal")
            }

            if(dbAereeTemp.isNotEmpty()){
                orePaziente?.let {
                    if(dbAereeTemp.size>=((it*60)-1)) {
                        dbAereeFinal=dbAereeTemp
                        aggiornaBarraDwnLd(4);
                        richLiquidiLive=false;
                        scrivi("f01000000010f1",5)}
                    else {
                        totalTime+=2;
                        scrivi("f00e0000000ef1",4)}  }
                timeSingleDb = System.currentTimeMillis()
                dbAereeTemp = mutableListOf()
                Log.d("giuseppeDownload","entrato dentro aeree e ore Paziente $orePaziente e size maggiore ${dbAereeFinal.size>=(orePaziente!!*60)} e DbAereeTemp $dbAereeFinal")
            }

            if(dbLiquidLvlTemp.isNotEmpty()){
                //se richiesta Db per grafico
                Log.d("giuseppeDownload3", "valore richLiquidLive $richLiquidiLive")
                if(!richLiquidiLive) {
                    orePaziente?.let {
                        if(dbLiquidLvlTemp.size>=(it-1)) {
                            dbLiquidLevelFinal = dbLiquidLvlTemp
                            Log.d("giuseppeConteggio", "DIMENSIONI ARRIVATE: ${dbLiquidLvlTemp.size} e altro ${dbLiquidLevelFinal.size}")
                            aggiornaBarraDwnLd(5);
                            scrivi("f01100000011f1",6)}
                        else {
                            totalTime+=2;
                            scrivi("f01000000010f1",5)}  }
                    timeSingleDb = System.currentTimeMillis()
                    Log.d("giuseppeDownload","entrato dentro LiquidLevel e ore Paziente $orePaziente e size ${dbLiquidLevelFinal.size} maggiore ${dbLiquidLevelFinal.size>(orePaziente!!)} e DbLiquidLevelTemp $dbLiquidLevelFinal")
                }
                //se invece si richiede il db liquidi totale per verifica di valori anomali e correzione su entrambi!!!!!
                else{
                    val coppia = calcoloLiquidiGiornoLive(dbLiquidLvlTemp)
                    _currentLiquidLevel.value = coppia
                }
                dbLiquidLvlTemp = mutableListOf()
            }

            //se richiestaLiquidLive e' falsa fa' come db
            if(dbLiquidLkgesTemp.isNotEmpty()) {
                //se richiesta DB per grafici!!
                if (!richLiquidiLive) {
                    orePaziente?.let {
                        if(dbLiquidLkgesTemp.size>=(it-1)) {
                            aggiornaBarraDwnLd(6);
                            dbLiquidLeakFinal=dbLiquidLkgesTemp
                            //prima di scrivere verifica che sia presente anche il dato su RandomId inviatto dalla macchina
                            if(randomId!=""){
                                scritturaFile(orePaziente.toString());
                                aggiornaBarraDwnLd(7)
                            }
                            //altrimenti richiedi il dato RandomId (modificando il valore della variabile requestRandom a true
                            else {
                                totalTime+=1;
                                requestRandomId=true
                                //poiche' il dato serve esclusivamente per il salvataggio db si mette 6 come richiestaDb (parametro)
                                //IN MODO DA NON INTERROMPERE IL THREAD DI SALVATAGGIO DB
                                scrivi("f01300000013f1",6);
                            }
                            Log.d("giuseppeDownload","entrato dentro liquidLeakages e ore Paziente $orePaziente e size ${dbLiquidLeakFinal.size} maggiore ${dbLiquidLeakFinal.size>(orePaziente!!)} e DbLiquidLeak $dbLiquidLeakFinal")
                        }
                        //SE NON TORNA LA QUANTITA'
                        else {
                            totalTime+=2;
                            scrivi("f01100000011f1",6)}  }
                    timeSingleDb = System.currentTimeMillis()
                    dbLiquidLkgesTemp = mutableListOf()
                }

                //se la richiesta e' per l'aggiornamento dei valori in liveData
                else {
                    //condizione dei valori oppure chiama db totale e chiama correzione e chiama funzione calcoloLiquidiGiornoLive, altrimenti se tutto regolare invia il dato a live
                    if (dbLiquidLkgesTemp.any { it > 5000 }) { hourLiquidTemp = dbLiquidLkgesTemp; scrivi("f01000000010f1", 5) }
                    else{
                    val coppia=calcoloLiquidiGiornoLive(dbLiquidLkgesTemp)
                        _currentLiquidLevel.value = coppia
                    } //si visualizza senza salvarlo per accorciare i tempi ma si può anche decidere di salvare
                }

                dbLiquidLkgesTemp = mutableListOf()
            }
        }

        when (packetId) {
            "01"-> {
                //Questi valori servono per il thread di richiesta valori in LiveData
                if(liveRimasti.contains("01")){liveRimasti.remove("01")}
                val aspCalc = payload.toInt(16).toString()
                aspirazione.value=aspCalc
                Log.d("giuseppeThread","Valore aspirazione $aspCalc")
            }
            "02"-> {
                if(liveRimasti.contains("02")){liveRimasti.remove("02")}
                //SEMPLIFICARE PER NON COMPIERE DUE VOLTE IL CALCOLO!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                val stringa= payload.toInt(16).toString()
                val coppia = Pair(1,stringa )
                airLeaksLiveData.value=coppia
            }
            "03"-> {
                if(liveRimasti.contains("03")){liveRimasti.remove("03")}
                val stringa = payload.toInt(16).toString()
                val coppia = Pair(2, stringa)
                airLeaksLiveData.value=coppia
            }
            "04"-> {
                val massimo = convertiPressione( (((payload.substring(0, 2).toInt(16)) * 2) - 330) / 10)
                val medio = convertiPressione ((((payload.substring(2,4).toInt(16)) * 2)-330) / 10)
                val minimo = convertiPressione ((((payload.substring(4).toInt(16)) * 2) - 330) / 10)
                val lista = mutableListOf<Int>(minimo, medio, massimo ) //i valori sono invertiti occhio!!
                intraPress.postValue(lista)
                Log.d("giuseppeBarra", "Pressione intrapleurica istantanea: $massimo,$medio, $minimo")
            }
            "05"-> { dbMinimaTemp += payload.chunked(2).map {((((it.toInt(16)*2)-330).toFloat())/10)} }

            "06"-> { dbMassimaTemp += payload.chunked(2).map {((((it.toInt(16)*2)-330).toFloat())/10)} }
            "07"->{
                if(liveRimasti.contains("07")){liveRimasti.remove("07")}
                val stringa = payload.toInt(16).toString()
                val coppia = Pair(1,stringa)
                _currentLiquidLevel.postValue(coppia)
            }
            "08"->{
                if(liveRimasti.contains("08")){liveRimasti.remove("08")}
                val totalLiquid=payload.toInt(16).toString()
                dayLiquidLevel.value=totalLiquid
            }
            "09"-> {
                if(liveRimasti.contains("09")){liveRimasti.remove("09")}
                orePaziente = payload.toInt(16) //assegna le ore calcolate
                //scritturaInizialeOModificaOre(orePaziente.toString())
                ore.value=orePaziente.toString()
                Log.d("giuseppeThread","NUMERO ORE $orePaziente")
            }
            "10"->{ dbLiquidLvlTemp += payload.chunked(4).map { (it.toInt(16)).toFloat() } }

            "11"->{ dbLiquidLkgesTemp += payload.chunked(4).map { (it.toInt(16)).toFloat() } }

            "12"-> {
                //adesso arriva direttamente il dato raccolta liquidi delle ultime 24 ore
                //Si assegna ad una variabile di liveData (che View sceglie cosa visualizzare oppure se non presente richiedere
                //e aspettare (MOSTRA SOLO QUANDO IL DATO E' DISPONIBILE)
                val valore24 = payload.toInt(16)
                Log.d("giuseppeLiquid", "ARRIVATO VALORE 24 ORE $valore24")
                directDailyLiquid.value=valore24
            }

            "13"-> {
                //legge il numero univoco - da mettere come proprietà
                randomId = payload
                //val converted = payload.toInt(16)
                //richiesto valore perche' non presente al momento del salvataggio
                if(requestRandomId==true){
                    scritturaFile(orePaziente.toString());
                    aggiornaBarraDwnLd(7)
                    //una volta scritto il file si provvede a cancellare il dati
                    requestRandomId=false
                }
                Log.d("complemento", "NUMERO ${randomId}")
            }

            "0a"-> { /*"Stato di carica della batteria") */ }

            "0b"-> { /* "Modello di RedLine: ${payload.toInt(16)}" */}

            "0c"-> { "Unita' di misura della pressione: ${payload.toInt(16)} cmH2O/kPa" }

            "0d"-> { dbLiquidiTemp += payload.chunked(2).map { (it.toInt(16)).toFloat() } }//valori singoli sono numeri float

            "0e"-> { dbAereeTemp += payload.chunked(4).map {(it.toInt(16)).toFloat()} }

            "0f"-> { /*modificaRiga(uriFile!!, 2, dbMinima.joinToString(""),false )*/ }

            else-> { /*"Errore di conversione: packet Id non trovato o non inserito"*/ }
        }
    }

    //per svuotare liste locali dei Db (PER I GRAFICI)
    fun svuotaListeDb(){
        //sono le liste locali dei grafici!!!!!!!!!!!!!!!!!!!
        dbMinimaFinal= mutableListOf()
        dbMassimaFinal= mutableListOf()
        dbLiquidFinal= mutableListOf()
        dbAereeFinal = mutableListOf()
        dbLiquidLevelFinal = mutableListOf()
        dbLiquidLeakFinal = mutableListOf()
    }

    //---------------------------------------------------------------------------------------------------------------------

    /* METODI RELATIVI AI THREAD (LIVE) E SALVA DB ------------------------------------------------------------------------
     */

       //lancia coroutine per salvataggio dei grafici
    fun lanciaSaveDb() {
        jobSaveDb = uiScope.launch {
            try {
                //var tempo = 1
                while (isActive) {
                    val secondi = ((System.currentTimeMillis())-inizioConn)
                    //Log.d("giuseppeSave", "progressione valore tempo in uiscope $tempo e ore paziente $orePaziente") //conferma che ad un certo punto legge le ore Paziente (verso i tre secondi)
                    //bloccante nel senso che non continua l'esecuzione!!!!!! fino anche non completato la funzione sotto!!! quando vera esegue ed aspetta la fine
                    if(arrivoStrIniziale==true){ saveDb(); arrivoStrIniziale=false}
                    //dopo 12 secondi valuta anche una altra possibilita', cioe' che sia sufficiente le ore paziente e se non ci sono richiedile
                    else {
                        if ( secondi >= 12000) { if(orePaziente!=null){saveDb()}
                        else{ scrivi("f00900000009f1")}
                        }
                    }
                    delay(1000)
                    //tempo += 1
                }
            } finally {
                Log.d("giuseppeSave", "finalmente terminata la coroutine del salvataggio Db")
                Log.d("giuseppeSave", "stato routine live ${jobRichiesta.isActive}")
                if(jobRichiesta.isActive){
                    Log.d("giuseppeSave","thread live trovato attivo")
                    jobRichiesta.cancel()
                    Log.d("giuseppeSave","verifica chiusura thread ${jobRichiesta.isActive}")
                }
                if(!jobRichiesta.isActive){
                    Log.d("giuseppeSave","non sono stati trovati thread attivi di richiesta live")
                    liveRimasti = mutableListOf("01", "02", "03", "07", "08", "09")
                    lanciaStringaIniziale()
                }
            }
        }
    }

    //aggiungere task e timer (con timer anche su singola azione) e task dopo verifica evento riuscito
    suspend fun saveDb(){
        return withContext(Dispatchers.Default){
            //ci vuole un certo margine di tempo circa mezzo secondo che arrivi tutta la stringa iniziale e venga aggiornata
            launch {
                delay(1000)
                //Log.d("giuseppeSave", "un secondo dopo che e' arrivata la stringa iniziale e tempo $tempo")
                //aggiungiTasks(7)
                _complScarico.postValue(Pair(NavigToDialog.MOSTRA,0))
                scrivi("f00500000005f1",1)
                //prendi il tempo attuale di partenza per il primo Db ed ad ogni richiesta prendi di nuovo il tempo (anche a quelle automatiche) di modo che vede quelle che non vengono richieste automaticam, per non arrivo stringa fineDb
                timeSingleDb = System.currentTimeMillis()
                //jobSaveDb.cancelAndJoin()
            }
            launch {
                Log.d("giuseppeSave", "se prima di launch di Db esegue in concomitanza")
                var richDbTemp = 0
                //var singleDbTime = 0
                var x: Int = 0
                totalTime = 17
                var singleReq = Pair<Int,Int>(0,0) //massimo tre richieste per db oppure si puo' mettere un valore totale di richieste massime - primo numero (richiestaDb) e il secondo e' il numero delle volte (max tre)
                while (x < totalTime){
                    val tempoSec = (System.currentTimeMillis())-timeSingleDb

                    if(richiestaDb!=richDbTemp){richDbTemp=richiestaDb!!}
                    //tenere questo log e verificare onResume e visualizzazione dati su grafico (se si mette in background 6-7 volte)
                    //per Log.d
                    if(richiestaDb!=0) { Log.d("giuseppeSave5", " lunghezza localPair ${localPair.first.length} conteggio totale $x e  richiesta Db: $richiestaDb e completato Scarico  ${0 + (_complScarico.value!!.second)} e tempo singolo Db $tempoSec") }
                    //quando l'ultimo db e' stato richiesto e quanto effettivamente si e' completato il salvataggio (tasks = 0) allora provvedi a cancellare la coroutine riportando il valore della richiestaDb a 0
                    if((richiestaDb==6)&&(_complScarico.value!!.second==7)){Log.d("giuseppeSave", "completato Db prima del tempo a $x");
                        _complScarico.postValue(Pair(NavigToDialog.NO_DIALOG,0))
                        richiestaDb=0 //perche' puo' succedere che altrimenti se non arrivano liveData
                        jobSaveDb.cancelAndJoin();}
                    //se ritarda richiedi il db e allunga il tempo di qualche secondo (3-4 secs) - Si puo' anche impostare un numero massimo di volte (3 volte)
                    if(tempoSec>6000) {
                        if(singleReq.first==richiestaDb){singleReq = Pair(singleReq.first, singleReq.second+1) }
                        else{singleReq = Pair(richiestaDb, 1) }

                        Log.d("giuseppeSave5", "lanciato di nuovo il Db ${richiestaDb} per arrivo tardivo!!!!!!!!!!")
                        when(richiestaDb){
                            1-> { dbMinimaTemp= mutableListOf(); scrivi("f00500000005f1",1)}
                            2-> { dbMassimaTemp= mutableListOf(); scrivi("f00600000006f1",2)}
                            3-> { dbLiquidiTemp= mutableListOf(); scrivi("f00d0000000df1",3)}
                            4-> { dbAereeTemp= mutableListOf();  scrivi("f00e0000000ef1",4)}
                            5-> { dbLiquidLvlTemp= mutableListOf(); scrivi("f01000000010f1",5)}
                            6-> { dbLiquidLkgesTemp= mutableListOf(); scrivi("f01100000011f1",6)}
                        }
                        timeSingleDb=System.currentTimeMillis() //si azzera il tempo in modo che non richiede di nuovo al secondo successivo
                        totalTime+=4
                    }
                    delay(1000) //aspetta un secondo
                    x+=1
                }
                Log.d("giuseppeSave", "raggiunto il termine del tempo")
                //qui dovrebbe andare a error message!!!!!
                _complScarico.postValue(Pair(NavigToDialog.RIMUOVI,0))
                //_complScarico.postValue(Pair(0,0))
                jobSaveDb.cancelAndJoin()
            }

        }
    }

    /*per lanciare la richiesta di dati iniziale
    f009000164006df1f0010001000001f1f002000200000002f1f003000200000003f1f007000200000007f1f00800020101000af1f00b000100000bf1f00c000101000df1
     */
    fun lanciaStringaIniziale(){
        jobRichiesta =  uiScope.launch { try {
            var tempo: Long =500
            //Log.d("complemento", "lanciata coroutine e tempo $tempo")
            while((isActive)&&(connesso.value==true)) {
                stringaIniziale(tempo)
                delay(tempo)
                tempo = tempo*2
            }
        }
        finally {
            Log.d("giuseppeCoroutine", "finalmente terminata la coroutine")
        }
        }
    }

    //si può modificare l'ordine sulla base dell'importanza
    suspend fun stringaIniziale(t: Long){
        return withContext(Dispatchers.Default){
            //launch { btService.scrivi("f00900000009f1")  }
            //verifica che la variabile liveRimasti contenga e richiedi
            Log.d("giuseppeCoroutine", "Lanciata coroutine Live ${liveRimasti}")
            if(liveRimasti.contains("01")){ Log.d("giuseppeCoroutine", "richiesto 01"); btService.scrivi("f00100000001f1"); delay(t) }
            if(liveRimasti.contains("02")){ Log.d("giuseppeCoroutine", "richiesto 02"); btService.scrivi("f00200000002f1"); delay(t) }
            if(liveRimasti.contains("03")){ Log.d("giuseppeCoroutine", "richiesto 03"); btService.scrivi("f00300000003f1"); delay(t) }
            if(liveRimasti.contains("07")){ Log.d("giuseppeCoroutine", "richiesto 07"); btService.scrivi("f00700000007f1"); delay(t) }
            if(liveRimasti.contains("08")){ Log.d("giuseppeCoroutine", "richiesto 08"); btService.scrivi("f00800000008f1"); delay(t) }
            if(liveRimasti.contains("09")){ Log.d("giuseppeCoroutine", "richiesto 09"); btService.scrivi("f00900000009f1"); delay(t) }
            if(liveRimasti.isEmpty()) {jobRichiesta.cancelAndJoin()} //oppure termina il thread se la variabile e' vuota
        }
    }

    //in questo modo cancella la coroutine di live data quando naviga e aspetta!!!!
    fun cancellaRichLive(){
        uiScope.launch {cancelLive()}
    }

    suspend fun cancelLive(){
        return withContext(Dispatchers.Default){
            if(jobRichiesta.isActive) { jobRichiesta.cancelAndJoin();
            }
            _navigaGraph.postValue(true)
        }
    }

    //---------------------------------------------------------------------------------------------------------------------

    /* METODI CHIARAMENTE DI UTILITA' GENERALE -----------------------------------------------------------------------------
         Da mettere senza dubbio in file esterni e con spiegazione
         LIVEDATA COME PARAMETRO E ENUMCLASS!!!! (EnumClass che siano autoesplicative!!!)
         Aggiungi e sottrai tasks
     */


    //da aggiungere reset (o per numero...mai maggiore di 2 o per timer..meglio...o altro)
    /*fun aggiungiTasks(numTasks:Int){
        Log.d("giuseppeGrafico", "dentro funzione aggiungi e aggiunte $numTasks")
        //FARE IN MODO DA METTERE CON VALUE E NAVIGARE
        _complScarico.postValue(Pair( _complScarico.value!!.first,numTasks))
    }*/

    //da togliere questa funzione e mettere direttamente AGGIORNA SOLAMENTE VALORE DELLA BARRA
    //da mettere ENUM TASK CON VALORI!!!!!!!
    fun aggiornaBarraDwnLd(numTasks: Int){
        //_complScarico.value= Pair(_complScarico.value!!.first, _complScarico.value!!.second.minus(1))
        Log.d("giuseppeSave19", "AGGIORNA BARRA DOWNLOAD $numTasks")
        _complScarico.value= Pair(NavigToDialog.MOSTRA, numTasks)
        //_complScarico.postValue(Pair(NavigToDialog.MOSTRA, numTasks))
    }


    //---------------------------------------------------------------------------------------------------------------------

    /* METODI RELATIVI ALLA LETTURA GRAFICI DA FILE (DA METTERE DENTRO LETTURA FILES GENERICI?)----------------------------------
        Aggiornano un liveData specifico (ma si puo' anche in qualche modo fare senza liveData?)
        METTENDO COME PARAMETRO UN LIVEDATA?
        QUANDO SI INVOCA CAMBIA IL VALORE DI UN LIVEDATA!!! indicato come parametro
     */


    //legge i dati e li passa al grafico FILTRO CHE DELIMITA I NUMERI DIVISO DA TUTTO QUELLO CHE NON E' NUMERI
    /**
     * Based on the request of Db for graph chart, read a file and update the value of liveData (with type and mutableList of List of Float
     */
    fun leggiFileOffline(uri: String, tipo:Int) {
        val uri = uri.toUri()
        if (tipo == 1) {
            var listaInteriMin: MutableList<Float> = pulisciListaXGrafici(ottieniLista(uri, context)[3])
            var listaInteriMax: MutableList<Float> = pulisciListaXGrafici(ottieniLista(uri, context)[4])
            Log.d("giuseppeConteggio", "DENTRO LEGGI OFFLINE PRESSIONE: ${listaInteriMin.size} e ${listaInteriMax.size}")
            _textChart.value = Pair(1, mutableListOf(listaInteriMin, listaInteriMax))
        }
        if(tipo==2){
            val listaInteriLiquidi: MutableList<Float> = pulisciListaXGrafici(ottieniLista(uri, context)[6])
            val listaInteriAeree: MutableList<Float> = pulisciListaXGrafici(ottieniLista(uri, context)[5])
            Log.d("giuseppeConteggio", "DENTRO LEGGI OFFLINE AEREE: ${listaInteriLiquidi.size} e ${listaInteriAeree.size}")
            _textChart.value = Pair(2, mutableListOf(listaInteriLiquidi, listaInteriAeree))
        }
        if(tipo==3){
            val interiTotale : MutableList<Float> = pulisciListaXGrafici(ottieniLista(uri, context)[7])
            val interiOra : MutableList<Float> = pulisciListaXGrafici(ottieniLista(uri, context)[8])
            Log.d("giuseppeConteggio", "DENTRO LEGGI OFFLINE LIQUIDI: ${interiTotale.size} e ${interiOra.size}")
            _textChart.value = Pair(3, mutableListOf(interiTotale, interiOra))
        }
    }

    //da togliere e la richiesta viene messa in coroutine con aggiungiTasks (7), iniziaTimer() (o anche si puo' togliere) e modifica variabile richiestaLive!!!!
    fun lanciaGrafico(numGraf:Int){
        Log.d("giuseppeConteggio", "DENTRO LANCIA GRAFICO ${dbLiquidLevelFinal.size}")
        Log.d("giuseppeGrafico", "entrato dentro lanciaGrafico e grafico ${numGraf}")
        when(numGraf){
            1->{ val lista = Pair(1,mutableListOf(dbMinimaFinal,dbMassimaFinal)); _textChart.value=lista }
            2->{ val lista = Pair(2, mutableListOf(dbAereeFinal, dbLiquidFinal)); _textChart.value=lista}
            3->{ val lista = Pair(3, mutableListOf(dbLiquidLevelFinal, dbLiquidLeakFinal)); _textChart.value=lista}
            else->return
        }
    }

    //--------------------------------------------------------------------------------------------------------------------------

    /* METODI RELATIVI ALLA VISUALIZZAZIONE DI LIVEDATA --------------------------------------------------------------------------
        Si riferiva a quando doveva essere richiesto da live il DB della raccolta liquidi giornaliera e calcolata
     */


    //questa funzione richiede i dati dei liquidi per lo schermo di liveData
    fun liquidiGiornoLive(){
        richLiquidiLive=true
        scrivi("f01100000011f1",6)
    }

    //----------------------------------------------------------------------------------------------------------------------------

    /* METODI RELATIVI A LIVE DATA DI NAVIGAZIONE -----------------------------------------------------------------------------------
     */


    //appena si arriva in graph fragment si ricambia il valore perche' altrimenti rinavigherebbe subito in graph quando ritorna in live
    fun reimpostaNavGraph(){
        _navigaGraph.value=false
    }

    //----------------------------------------------------------------------------------------------------------------------------------

}