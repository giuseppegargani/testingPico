package com.example.bttesting

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
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

/* DA FARE:

*/

/* IDEE E LINKS:

    1 - Pubblicazione su Playstore, versione chiave di Bundle: (per ricordarsi su come fare)
        https://stackoverflow.com/questions/68057944/version-code-1-has-already-been-used-try-another-version-code
        https://stackoverflow.com/questions/52507156/your-android-app-bundle-is-signed-with-the-wrong-key-ensure-that-your-app-bundl

    2 - Generatore di lista di numeri licenza RANDOM:
        https://www.randomcodegenerator.com/en/generate-serial-numbers

    3 - GET THE CURRENT LANGUAGE DAL DEVICE:
        https://stackoverflow.com/questions/4212320/get-the-current-language-in-device

    4 - PLACEHOLDERS PER STRINGHE (GetString())
        https://stackoverflow.com/questions/3656371/is-it-possible-to-have-placeholders-in-strings-xml-for-runtime-values

    5 - Quale e' la versione migliore di Bluetooth?!?!  impostazioni

    6 - SI DEVE TESTARE LA VELOCITA' DI CONNESSIONE DI DISPOSITIVI
    - velocità di connessione con coroutines
    - 10 connessioni entro 30 secondi OK
    - stabilità nella navigazione (navigare nelle diverse attività)
    - stabilità nell'invio dei dati!!! (invio continuo di una telefonata)
    - TESTARE CONTINUITA' IN USO INTENSIVO

    7 - CREARE UN SERVICE VERO E PROPRIO E POI SI DEVE RIMUOVERE IN ONDESTROy?

    8 - COMUNICAZIONE TRA ACTIVITY E FRAGMENT TRAMITE VIEWMODEL ISTANZIATO CON FACTORY
    https://developer.android.com/guide/fragments/communicate



*/

/* SI DEVONO RIMUOVERE I LISTENERS SU VIEWMODEL? IN ONDESTROY
    Qui il concetto e' memory Leaks!!

 */

/* LiveData e ViewModel di Activity e Observer comuni a tutti i fragment
https://stackoverflow.com/questions/62591052/should-i-add-binding-lifecycleowner-this-when-i-use-viewmodel
 */

/*SHARED VIEWMODEL E' CORRETTO PER APP PICCOLE MA PER meno corretto per le GRANDI, ogni fragment elabora dati da service in modo diverso e complesso!!!!!!!
    quindi service fornisce delle informazioni e poi i singoli viewmodels le elaborano in modi diversi e complessi!!!!!!
    IL CICLO DI VITA DEI SINGOLI VIEWMODEL E' COLLEGATO AI SINGOLI FRAGMENT
    https://www.youtube.com/watch?v=THt9QISnIMQ
 */

/*VIEWMODEL MULTIPLI CON DIFFERENT SCOPE
    https://stackoverflow.com/questions/51712533/is-it-a-bad-practice-to-have-multiple-viewmodels-approximately-one-for-each-fra/51712624
    E discussione di Fiorina Montescu di Google (vedi sopra)   almeno un ViewModel per fragment (oppure uno comune)
    https://www.reddit.com/r/androiddev/comments/akd9ok/is_using_multiple_viewmodels_in_fragment/
 */

/* process death and Viewmodel (video)
https://www.youtube.com/watch?v=sLCn27DceRA
 */

/*IL PARADIGMA MIGLIORE E' RX E NON LIVE DATA
    Pag.317 Libro Carli    possibilità di gestire gli errori
 */

/* SI POTREBBE USARE UN SERVICE VERO (ma secondo me non si sente la necessità)
 */

/* IMPOSTATO VERIFICA ID PAGINA E SELEZIONA ELEMENTO DI MENU
    in realtà si devono mettere figli e genitore
 */

/* Layout personalizzato di menu item
https://www.corsoandroid.it/creare_il_layout_di_un_navigation_drawer_in_5_passi.html
https://abhiandroid.com/materialdesign/navigation-drawer   interessante!!!!!!!!!!!!!!!!!!!!
https://stackoverflow.com/questions/30594025/how-to-customize-item-background-and-item-text-color-inside-navigationview
https://stackoverflow.com/questions/32042794/changing-text-color-of-menu-item-in-navigation-drawer
https://stackoverflow.com/questions/38336538/get-current-selected-item-of-navigation-drawer-in-android

 */

