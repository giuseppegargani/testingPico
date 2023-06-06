package com.example.bttesting.service

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_FINISHED
import android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_STARTED
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.*
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.bttesting.ConnectFragment
import com.example.bttesting.DeviceData
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

private const val TAG = "MY_APP_DEBUG_TAG"

// Defines several constants used when transmitting messages between the
// service and the UI.
const val MESSAGE_READ: Int = 0
const val MESSAGE_WRITE: Int = 1
const val MESSAGE_TOAST: Int = 2

/* COSA SI VUOLE DA QUESTA LIBRERIA:
    1 - che utilizzi Flow
    2 - che sia immediata
    3 - DOVREBBE UTILIZZARE UN SERVICE IN MODO CHE POSSA COMPIERE AZIONI IN BACKGROUND (BroadcastReceiver)
 */

/* Qui si devono mettere diverse cose da testare:
        - Accoppiare e spaiare una device in tempo reale; ed aggiornare la lista di devices
        - testare la liveData delle connessioni ed altro
        - testare la riconnessione (mettendo un blocco e verificando che richiamando la connessione con una device diversa)
        - testare inizio connessione con una variabile che lo comunica e inizio scoperta
        - AZIONI MULTIPLE: richiedi accoppiamento e scoppiamento e connetti e sconnetti, verifica la riconnessione
 */

/* METTERE FLOW CHE TRASMETTE DATI ALTRIMENTI RALLENTA IL MAINTHREAD
    Si possono passare i dati ad una funzione che li invia a ViewModel come Flow!!! Flow...emit

    importante: https://stackoverflow.com/questions/58773453/kotlin-flow-vs-android-livedata

    ARTICOLO INTERESSANTE: https://www.netguru.com/blog/android-coroutines-%EF%B8%8Fin-2021
 */

/*VERIFICARE I JOB E LA CANCELLAZIONE E METTERLI SEPARATI sulla base dei compiti
 */

/* DIFFERENZA TRA POSTVALUE E SETVALUE!!!!!! uno va su mainThread!!!!! ma prende tutti i valori
https://stackoverflow.com/questions/51937241/updated-value-of-livedata-gets-lost
 */

/* ACCOPPIARE PROGRAMMATICAMENTE:
https://stackoverflow.com/questions/17168263/how-to-pair-bluetooth-device-programmatically-android
 */

/* VERIFICARE DOVUNQUE CHE LE COROUTINES MESSE SIANO ANCHE RIMOSSE!!!!

 */

/* RICONNESSIONE AVVIENE QUANDO SI PERDE IL COLLEGAMENTO E VIENE ANNULLATA QUANDO SI RICEVE UN ALTRA RICHIESTA DI COLLEGAMENTO
    si puo' cancellare una coroutine sospendibile con isActive (su connect e quando connette)
    SI METTE IS ACTIVE SU JOB() PER CANCELLARE
 */

/* METTERE LE FUNZIONI DENTRO THREAD COME SUSPEND COSI' SI PUò USARE CANCELANDJOIN() SU LAVORO (JOB())  !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

 */

/* USARE FLOW (CALLBACKFLOW) PER DISCOVERY E INVIARE COME ASLIVEDATA  (SSOT E TESTABILE)  E DEBUG FLOW!!!
    CALLBACKFLOW DESIGNATA ESPRESSAMENTE PER QUESTO USO!!!! BROADCAST RECEIVER
    INTERESSANTE: https://engineering.monstar-lab.com/en/post/2020/08/12/Let-it-Flow/            !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    https://stackoverflow.com/questions/67071830/wrap-broadcast-receiver-into-flow-coroutine
    callbackflow() https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/callback-flow.html

    DEBUG FLOW: https://kotlinlang.org/docs/debug-flow-with-idea.html#debug-the-coroutine
 */

/* DEBUG DISCOVERY!! CI SONO TANTI RECEIVER COLLEGATI A INIZIO E MODIFICA E TERMINE DISCOVERY
 */

