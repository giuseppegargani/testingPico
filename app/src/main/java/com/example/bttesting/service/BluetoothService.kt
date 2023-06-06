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