/*ARTICOLI INTERESSANTI:
https://canlioya.medium.com/

 */

/* SHARED PREFERENCES
    SHARED PREFERENCES DENTRO ACTIVITY (o meglio metterlo dentro Fragment)?
    Dove si dovrebbe inseire dentro onCreate? in quale posizione?

    Link interessanti:
    https://www.digitalocean.com/community/tutorials/android-shared-preferences-example-tutorial
    https://www.digitalocean.com/community/tutorials/android-sharedpreferences-kotlin
    https://heartbeat.comet.ml/sharedpreferences-in-android-818e3b614b85
    https://developer.android.com/training/data-storage/shared-preferences
    https://developer.android.com/reference/android/content/SharedPreferences

    Ottenere tutte le chiavi di Shared Preferences:
    https://stackoverflow.com/questions/22089411/how-to-get-all-keys-of-sharedpreferences-programmatically-in-android

    Utilizzo generale:
    https://stackoverflow.com/questions/5950043/how-to-use-getsharedpreferences-in-android

    Utilizzo non da activity:  (con un altro context)
    https://stackoverflow.com/questions/69205022/how-to-reference-getsharedpreferences-from-a-static-context-that-is-not-an-act

 */

/* CHIAVE LICENZA UNICA E GOOGLE PLAY LICENSE

    LA CHIAVE DI LICENZA SI MODIFICA AD OGNI VERSIONE!!!!!!!!!!!!
    In questo modo permette di tenere aggiornati sui servizi (marketing)
    Al modificare delle versioni su Playstore riaggiorna l'app e si puo' fissare di reinserire una nuova password (o mantenere la solita)

    Generare una license key:
    https://keygen.sh/blog/how-to-generate-license-keys/

    Articolo originale:
    https://www.brandonstaggs.com/2007/07/26/implementing-a-partial-serial-number-verification-system-in-delphi/

    Playground Js e Kotlin:
    https://codesandbox.io/s/pkv-in-js-lfl5nx?file=/src/index.js:3500-3565
    https://www.mycompiler.io/new/kotlin?fork=ILSj2FUNud1

    DOCUMENTAZIONE UFFICIALE!!!!!!!!!!
    https://developer.android.com/google/play/licensing/client-side-verification
    https://developer.android.com/reference/kotlin/javax/crypto/KeyGenerator

    Altri articoli:
    https://stackoverflow.com/questions/2642259/looking-for-a-license-key-algorithm
    https://stackoverflow.com/questions/3383608/algorithm-for-activation-key-security
    https://stackoverflow.com/questions/3002067/how-are-software-license-keys-generated

    Importare CryptoJs in Javascript:
    https://stackoverflow.com/questions/62905663/how-to-import-crypto-js-in-either-a-vanilla-javascript-or-node-based-javascript

    - OPERATORI BITWISE:
    VERIFICARE SE ESATTAMENTE LO STESSO COMPORTAMENTO DI JS (Unsigned)
    Quadro generale: https://www.baeldung.com/kotlin/bitwise-operators
    Manipolazione bits: https://en.wikipedia.org/wiki/Bit_manipulation
    ParseInt e TOINT: https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.js/parse-int.html (adesso si usa toInt)
    SUBSTRING: https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/substring.html
    PADSTART: https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/pad-start.html
    UPPERCASE: https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/uppercase.html
    & 0x00FF https://www.baeldung.com/java-and-0xff#:~:text=1.-,Overview,0xff%20in%20binary%20is%2011111111.

    - CONVERTITORE ESADECIMALE A DECIMALE
    https://www.rapidtables.com/convert/number/decimal-to-hex.html

    - COMPLEMENTO A 2 CALCOLATORE
    https://www.omnicalculator.com/math/twos-complement

    - PLAYGROUND JS
    XOR ^   https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Operators/Bitwise_XOR
    VARI OPERATORI: https://www.w3schools.com/js/js_operators.asp
    0xFF  e MASK!!!   https://stackoverflow.com/questions/4058339/what-is-0xff-and-why-is-it-shifted-24-times
    >> Right Shift Operator: https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Operators/Right_shift

 */