/* MA INVIA SOLO I DATI QUANDO CAMBIANO O TUTTI I DATI? E NEL CASO DI DB CON SERIE DI DATI UGUALI? TESTARE!!!!!!!!
 */

/* SI USA KOTLIN FLOW E SI CONVERTONO IN LIVEDATA "ASLIVEDATA" perchè LiveData può influenzare UIThread
    Percio' non si devono usare per trasmettere dati ma solo in ViewModel perchè UIView non può contenere DATI!!!!!!!
    Documentazione ufficiale:

    https://developer.android.com/codelabs/advanced-kotlin-coroutines#9
    https://kotlinlang.org/docs/flow.html (con tanti tests)   EDITOR ONLINE e POSSIBILITA' DI ACCEDERE AD UN PLAYGROUND IN UNA PAGINA SEPARATA!!!!
    https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-flow/index.html   DOCUMENTAZIONE UFFICIALE INTERESSANTE!!!!!
    https://developer.android.com/topic/libraries/architecture/livedata
    https://github.com/giuseppegargani/kotlin-coroutines
    Activities and fragments should not hold LiveData instances because their role is to display data, not hold state. Also, keeping activities and fragments free from holding data makes it easier to write unit tests.

    It may be tempting to work LiveData objects in your data layer class, but LiveDatais not designed to handle asynchronous streams of data.
    Even though you can use LiveData transformations and MediatorLiveData to achieve this, this approach has drawbacks: the capability
    to combine streams of data is very limited and all LiveData objects (including ones created through transformations) are observed on the main thread. Can block mainThread

    SI PUò USARE ANCHE PER AGGIORNARE UNA UI SULLA BASE DELLE MODIFICHE AD UN DB (condiviso o remoto)!!!!!!!
    E' un flusso di dati (valori multipli quindi) che vengono elaborati in maniera asincrona
    I flussi di valore emesso devono essere dello STESSO TIPO!!! ma si possono elaborare successivamente in un VieWModel!!! con un LiveData collegato

    https://developer.android.com/kotlin/flow

 */

/* ASLIVEDATA operatore intermedio
    https://developer.android.com/reference/kotlin/androidx/lifecycle/package-summary#aslivedata
    ASLIVEDATA E' UN METODO DI FLOW()
 */

/* DEBUG KOTLIN FLOW
    https://kotlinlang.org/docs/debug-flow-with-idea.html#debug-a-kotlin-flow-with-two-coroutines
 */

/* KOTLIN FLOWS E LIVEDATA
    https://developer.android.com/codelabs/advanced-kotlin-coroutines#0    CODELAB
    https://developer.android.com/topic/libraries/architecture/coroutines
 */

/*SI DEVE USARE LE COROUTINES!!!!! MIGLIORARE EFFICIENZA!!! TUTORIAL
        https://developer.android.com/kotlin/coroutines/coroutines-adv
        https://developer.android.com/codelabs/kotlin-coroutines#4
 */

/*DA METTERE Try Catch E GESTIONE ERRORI e connessione lontana

 */

/* Riferimenti:
    https://developer.android.com/guide/topics/connectivity/bluetooth/transfer-data
 */

/*LIVEDATA IN SERVICE
    Puo' osservare il lifecycle oppure può essere observer Forever e può solo essere rimosso espressamente
 */

/*QUANDO DISCONNESSO RITORNA AL MENU' DI SELEZIONE E QUANDO CONNESSO VAI AL MENU DATI
 */

/*POSTARE UN VALORE OGNI VOLTA CHE ARRIVA ANCHE SE UGUALE!!!!!!!!
    postValue se su Thread differente (senza bisogno di invocare una funzione)
    https://stackoverflow.com/questions/53304347/mutablelivedata-cannot-invoke-setvalue-on-a-background-thread-from-coroutine

 */

/*DIFFERENZA TRA MUTABLELIVEDATA E LIVEDATA
https://stackoverflow.com/questions/55914752/when-to-use-mutablelivedata-and-livedata
 */

