package com.example.bttesting

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.preference.PreferenceManager
import com.example.bttesting.databinding.ActivityMainBinding
import com.example.bttesting.service.BluetoothService
import com.example.bttesting.viewModels.BluetoothViewModel
import com.example.bttesting.viewModels.BluetoothViewModelFactory
import java.util.*

class MainActivity : AppCompatActivity() {

    private var mBtService: BluetoothService? = null
    private lateinit var btViewModelFactory: BluetoothViewModelFactory
    private lateinit var btViewModel: BluetoothViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val permissionRequestLocation = 123
    private val permissionRequestLocationKey = "PERMISSION_REQUEST_LOCATION"
    private var alreadyAskedForPermission = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // verifica richiesta di permesso di localizzazione...ma perche' savedInstanceStare e non sharedPreferences!?!?
        if (savedInstanceState != null) {
            alreadyAskedForPermission = savedInstanceState.getBoolean(permissionRequestLocationKey, false)
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)     //da mettere anche in fondo...

        //si crea una istanza di btService e si passa a ViewModel attraverso la factory (il viewModel ha il lifecycle di Activity)
        mBtService = BluetoothService(this)
        btViewModelFactory = BluetoothViewModelFactory(mBtService!!,this.application, this)
        btViewModel = ViewModelProvider(this, btViewModelFactory).get(BluetoothViewModel::class.java)

        //per usare liveData nella View (di mainActivity) ....ma probabilmente non ce ne sarà bisogno...perchè non metteremo nessun Observer..o forse qualche elemento (View) comune a tutti i fragment
        binding.lifecycleOwner = this
        binding.bluetoothViewModel = btViewModel  //per usare Databinding

        checkPermissions()

        var linguaDefault = "en"
        linguaDefault=setDefaultLanguage() //prende la lingua del tablet

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val booleanoInserito = sharedPreferences.getBoolean("boolean_license_key",false)
        val linguaggio: String = sharedPreferences.getString("language", linguaDefault)!!

        //e cambia la lingua sulla base delle preferenze impostate
        cambioLingua(linguaggio)

        if(booleanoInserito==false){
            checkLicenseDialog(this, sharedPreferences)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //quando termina l'attività toglie il BroadcastReceiver
        mBtService?.unregisterReceiver()
        mBtService?.viewModelJob?.cancel() //verificare dove (a seconda del lifecycle)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.myNavHostFragment)
        return NavigationUI.navigateUp(navController, appBarConfiguration)
    }