/* DIALOG, ALERTDIALOG CON INPUT
    https://stackoverflow.com/questions/10903754/input-text-dialog-android

    Fare in modo che non chiuda il dialog:
    https://stackoverflow.com/questions/2620444/how-to-prevent-a-dialog-from-closing-when-a-button-is-clicked
    https://www.geeksforgeeks.org/how-to-prevent-alertdialog-box-from-closing-in-android/
    https://www.digitalocean.com/community/tutorials/android-alert-dialog-using-kotlin

    Pulire input text:
    https://stackoverflow.com/questions/5308200/clear-text-in-edittext-when-entered
 */

/* KEYGEN Documentazione :

    - REGEX MATCH (per verificare con Regex!!)
    https://www.baeldung.com/kotlin/regular-expressions

    - LONG E BIGINTEGRAL
    https://stackoverflow.com/questions/31748028/long-vs-biginteger

    - LONG E OPERATORI BITWISE
    https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/
    https://www.programiz.com/kotlin-programming/bitwise
    https://www.baeldung.com/kotlin/bitwise-operators

    - CONVERTIRE INT IN STRINGHE HEX
    https://www.techiedelight.com/convert-int-to-a-hex-string-in-kotlin/
    https://www.baeldung.com/kotlin/int-to-hex-string

    - ESPERIENZA PERSONALE CON COMPOSE!!!
    https://www.composables.co/blog/compose-desktop

    - COVNERTIRE STRINGHE HEX IN LONG
    https://stackoverflow.com/questions/41651704/kotlin-parse-hex-string-to-long

    - PARSEINT
    https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/parseInt

    - CONVERTIRE BYTEARRAY IN HEX
    https://www.baeldung.com/kotlin/byte-arrays-to-hex-strings
    https://www.programiz.com/kotlin-programming/examples/convert-byte-array-hexadecimal

    - SECURERANDOM
    https://www.knowledgefactory.net/2021/09/kotlin-random-and-secure-random-with.html
    https://developer.android.com/reference/kotlin/java/security/SecureRandom   !!!!!!!

    - CRYPTO.RANDOMBYTES
    https://www.tutorialspoint.com/crypto-randombytes-method-in-node-js
    https://www.geeksforgeeks.org/node-js-crypto-randombytes-method/

    - CONVERTIRE CHAR TO INT
    https://blog.jdriven.com/2019/10/converting-char-to-int-in-kotlin/
    https://www.techiedelight.com/convert-char-to-its-ascii-code-in-kotlin/
    https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char/

    - CONVERTITORE HEX TO DECIMALI
    https://www.rapidtables.com/convert/number/hex-to-decimal.html

 */

/* KEYGEN DA FARE!!!!!
    - CAMBIARE I NOMI DELLE VARIABILI E METTERLI PIU' SIGNIFICATIVI!!!!!!!
    - SEMPLIFICARE LE FUNZIONI!!! ED ORDINARLE!!!!
    - QUANDO SI ESEGUE QUALCHE COMANDO SU UNA VARIABILE METTERE CONDIZIONI DI APPLICABILITà (altrimenti return)!!!!!!!!!!!!!!!!
 */

/* VEDERE CONTROLLO E TEST LICENZA USANDO PLAYSTORE (anche con verifica lato server)!!!!!
    Come si controlla a chi sospendere la licenza? fare tests!!
    https://developer.android.com/google/play/licensing
 */

/* PRVENIRE LA REVERSE ENGINEERING
    https://proandroiddev.com/how-to-prevent-hackers-from-reverse-engineering-your-android-apps-2981661ab1c2

 */

/* VERIFICARE MANIFEST E PROVIDER PER INVIO FILES IN HISTORICAL
    https://stackoverflow.com/questions/56598480/couldnt-find-meta-data-for-provider-with-authority
 */

/* SI DEVE modificare la chiave e creare una lista di chiavi

 */