/* CONVERTIRE DA LIVEDATA A FLOW
https://chao2zhang.medium.com/converting-livedata-to-flow-lessons-learned-9362a00611c8
 */

/* CONTROLLARE INIZIALIZZAZIONE LIVEDATA!!!!!!!!!! da mettere come null

 */

/*QUANDO SI CANCELLA UN THREAD CHIUDE IL SOCKET E QUINDI IL TRY CATCH DI LETTURA RESTITUISCE UN FALLIMENTO
    Si potrebbe verificare che il socket e' chiuso ma per quale ragione? se chiuso volontariamento o meno
    chiiudi volontariamente il socket e non provare a ricollegarti oppure se si chiude per cause esterne ricollegati
    MA SE SI USA JOIN? SU CONNECTED THREAD?
 */

/*MECCANISMO RICERCA: Quando inizializzato e' null; quando inizia ricerca e' false, se nuova richiesta di discovery diventa true, e ritorna false dopo un nuovo riavvio,
entrata in ricerca e valore cancelRequest false e valore true
2022-01-20 12:39:31.950 16611-16611/com.example.bttesting D/giuseppeMessage: in ricerca e dopo cancellazione e valore cancelRequest true e valore true
 */


class BluetoothService(private var context: Context/*, private val handler: Handler*/) {

    companion object {
        val STATE_NONE = 0       // we're doing nothing
        val STATE_LISTEN = 1     // now listening for incoming connections
        val STATE_CONNECTING = 2 // now initiating an outgoing connection
        val STATE_CONNECTED = 3  // now connected to a remote device
    }

    //blutooth Adapter
    var mAdapter: BluetoothAdapter? = null
    var mState: Int = 0
    var mNewState: Int = 0
    var pairedDevices: Set<BluetoothDevice>? = null
    //thread di connessione
    var mConnectThread: BluetoothService.ConnectThread? = null
    var mConnectedThread: BluetoothService.ConnectedThread? = null
    //cambio device
    var prima: Boolean = true
    //btDevice
    var btDevice: BluetoothDevice? = null
    //socket per LiveData
    var mSocket: BluetoothSocket? = null

    val MY_UUID_SECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    // Name for the SDP record when creating server socket
    val NAME_SECURE = "BluetoothChatSecure"

    var messaggioLetto: String = ""

    var btDevicesList = mutableListOf<DeviceData>() //lista di devices non Live

    //LIVEDATA
    private val _testoLive= MutableLiveData<String>()
    val testoLive:LiveData<String>
        get() = _testoLive

    //variabile relativa al tentativo di connessione da inviare solamente in caso di fine (connessione o fallimento)
    var fineTentativo= MutableLiveData<DeviceData?>(null)
    //var tentativoTemp = Pair<DeviceData?, Boolean>(null, false)  //perche' per inizio connessione non c'è bisogno di inviare dati e viene solo immagazzinato

    var btDeviceLive = MutableLiveData<BluetoothDevice>() //per il metodo riguardante le singole unità
    var btDevicesListLive= MutableLiveData<MutableList<DeviceData>>() //per la lista da mandare a ViewModel (da mettere usando Flow)
    var connected = MutableLiveData<Boolean>(false)
    private val _discovering = MutableLiveData<Boolean>()
    val discovering:LiveData<Boolean>
    get() = _discovering
    //variabile che verifica che durante la ricerca di device non sia stata richiesta una nuova ricerca altrimenti non mandare finediscovery
    private var cancelRequest:Boolean = false

    var connectedDevice: BluetoothDevice? = null