    //Cambio lingua per locale - Si puo' anche selezionare la lingua dal dialog!!! se si volesse implementare
    fun cambioLingua(lang: String){
        val config = resources.configuration
        //val lang = "fa" // your language code
        val locale = Locale(lang)
        Locale.setDefault(locale)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            config.setLocale(locale)
        else
            config.locale = locale

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            createConfigurationContext(config)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun checkPermissions() {

        if (alreadyAskedForPermission) {
            // don't check again because the dialog is still open
            return
        }

        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {

            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.need_loc_access))
            builder.setMessage(getString(R.string.please_grant_loc_access))
            builder.setPositiveButton(android.R.string.ok, null)
            builder.setOnDismissListener {
                // the dialog will be opened so we have to save that
                alreadyAskedForPermission = true
                requestPermissions(arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), permissionRequestLocation)
            }
            builder.show()

        } else {
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {

            permissionRequestLocation -> {
                // the request returned a result so the dialog is closed
                alreadyAskedForPermission = false
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    //Log.d(TAG, "Coarse and fine location permissions granted")
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle(getString(R.string.fun_limted))
                        builder.setMessage(getString(R.string.since_perm_not_granted))
                        builder.setPositiveButton(android.R.string.ok, null)
                        builder.show()
                    }
                }
            }
        }
    }

    /**
     * Dialog per la verifica della chiave di licenza
     */
    fun checkLicenseDialog(c: Context, sharedPreferences: SharedPreferences) {

        val taskEditText = EditText(c)
        val dialog: AlertDialog = AlertDialog.Builder(c)
            .setCancelable(false)
            .setTitle(R.string.license_title)
            .setMessage(R.string.license_message)
            .setView(taskEditText)
            .setPositiveButton(R.string.license_positive_button, null )
            .create().apply {
                setOnShowListener {
                    getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {

                        var chiaveInserita = taskEditText.text.toString()

                        val booleanoVerifica: Boolean= verificaLicenza(chiaveInserita)
                        if(booleanoVerifica){
                            Toast.makeText(c, R.string.license_positive_result, Toast.LENGTH_SHORT).show()
                            //modifica il valore memorizzato
                            with (sharedPreferences.edit()) {
                                putBoolean(getString(R.string.boolean_license_key), true)
                                apply()
                            }
                            dismiss()
                        }
                        else {
                            Toast.makeText(c, R.string.license_negative_result, Toast.LENGTH_SHORT).show()
                            taskEditText.text.clear()
                        }
                    }
                }
            }
        dialog.show()
    }

    val listaChiavi: List<String> = listOf("XonKv2GR", "JYB4yGDp", "gcFgjWuk", "wC7hV42p", "c99SHKUN", "W7XPnDsm", "ykfxDPUU", "ATHsYDgt", "grhdeRTC", "zAMEKtgo", "i6SS7UhW", "cchDajTL", "fkxRsTtp",
        "T4bNgro6", "6Zv9NCXo", "xfcaUpfZ", "Yzza5KAd", "J2F3q8J8", "PCjSj9pN", "FgcxCrjW", "3Vj6zXP8", "6Sz7krXi", "w3n2C6BH", "EcEEbPg9", "2LkrRDNL", "xMfrUK8Z", "uaRSbq8Q", "jFWhK2ys", "P5n876QR", "XrLBA8c4", "uapZoN7x", "8vVE4Wzb",
        "enykJak7", "QR8LDq6T", "Yvu6zSK9", "Xm4fSzdA", "dfFYkaHi", "ipcd3aVv", "BB3ZMC89", "tc8vk8mz", "SLCch52L", "bNa76fP7", "pJogHShv", "LPTcbbVU", "jktsjtnG", "kyEAFrNw", "ajnbrNDk", "dQMGS8Ce", "8zGqdhQ8", "25nPQBaU", "roxf5jJJ",
        "KS6PtxPm", "Td9tnumU", "VovNhYmH", "BctvETgN", "sdTnBHZP", "ruqS7xWy", "uyq3QPMJ", "hovDKwmT", "BreKRyoV", "Ff7bXxU9", "2PGDDhiU", "9Cm9JBgh", "vubXkXG6", "NTtTvpBZ", "8t3x49aU", "GZzGibCC", "amCBw74D", "eghAJiLR", "t3S8sxjp",
        "v6QAK5N4", "ai3AiHXv", "XGozhBSc", "Qgj9e3DP", "dj697W7T", "2gPXdMEQ", "roX7reWj", "BkAxu3cA", "Z7Cw54TP", "ab2pbNEr", "jbngoxZW", "3huzBBar", "BvU7TvbT", "z8cmXs5z", "NVa7u5XB", "jkeq6Ktq", "jfNSGjnS", "HU4ebeu2", "ycQCk4mw",
        "EstemXvN", "dHFupffi", "pGSquoB6", "EDKck3Gi", "Gz6xsuqu", "mjyFwrWS", "Ap95WYFX", "2kzzKFbj", "WFwbrd7M", "2tpNQA6n", "uQeLxX9P" )

    /**
     * Verifica della chiave inserita sulla base di un elenco di 100 chiavi
     */
    private fun verificaLicenza(valoreInserito: String ):Boolean {
        if(listaChiavi.contains(valoreInserito)) { return true }
        return false
    }

    /**
     * Dopo aver letto il valore esteso della lingua impostata nel tablet restituisce il codice corrispondente (es. italiano -> it)
     */
    private fun setDefaultLanguage():String {
        return when (Locale.getDefault().displayLanguage) {
            "english" -> "en"
            "italiano" -> "it"
            else -> "en"
        }
    }
}