/* Ordine di verifica (chiave o permission). Cosa Cambia?
    RIGUARDO AL CONTROLLO SULLA CHIAVE...
    commenti dal codice ma si deve ragionare

     //verifica se e' stata inserita la license key!! o si dovrebbe mettere prima rispetto al controllo su permission? come ordine
        //BISOGNA PREVEDERE IL CASO IN CUI LA CHIAVE NON FOSSE PRESENTE!!!! mettendo un valore di default!!!!!! una stringa di default da qualche parte (e verificarla)
        //REGISTRARE IN MODALITà SICURA!!!! con ENCRYPTEDSHAREDPREFERENCES (https://developer.android.com/training/data-storage/shared-preferences)
        //inizialmente creiamo due variabili (una booleana) per poterci lavorare meglio!!! ma quella finale sarà il solo booleano che inizialmente falso, lancerà una funzione da dialog fragment!!! per la verifica della correttezza!!!
        //SEMPLIFICARE STRINGHE DEI NOMI DENTRO XML!!!!!! soprattutto per il booleano!!!
        //si deve semplificare tutta questa parte mettendola dentro una seconda funzione rispetto a quella di verifica del codice!!!
        //DIALOG FRAGMENT SI DEVE FARE PERSONALIZZATO?? O per il momento si puo' lasciare AlertDialog??      CUSTOMIZZAZIONE!!!!
        //Sulla base dell'esempio in historicalFragment e dialogo RinominaFile

        //IN REALTà PENSO CHE VENGA USATO SOLAMENTE BOOLEAN (che viene modificato nel caso che venga inserita una chiave corretta)
        //per questo motivo sono state inserite due volte - ma penso che si possa inserire un solo getDefaultSharedPreferences!!!! DEFAULT
        //val licenseShared = this?.getSharedPreferences(getString(R.string.license_key), Context.MODE_PRIVATE) ?: return
        //valore di default
        //val defaultString = resources.getString(R.string.default_license_key) //semplicemente una stringa di default!!! da resources!!!

        //val stringaPrecedente = licenseShared.getString(getString(R.string.license_key), defaultString)

        /*with (licenseShared.edit()) {
            putString(getString(R.string.license_key), "inserito secondo valore")
            apply()
        }*/
        /*with (licenseBoolean.edit()) {
            putBoolean(getString(R.string.boolean_license_key), false)
            apply()
        }*/
        //val stringaInserita = licenseShared.getString(getString(R.string.license_key), defaultString)
        //Log.d("giuseppeChiave","Questi sono i valori; Chiave precendentemente memorizzata: ${stringaPrecedente}  Chiave attualmente memorizzata: ${licenseShared} e questo il valore default ${defaultString} e inserita: $stringaInserita")

        //caricata libreria in gradle: implementation 'androidx.preference:preference-ktx:1.2.0' (altrimenti deprecato!!!!)
        //vedi: https://stackoverflow.com/questions/56833657/preferencemanager-getdefaultsharedpreferences-deprecated-in-android-q
        /*
        val licenseBoolean = this?.getSharedPreferences(getString(R.string.boolean_license_key), Context.MODE_PRIVATE) ?: return
        val defaultBoolean = resources.getBoolean(R.bool.default_boolean_license_key)//default del booleano - Ma non si puo' mettere hardcoded?!?!?!
        val booleanoPrecedente = licenseBoolean.getBoolean(getString(R.string.boolean_license_key), defaultBoolean)
        Log.d("giuseppeChiave", "Valori Booleano preferences: precedente: $booleanoPrecedente, chiave attualmente memorizzata: $licenseBoolean, valore di default: $defaultBoolean e inserita: $booleanoInserito")
        val booleanoInserito = licenseBoolean.getBoolean(getString(R.string.boolean_license_key), defaultBoolean)
        */


 */