    var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main +  viewModelJob)
    var lavoro: Job? = null
    var riconnessione = true

    init {

        // Register for broadcasts when a device is discovered.
        /*var filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        context.registerReceiver(mReceiver, filter)
        // Register for broadcasts when discovery has finished
        filter = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        context.registerReceiver(mReceiver, filter)*/

        registraRicerca()

        mState = STATE_NONE
        mNewState = mState
        //Log.d("giuseppeViewModel", "inizializzato il servizio")
        mAdapter = BluetoothAdapter.getDefaultAdapter()
        //prende una lista di paired devices (verificata)!!! già all'avvio!!! (nel nostro caso sono le due devices rpe00002 e rpe00010
        pairedDevices = mAdapter?.bondedDevices
        //Log.d("giuseppe", "lunghezza paired devices ${pairedDevices?.size}")

        //inizializzate
        _testoLive.value = ""
        btDevicesListLive.value = mutableListOf()

        //startDiscovery()

    }

    //DA TOGLIERE ASSOLUTAMENTE OGNI RIFERIMENTO A STATE!!!!!!!! mettere una funzione che analizza se connesso da Adapter!!!
    @Synchronized fun getState(): Int {
        return mState
    }

    //aggiorna la lista delle accoppiate
    fun updatePaired(){
        pairedDevices=mAdapter?.bondedDevices
        Log.d("giuseppePaired", "Lista di Paired: ${pairedDevices?.map { it.name }}")
    }

    //disaccoppia una device e ricarica la lista di accoppiate
    fun unpair(btdevi:DeviceData){
        val device = mAdapter!!.getRemoteDevice(btdevi.deviceHardwareAddress)

        try {
            device::class.java.getMethod("removeBond").invoke(device)
            Log.d("giuseppePaired5", "passata per removeBond in Fragment ")
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Removing bond has been failed. ${e.message}")
        }
        //updatePaired()
        //iniziaRicerca()
        Log.d("giuseppePair3", " lista dopo unpair ${pairedDevices?.map { it.name } }")
    }

    fun pair(deviceData: DeviceData){
        Log.d("giuseppePaired", "Passata per PAIR")
        val bluetoothDevice: BluetoothDevice = mAdapter!!.getRemoteDevice(deviceData.deviceHardwareAddress)
        try {
            bluetoothDevice::class.java.getMethod("createBond").invoke(bluetoothDevice)
            Log.d("giuseppePaired5", "passata per pairBond in Fragment ")
        } catch (e: Exception) {
            Log.e(TAG, "Removing bond has been failed. ${e.message}")
        }
        //updatePaired()
        //iniziaRicerca()
        Log.d("giuseppePair3", " lista dopo unpair ${pairedDevices?.map { it.name } }")
    }

    //Togliere...esperimento LiveData
    fun impostareStringaLive(stringa:String){
        //testoLive.postValue(stringa)
        uiScope.launch {
            _testoLive.value=stringa
        }

    }

    fun iniziaRicerca(){
        //per verifica
        Log.d("giuseppeMessage","entrata in ricerca e valore cancelRequest ${cancelRequest} e valore ${_discovering.value}")
        if (mAdapter?.isDiscovering == true) {cancelRequest=true; mAdapter?.cancelDiscovery()}
        Log.d("giuseppeMessage","in ricerca e dopo cancellazione e valore cancelRequest ${cancelRequest} e valore ${_discovering.value}")

        //aggiorna lista paired ad inizio di ricerca
        updatePaired()

        //e svuota la lista precedente e invia il valore di LiveData
        btDevicesList= mutableListOf()
        //btDevicesListLive.postValue(btDevicesList)

        // Request discover from BluetoothAdapter
        mAdapter?.startDiscovery()
        //cancelRequest=false
        Log.d("giuseppeMessage","in uscita in ricerca e valore cancelRequest ${cancelRequest} e valore ${_discovering.value}")
    }


    fun scrivi(stringa: String){
        var packet: ByteArray = stringa.decodeHex()
        mConnectedThread?.let { mConnectedThread!!.write(packet) }
    }

    //da lanciare da ViewModelCorrispndente quando termina il ciclo di vita
    fun unregisterReceiver(){
        //context.unregisterReceiver(mReceiver)
    }

    //riconnessione  DA RIATTIVARE
    fun riconnessione(device: BluetoothDevice){
        //uiScope.launch { reconnect(device) }
    }

    suspend fun reconnect(device: BluetoothDevice){
        return withContext(Dispatchers.Default){
            //Log.d("giuseppeConnected", "iniziata coroutine di riconnessione")
            lavoro = launch {
                if(isActive){
                    delay(10000)
                    val nome = device.name ?: "null"
                    val address = device.address
                    val deviceData = DeviceData(nome, address)
                    connect(deviceData)
                }
            }
        }
    }

    fun registraRicerca() {
        uiScope.launch { getDevices() }
    }

    //DEVE INVIARE SOLO UN LISTA DI BLUETOOTHDEVICE E NON DEVICEDATA
    /**
     * function in bluetooth service that it is a broadcast receiver that it receives a discovered bt device
     */
    suspend fun getDevices() {
        return withContext(Dispatchers.Default) {
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent) {
                    if (intent.action == BluetoothDevice.ACTION_FOUND) {
                        val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)

                        //se la lista di device non contiene gia aggiornala altrimenti non la aggiornare
                        var btName = device?.name

                        //filtra sulla base del nome e se RPE elimina i primi quattro caratteri
                        if((btName==null)||!(btName.startsWith("RPE"))){return}
                        else{btName = btName.drop(4)}

                        val btAddress = device!!.address
                        var paired = false

                        Log.d("giuseppePaired", " TROVATA e trovata nelle devices ${pairedDevices?.map { it.name }} e device ${device.name} e contenuta ${pairedDevices!!.contains(device)} ")
                        if((pairedDevices!=null)&&(pairedDevices!!.contains(device))) { paired = true} //se contenuta modifica attributo
                        val btDeviceData = DeviceData(btName, btAddress, paired)

                        if(!btDevicesList.contains(btDeviceData)){ btDevicesList.add(btDeviceData)
                            btDevicesListLive.value=btDevicesList
                            //Log.d("giuseppeRecycler", "trovata ${device.name} che non contiene e dimensioni lista ${btDevicesListLive.value!!.size} e lista ${btDevicesListLive.value!!}")
                        }
                    }
                    //modifica variabile di fine ricerca per visualizzazione
                    if (intent.action== ACTION_DISCOVERY_STARTED) {
                        Log.d("giuseppeMessage", "iniziata START_Action_Discovery e valore $cancelRequest")
                        //se connesso mette subito la prima device attuale
                        if(connected.value==true){ val btDevice = DeviceData(connectedDevice!!.name.drop(4), connectedDevice!!.address, true ); btDevicesList.add(btDevice);btDevicesListLive.value=btDevicesList  }
                        cancelRequest=false;  _discovering.value = true;}
                    if (intent.action == ACTION_DISCOVERY_FINISHED) {
                        //se la fine non e' dovuta a richiesta di cancellazione, ma e' naturale invia valore
                        Log.d("giuseppeMessage", "FINITA Action_Discovery e valore $cancelRequest")
                        //if((cancelRequest!=null)&&(!cancelRequest!!)) {_discovering.value = false}}
                        if(!cancelRequest) {btDevicesListLive.value=btDevicesList; _discovering.value = false} }

                    if(intent.action == BluetoothDevice.ACTION_BOND_STATE_CHANGED){
                        val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                        val bonded = mAdapter!!.bondedDevices
                        val modificato = pairedDevices!!.size!=bonded.size
                        val accoppiato = bonded.contains(device)
                        val deviceData = DeviceData(device!!.name, device.address )
                        if (modificato) {
                            if (btDevicesList.contains(deviceData)){
                                Log.d("giuseppePaired5", "cambiato lo stato di un device ${device!!.name} e $accoppiato e bonded ${bonded.map { it.name }} e contenuto nella lista")
                                btDevicesList.map { if(it==deviceData){it.paired=accoppiato } }
                                btDevicesListLive.value = btDevicesList
                            }
                        }
                        pairedDevices=bonded //si aggiorna il valore della lista dei paired
                    }
                }
            }
            var filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
            context.registerReceiver(receiver, filter)
            filter = IntentFilter(ACTION_DISCOVERY_FINISHED)
            context.registerReceiver(receiver, filter)
            filter = IntentFilter(ACTION_DISCOVERY_STARTED)
            context.registerReceiver(receiver, filter)
            filter = IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
            context.registerReceiver(receiver, filter)
            /*awaitClose {
                context.unregisterReceiver(receiver)
            }*/
        }}

    fun connetti(deviceData: DeviceData){
        uiScope.launch { connect(deviceData) }
    }

    suspend fun connect(deviceData: DeviceData) {
        return withContext(Dispatchers.Default) {
            //Log.d("giuseppeViewModel", "lanciato connect di service")

            if (mConnectedThread != null) {
                mConnectedThread!!.cancel(); mConnectedThread?.join()
            }
            if (mConnectThread != null) {
                mConnectThread!!.cancel()
            }

            //if(prima) btDevice = pairedDevices!!.elementAt(0)
            //else btDevice = pairedDevices!!.elementAt(1)


            val device = mAdapter!!.getRemoteDevice(deviceData.deviceHardwareAddress)
            btDevice = device

            mConnectThread = ConnectThread(device)

            mConnectThread!!.start()
            mConnectThread!!.join()
            prima = !prima

            //SI PUO' ANCHE METTERE QUI .....
            mSocket?.let {
                mConnectedThread = ConnectedThread(mSocket!!)
                mConnectedThread!!.start()
                connectedDevice = btDevice
            }
        }
    }

    //per disconnettersi
    fun disconnect(){
        riconnessione = false
        if(mConnectThread!=null){mConnectThread!!.cancel(); mConnectThread!!.join() }

        if(mConnectedThread!=null){
            mConnectedThread!!.cancel();

            //termina anche il thread di connessione rilanciato
            mConnectedThread!!.join()
            if(mConnectThread!=null){mConnectThread!!.cancel(); mConnectThread!!.join() } //ridondante?

            //Log.d("giuseppeConnected", "abbiamo aspettato il termine del thread connected con join() e il valore del thread e' ${mConnectedThread} e il valore di lavoro ${lavoro?.isActive} e il valore di connect ${mConnectThread}")
        }
        //mConnectThread.cancel()
        //mConnectedThread?.cancel()
        lavoro?.cancel()
        lavoro = null
        //Log.d("giuseppeConnected", "adesso il valore di lavoro ${lavoro?.isActive} e valore ${lavoro}")
        riconnessione= true
    }

    inner class ConnectThread(device: BluetoothDevice) : Thread() {

        var deviceData = DeviceData(device.name, device.address)

        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            device.createRfcommSocketToServiceRecord(MY_UUID_SECURE)
        }
        init {
            Log.d("giuseppeVerifica", "inizializzato socket")
        }

        public override fun run() {

            mmSocket?.let { socket ->
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                //Log.d("giuseppeViewmodel", "iniziato tentativo di connessione: socket connesso ${socket.isConnected}")
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    mmSocket?.connect()
                    //Log.d("giuseppeViewmodel", "provo a connettermi")

                } catch (e: IOException) {
                    // Close the socket
                    try {
                        mmSocket?.close()
                    } catch (e2: IOException) {
                        Log.e(TAG, "unable to close() socket during connection failure", e2)
                        Log.d("giuseppe", "incapace di chiudere il socket durante il fallimento di connessione")
                    }
                    //fine tentativo per fallimento connessione
                    fineTentativo.postValue(deviceData)  //segnala fine tentativo
                    //Log.d("giuseppeViewModel", "fallita connessione")
                    //connectionFailed()
                    return
                }
                Log.d("giuseppeVerifica", "Finito di connettersi")
                //Log.d("giuseppeConnected", "completato processo di connessione: socket connesso ${socket.isConnected}")
                //cambia calore di connected e sospende riconnessione
                connected?.postValue(true)
                lavoro?.cancel() //sospende riconnessione
                mSocket = socket
                /*mSocket?.let {
                    mConnectedThread= ConnectedThread(mSocket!!)
                    mConnectedThread!!.start()
                    connectedDevice=btDevice
                }*/
                // The connection attempt succeeded. Perform work associated with
                // the connection in a separate thread.
                //manageMyConnectedSocket(socket)
            }
        }

        // Closes the client socket and causes the thread to finish.
        fun cancel() {
            try {
                mmSocket?.close()
                //Log.d("giuseppeConnected", "connectThread chiuso")
            } catch (e: IOException) {
                Log.e("giuseppe", "Could not close the client socket", e)
            }
        }
    }

    //manda un log quando riceve un messaggio
    inner class ConnectedThread(private val mmSocket: BluetoothSocket) : Thread() {

        private val mmInStream: InputStream = mmSocket.inputStream
        private val mmOutStream: OutputStream = mmSocket.outputStream
        private var mmBuffer: ByteArray = ByteArray(1024) // mmBuffer store for the stream

        init {

        }

        override fun run() {
            //Log.d("giuseppeConnected", "Thread connesso")
            var numBytes: Int // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                // Read from the InputStream.
                numBytes = try {
                    mmInStream.read(mmBuffer)
                } catch (e: IOException) {
                    //Log.d(TAG, "Input stream was disconnected", e)
                    connected.postValue(false)
                    //Log.d("giuseppeConnected", "passato per catch di connected")
                    if(riconnessione) {riconnessione(btDevice!!)}
                    break
                }
                // Send the obtained bytes to the UI activity.
                //GIUSEPPE RIPRISTINARE
                //val readMsg = handler.obtainMessage(MESSAGE_READ, numBytes, -1, mmBuffer)
                //readMsg.sendToTarget()
                messaggioLetto = mmBuffer.take(numBytes).toByteArray().toHex2()
                Log.d("giuseppePacketFrameTest", "messaggio letto: $messaggioLetto")
                impostareStringaLive(messaggioLetto)
                mmBuffer = ByteArray(1024)
            }
        }

        // Call this from the main activity to send data to the remote device.
        fun write(bytes: ByteArray) {
            try {
                mmOutStream.write(bytes)
            } catch (e: IOException) {
                Log.e(TAG, "Error occurred when sending data", e)

                // Send a failure message back to the activity.
                //GIUSEPPE DA RIPRISTINARE
                /*val writeErrorMsg = handler.obtainMessage(MESSAGE_TOAST)
                val bundle = Bundle().apply {
                    putString("toast", "Couldn't send data to the other device")
                }
                writeErrorMsg.data = bundle
                handler.sendMessage(writeErrorMsg)*/
                return
            }

            // Share the sent message with the UI activity.
            //GIUSEPPE RIPRISTINARE
            /*val writtenMsg = handler.obtainMessage(MESSAGE_WRITE, -1, -1, mmBuffer)
            writtenMsg.sendToTarget()*/
            Log.d("giuseppeViewmodel", "buffer $mmBuffer")
        }

        // Call this method from the main activity to shut down the connection.
        fun cancel() {
            try {
                //Log.d("giuseppeConnected", "iniziato procedura di cancellazione thread connesso CHIUSURA SOCKET")
                mmSocket.close()
                //Log.d("giuseppeConnected", "avvenuta richiesta di chiusura socket")
                connected.postValue(false)
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the connect socket", e)
            }
        }
    }

    fun ByteArray.toHex2(): String = asUByteArray().joinToString("") { it.toString(radix = 16).padStart(2, '0') }

    fun String.decodeHex(): ByteArray {
        check(length % 2 == 0) { "Must have an even length" }

        return chunked(2)
            .map { it.toInt(16).toByte() }
            .toByteArray()
    }

}