/* ALTRO CODICE PRECEDENTE
//--------------------------------------------------- VERIFICA KEYGEN ---------------------------------------------------------------------------------------------


    //Questo poi si riduce ad una semplice verifica della presenza in una lista di cento elementi!!!!

    //SEMPLIFICARE LE FUNZIONI!!!!!!!! RIDURNE IL NUMERO!!!!!
    // ORDINARLE E PULIRLE

    //VERIFICA DELLA LUNGHEZZA
    //ACCORCIARE IN UNA RIGA SOLA!!!
    //NON C'è BISOGNO DI UNA FUNZIONE SEPARATA E SI PUO' METTERE ALL'INTERNO DELLA FUNZIONE FINALE
    fun verificaLunghezza(key: String):Boolean {
        val rimpiazzo: String = key.replace("-","")
        val risultatoVerifica: Boolean = (key.length == 24) && (rimpiazzo.length == 20)
        return risultatoVerifica
    }

    /* function isSeedFormatValid(seed) {
            return seed.match(/[A-F0-9]{8}/) != null
            }
         */
    //verifica che sia composto di otto caratteri (NE DI PIU' NE DI MENO!!!) E CHE NON SIA NULLO!!!!!!  a partire dal parametro seme
    //TOGLIERE DEFAULT A PARAMETRO!!!!!!!!!!!!!!!!!!!!!!
    fun verificaFormatoSeme(seme:String? = "5555343D"): Boolean {
        //si verifica direttamente su una stringa
        val regex = """[A-F0-9]{8}""".toRegex()
        if(seme == null) {return false} //SI PUO' ANCHE SEMPLIFICARE O TOGLIERE!!!!!!!!!!!!
        val risultato: Boolean = regex.matches(seme)
        return risultato
    }

    //PER CALCOLO CHECKSUM
    fun getCheckSumSerial(serial: String): String {

        //Valori iniziali di right!!!! a cui sommare     SONO STRINGHE!!!!
        var right:Int = Integer.decode("0x00af"); // 175
        var left:Int = Integer.decode("0x0056"); // 86

        /*per convertire in numero si usa il seguente comando:
        var rightConverted: Int = Integer.decode(right)*/
        Log.d("giuseppeKeygen","valori iniziali. right: ${right} left: ${left}")
        Log.d("giuseppeKeygen","lunghezza seriale: ${serial.length}")

        for (i: Int in 0..serial.length-1) {
            //val charCodeAt: Int =
            //val c = 'q'
            //val convertito = c.toInt()
            val convertito: Int = serial[i].toInt()
            right += convertito

            if(right > (Integer.decode("0x00ff"))) { right -= (Integer.decode("0x00ff")) }

            left += right
            if(left > (Integer.decode("0x00ff"))) { left -= (Integer.decode("0x00ff")) }
            Log.d("giuseppeKeygen","una riga $i e lettera: ${serial[i]} convertito: ${convertito} right: ${right} left: ${left} ")
        }

        //return toFixedHex((left << 8) + right, 4);
        //SI DEVE CONVERTIRE IN NUMERO ESADECIMALE O NO??!!!!! Si puo' fare direttamente?
        Log.d("giuseppeKeygen","return: ${toFixedHex((left shl 8) + right, 4)}")
        return toFixedHex((left shl 8) + right, 4)
    }

    //funzione per formattare esadecimali (aggiungi 0 oppure taglia)
    //TOGLIERE DEFAULT A PARAMETRO!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    fun toFixedHex(numero: Int, leng: Int = 2):String {
        val convert:String = Integer.toHexString(numero).uppercase().padStart(leng, '0').substring(0,leng)
        return convert
    }

    /* PER LA VERIFICA DEL CHECKSUM
            function isSerialChecksumValid(serial, checksum) {
            const c = getChecksumForSerial(serial)
            return c === checksum
            }
        */
    fun verificaChecksum(serial:String, checksum: String):Boolean {
        val c:String = getCheckSumSerial(serial)
        Log.d("giuseppeKeygen5","dentro funzione verifica checksum. checksum: ${checksum} checksum calcolato: ${c}")
        return c == checksum
    }

    /* VERIFICA GENERALE DELLA CHIAVE
               function isKeyValid(key) {
         if (!isKeyFormatValid(key)) {
           return false
         }

         const [, serial, checksum] = key.replace(/-/g, '').match(/(.{16})(.{4})/)
         if (!isSerialChecksumValid(serial, checksum)) {
           return false
         }

         const seed = serial.substring(0, 8)
         if (!isSeedFormatValid(seed)) {
           return false
         }

         // Verify 0th subkey
         const expected = getSubkeyFromSeed(seed, 24, 3, 200)
         const actual = serial.substring(8, 10)
         if (actual !== expected) {
           return false
         }

           return true
       }

       */
    fun verificaKey(key: String): Boolean {

        //C'è BISOGNO DI METTERE LE PARENTESI GRAFFE???
        //qui mettiamo una verifica della lunghezza (con trattini e senza)
        if(!verificaLunghezza(key)) return false

        //In js c'era una assegnazione multipla e verifica del checksum (si toglievano i trattini e verificavano le misure dei due pezzi rimasti dopo aver preso i primi 16)
        //SI PUO' METTERE UNA CONDIZIONE AGGIUNTIVA MA VERIFICA ALLA RIGA PRECEDENTE!!!!!!!!!
        val ripulita: String = key.replace("-","")   //si tolgono i trattini
        val seriale: String = ripulita.take(16)
        val checksum: String = ripulita.takeLast(4)

        var verifchecks: Boolean = verificaChecksum(seriale, checksum)
        Log.d("giuseppeKeygen5", "verifica. ripulita: ${ripulita} seriale: ${seriale} checksum: ${checksum} verifica checksum: ${verificaChecksum(seriale, checksum)}"+
                " risultato verifica checksum: ${verifchecks}")
        if (!verificaChecksum(seriale, checksum)) return false

        /* qui estrae il seme (prime otto lettere dal seriale)
            const seed = serial.substring(0, 8)
            if (!isSeedFormatValid(seed)) {
            return false
            }
        */
        var seme: String = seriale.take(8)
        Log.d("giuseppeKeygen5", "seme: $seme VERIFICA SEME: ${verificaFormatoSeme(seme)}")
        if(!verificaFormatoSeme(seme)) return false

        /* VERIFICA SUBKEY
            // Verify 0th subkey
                const expected = getSubkeyFromSeed(seed, 24, 3, 200)
         */
        //prende la prima sottochiave!!!!!
        var actualSubkey1: String = seriale.substring(8, 10)
        var expectedSubkey1: String = getSubkeyFromSeed(seme, 24, 3, 200)

        Log.d("giuseppeKeygen5", "RIPULITA: $ripulita seriale: $seriale checksum $checksum seme: $seme actualSubkey1: $actualSubkey1 expectedSubKey: $expectedSubkey1")
        if(actualSubkey1 != expectedSubkey1) return false

        return true
    }

    //PER OTTENERE SUBKEY
    //LONG O BIGINTEGER??    !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    fun getSubkeyFromSeed(seed: String, a:Int, b:Int, c:Int): String {

        //mettere val invece di var!!!   ma dovrebbe essere in questa posizione
        //var seme:Int = seed.toInt(16) //SI USA SEME!!!!!!!!! perche' convertito da stringa!!!!
        //DOVREBBE ESSERE 2357612039
        /*var seme: Long = 0
        try {
            Log.d("giuseppeKeygen","CONVERSIONE: SEED: ${seed} SEME: ${seed.toLong(16)}")
            seme = seed.toLong(16) //SI USA SEME!!!!!!!!! perche' convertito da stringa!!!!
        }
        catch(ex: Exception) {
            Log.d("giuseppeKeygen","errore di conversione ${ex}")

        }*/

        //SI PUO' METTERE UN ALTRO MECCANISMO DI CONVERSIONE?? forse meglio?!?!
        var seme: Long = seed.toLong(16)

        val moduloA: Int = a % 25
        //val moduloB:Long = b % 3
        //var subkey: String? = null
        var numSubkey: Long?

        if(moduloA % 2 == 0){

            //subkey = ((seme >> a) & 0x000000ff) ^ (((seme >> b) | c) & 0xff);
            /*val numero: Int = (seme shr a) //di tipo Int!!!
            val numero2: Int = ((seme shr b) or c)
            val primaParte = (numero and 0x00ff)
            //prova ad aggiungere toByte() in fondo come conversione
            val primaParteToByte = primaParte.toByte()
            val secondaParte = (numero2 and 0x00ff)
            val unioneParti = (primaParte xor secondaParte )

            println("dentro funzione; seme: ${seme} numero1: ${numero} primaparte: ${primaParte} "+
            "primaPartetoByte: ${primaParteToByte}  b: ${b} c: ${c} ")
            println("dentro funzione; numero2: ${numero2} secondaParte: ${secondaParte} unioneParti ${unioneParti}")
            */

            //subkey = seed shr a and 0x000000ff xor (seed shr b or c and 0xff)
            //val aLong: Long = a.toLong()

            //val sintConver: Long = (seme.shr(a))
            //val sintesi: Long = ((seme.toLong() shr a.toLong()) and 0x000000ff.toLong())
            //val sintesi3 = (sintesi) xor (((seme shr b) or c) and 0x00ff)

            numSubkey = (seme.shr(a)) and 0x000000ff xor (seme.shr(b) or c.toLong() and 0xff)
            Log.d("giuseppeKeygen","dentro funzione; numSubkey: ${numSubkey}")
        }
        else {
            //subkey = ((seed >> a) & 0x000000ff) ^ ((seed >> b) & c & 0xff);
            //val sintesi2 = ((seme shr a) and 0x000000ff) xor (((seme shr b) and c) and 0x00ff)
            //numSubkey = sintesi2
            numSubkey = seme shr a and 0x000000ff xor (seme shr b and c.toLong() and 0xff)
            Log.d("giuseppeKeygen","dentro funzione; numSubkey: ${numSubkey}")
        }
        return toFixedHex(numSubkey.toInt(),2)
    }
*/

/* VERIFICA SU SHARED PREFERENCES: Se ci vuole la creazione di due variabili distinte con un foglio di valori xml, cosa puo' succedere,
    /*CAMBIA LINGUA PRIMA DEL DIALOG CON LA RICHIESTA DELLA CHIAVE (in questo modo si puo' modificare il messaggio di apertura)
        SE NULL SI DEVE IMPOSTARE IL VALORE DI DEFAULT!!!!!! COMUNQUE PREFERENCES VA IN SHARED PREFERENCES!!!!!! e quindi si puo' impostare con una schermata di icone ad hoc!!!!!
        MA NON E' DOPPIO??    !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        C'E' UN VALORE DI DEFAULT COME SECONDO PARAMETRO e quindi dovrebbe tornare mai null
        */
 */

/* Era presente una estensione su ByteArray che convertiva in esadecimali
    //Estensione su ByteArray creata per convertire in hexadecimali
    //https://www.baeldung.com/kotlin/byte-arrays-to-hex-strings
    /**
     * Estensione di Bytearray per convertire in esadecimali con il metodo toHex()
     */
    //fun ByteArray.toHex(): String = joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }
*/

/* MHANDLER: era presente anche un mHandler che e' stato tolto:
    //schema di un Handler dentro mainActivity  (ma ci vogliono le coroutines!!!)
    /*private val mHandler = @SuppressLint("HandlerLeak")
    object : Handler() {

        fun ByteArray.toHex2(): String = asUByteArray().joinToString("") { it.toString(radix = 16).padStart(2, '0') }
        fun String.decodeHex(): ByteArray {
            check(length % 2 == 0) { "Must have an even length" }

            return chunked(2)
                .map { it.toInt(16).toByte() }
                .toByteArray()
        }

    }*/
*/

/* NAVIGATIONBAR
        //NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        dentro onCreate

 */

class MainActivity : AppCompatActivity() {

    private var mBtService: BluetoothService? = null
    private lateinit var btViewModelFactory: BluetoothViewModelFactory
    private lateinit var btViewModel: BluetoothViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val PERMISSION_REQUEST_LOCATION = 123
    private val PERMISSION_REQUEST_LOCATION_KEY = "PERMISSION_REQUEST_LOCATION"
    private var alreadyAskedForPermission = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // verifica richiesta di permesso di localizzazione
        if (savedInstanceState != null) {
            alreadyAskedForPermission = savedInstanceState.getBoolean(PERMISSION_REQUEST_LOCATION_KEY, false)
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        drawerLayout = binding.drawerLayout
        val navController = this.findNavController(R.id.myNavHostFragment)
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        NavigationUI.setupWithNavController(binding.navView, navController)

        //modifica il colore della label del drawerMenu
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            var eleMenu: MenuItem? = binding.navView.menu.findItem(destination.id)
            eleMenu?.let {
                binding.navView.menu.findItem(destination.id).setCheckable(true).setChecked(true)
            }
        }

        //si crea una istanza di btService e si passa a ViewModel attraverso la factory (il viewModel ha il lifecycle di Activity)
        mBtService = BluetoothService(this)
        btViewModelFactory = BluetoothViewModelFactory(mBtService!!,this.application, this)
        btViewModel = ViewModelProvider(this, btViewModelFactory).get(BluetoothViewModel::class.java)

        //per usare liveData nella View (di mainActivity) ....ma probabilmente non ce ne sarà bisogno...perchè non metteremo nessun Observer..o forse qualche elemento (View) comune a tutti i fragment
        binding.lifecycleOwner = this
        binding.bluetoothViewModel = btViewModel  //per usare Databinding

        checkPermissions()

        var linguaDefault: String = "en"
        linguaDefault=setDefaultLanguage() //prende la lingua del tablet
        Log.d("giuseppeLingua", "valore lingua default: $linguaDefault")

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this /* Activity context */)
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
                ), PERMISSION_REQUEST_LOCATION)
            }
            builder.show()

        } else {
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {

            PERMISSION_REQUEST_LOCATION -> {
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
                        //Validate and dismiss

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

    //Si dovrebbe mettere segreto!?!?!?!?!? !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    //TOGLIERE 1 !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    val listaChiavi: List<String> = listOf("XonKv2GR", "JYB4yGDp", "gcFgjWuk", "wC7hV42p", "c99SHKUN", "W7XPnDsm", "ykfxDPUU", "ATHsYDgt", "grhdeRTC", "zAMEKtgo", "i6SS7UhW", "cchDajTL", "fkxRsTtp",
        "T4bNgro6", "6Zv9NCXo", "xfcaUpfZ", "Yzza5KAd", "J2F3q8J8", "PCjSj9pN", "FgcxCrjW", "3Vj6zXP8", "6Sz7krXi", "w3n2C6BH", "EcEEbPg9", "2LkrRDNL", "xMfrUK8Z", "uaRSbq8Q", "jFWhK2ys", "P5n876QR", "XrLBA8c4", "uapZoN7x", "8vVE4Wzb",
        "enykJak7", "QR8LDq6T", "Yvu6zSK9", "Xm4fSzdA", "dfFYkaHi", "ipcd3aVv", "BB3ZMC89", "tc8vk8mz", "SLCch52L", "bNa76fP7", "pJogHShv", "LPTcbbVU", "jktsjtnG", "kyEAFrNw", "ajnbrNDk", "dQMGS8Ce", "8zGqdhQ8", "25nPQBaU", "roxf5jJJ",
        "KS6PtxPm", "Td9tnumU", "VovNhYmH", "BctvETgN", "sdTnBHZP", "ruqS7xWy", "uyq3QPMJ", "hovDKwmT", "BreKRyoV", "Ff7bXxU9", "2PGDDhiU", "9Cm9JBgh", "vubXkXG6", "NTtTvpBZ", "8t3x49aU", "GZzGibCC", "amCBw74D", "eghAJiLR", "t3S8sxjp",
        "v6QAK5N4", "ai3AiHXv", "XGozhBSc", "Qgj9e3DP", "dj697W7T", "2gPXdMEQ", "roX7reWj", "BkAxu3cA", "Z7Cw54TP", "ab2pbNEr", "jbngoxZW", "3huzBBar", "BvU7TvbT", "z8cmXs5z", "NVa7u5XB", "jkeq6Ktq", "jfNSGjnS", "HU4ebeu2", "ycQCk4mw",
        "EstemXvN", "dHFupffi", "pGSquoB6", "EDKck3Gi", "Gz6xsuqu", "mjyFwrWS", "Ap95WYFX", "2kzzKFbj", "WFwbrd7M", "2tpNQA6n", "uQeLxX9P" )

    /**
     * Verifica della chiave inserita sulla base di un elenco di 100 chiavi
     */
    fun verificaLicenza(valoreInserito: String ):Boolean {
        if(listaChiavi.contains(valoreInserito)) { return true }
        return false
    }

    fun setDefaultLanguage():String {
        //imposta la lingua di default, che e' quella del tablet se non si e' ancora impostata (comunque se non trova le stringhe prende dall'inglese)
        val linguatablet = Locale.getDefault().getDisplayLanguage()
        //Toast.makeText(this, "la lingua impostata del tablet e' $linguatablet", Toast.LENGTH_LONG).show()
        when (linguatablet) {
            "english" -> return "en"
            "italiano" -> return "it"
            else -> return "en"
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.applyNewLocale(Locale("es", "MX")))
    }

    fun Context.applyNewLocale(locale: Locale): Context {
        val config = this.resources.configuration
        val sysLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.locales.get(0)
        } else {
            //Legacy
            config.locale
        }
        if (sysLocale.language != locale.language) {
            Locale.setDefault(locale)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                config.setLocale(locale)
            } else {
                //Legacy
                config.locale = locale
            }
            resources.updateConfiguration(config, resources.displayMetrics)
        }
        return this
    }

}

