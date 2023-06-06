package com.example.bttesting

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bttesting.databinding.HelpFragmentBinding
import com.example.bttesting.databinding.InfodialogHelpBinding

/* DIVERSI METODI ALTERNATIVI:
    1 - In una schermata fare un elenco di immagini interattive con un elemento scrolling
    2 - Serie di schermate con Immagine e descrizione in fondo alla pagina

    - Tutte le immagini devono avere un bordo e devono essere piu' piccole
    - Si puo' mettere una scritta in sovraimpressione (oppure in qualche modo di colore diverso)
    - FOTO CON DIDASCALIE IN FONDO ALLA PAGINA!!! delle scritte in fondo alla pagina

    Il messaggio di testo puo' comparire sia in fondo alla pagina oppure in prossimità dell'elemento (Si mettono delle stringhe di testo, nascoste che si attivano cliccando sull'elemento -
    e rimangonp accese fino a quando si clicca sullo sfondo o su un altro elemento)
    Si possono togliere cliccando su un altro elemento. Si vuole che rimangano diverse scritte in contemporanea oppure no?
    Secondo me l'idea migliore e' quella di tenere visibili le scritte fino a che si clicca di nuovo.

*/

/* DA FARE:

    1 - Da togliere string hardcode e mettere come risorse!!
    2 - TRADURRE IN INGLESE E METTERE LE VARIE SCRITTE IN INGLESE
    3 - METTERE COME RISORSE TUTTE LE STRINGHE   (anche di tutte le altre schermate!!!!!)
    4 - RICARICARE LE IMMAGINI TOGLIENDO IL SIMBOLINO DEL PALLONE!!!! in alto a sinistra
    5 - SI DEVE IMPLEMENTARE ANCHE IL PASSAGGIO DA UNA SCHERMATA ALLA ALTRA CON LA NAVIGAZIONE!!!!!!!! mettendo in sequenza!!
    7 - IMPOSTARE I PULSANTI SULLE IMMAGINI ALLA DIMENSIONE E POSIZIONE CORRETTA!!!!!!
    8 - SBARRA LATERALE!!!!! renderla piu' funzionale!!!!
    9 - NAVIGAZIONE AD UN CERTO PUNTO DELLA PAGINA E TORNARE INDIETRO AL MENU
    10 - Oppure menu a tendina!!!! nella parte che non scolla in alto a destra!!!  o un menu a destra se non viene troppo piccolo!!!!

    11 - RIUNIRE LE FOTO IN UNA CARTELLINA (in drawable)

    12 - CONTROLLARE TUTTE LE IMMAGINI VEDENDO DOVE VENGONO UTILIZZATE
        Rimuovere immagine della terra (earth.gif)

*/

/* CONCETTI:

    1 - ADJUSTVIEWBOUND: https://stackoverflow.com/questions/15142780/how-do-i-remove-extra-space-above-and-below-imageview

    2 - A VOLTE E' PIU' FACILE MODIFICARE LE IMPOSTAZIONI DA XML CHE DA DESIGN (soprattutto per copia e incolla)....oppure si carica un documento di stile
        per aggiustamenti sullo stile del testo!!!!

    3 - SITO MOLTO INTERESSANTE CHE PARLA DI GRAFICA ED ANDROID!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    https://google-developer-training.github.io/android-developer-fundamentals-course-concepts-v2/unit-2-user-experience/lesson-4-user-interaction/4-1-c-buttons-and-clickable-images/4-1-c-buttons-and-clickable-images.html
    https://www.tutlane.com/tutorial/android/android-imagebutton-with-examples
    https://www.tutorialkart.com/kotlin-android/how-to-dynamically-change-button-background-in-kotlin-android/#:~:text=setBackgroundResource()%20method%20is%20used,the%20background%20to%20the%20button.

    4 - DRAWABLE E NINE PATCH: https://developer.android.com/guide/topics/resources/drawable-resource#StateList
    https://developer.android.com/guide/topics/resources/drawable-resource
    ed altri concetti interessanti!!!

    5 - BUTTONS DOCUMENTAZIONE UFFICIALE!! https://developer.android.com/develop/ui/views/components/button
    CAMBIO DI SFONDO DINAMICO: https://stackoverflow.com/questions/12249495/android-imagebutton-change-image-onclick
    PADDING: tra il testo e l'icona del pulsante (dentro il pulsante)

    6 - SPINNERS: https://developer.android.com/develop/ui/views/components/spinner#kotlin

*/

/* DA DECIDERE:
    1 - Durata dei Toast (decidere se mettere come tempo corto oppure lungo)
    2 - Se mettere anche testo con grafico a scomparsa (e con un timer)...IN QUESTO CASO DOVREBBE PRENDERE UNA VIEW COME PARAMETRO, mostrare o nascondere la view, cancellare il thread (in certe situazioni))
        Problema con i pulsanti laterali (da prevedere due modelli diversi di nuvoletta, uno con gancio superiore nel mezzo, e uno con gancio laterale (oppure rotarli)
    3 - Si deve anche inserire lo stesso menu di Help oppure no?
    4 - Alcuni layout hanno anche doppia immagine cliccabile!!!!
    5 - Le icone devono avere al loro interno la parola Icon: selectionIconHomescreenHelp
    6 - BARRA LATERALE: si puo' mettere un pulsante unico GRANDE con l'indicazione generica (la barra laterale permette una navigazione veloce alle varie schermate!!!)
    7 - UNIFORMARE PADDING ELEMENTI (Layout!!!! etc..) e margine
    8 - DECIDERE COME SI PUO' STACCARE DA UNA SCHERMATA ALLA ALTRA (anche mettere uno sfondo diverso al titolo - nero etc..)
    9 - LE ICONE HANNO UN NOME (ribadito nel Toast) e nella DESCRIZIONE (che ha una parte introduttiva in cui si spiega a grandi linee il funzionamento)
*/

/* Commenti liberi:

    - Si deve provare anche a mettere in navigazione CREANDO UNA ALTRA ATTIVITA' con le istruzioni!!!!!!
        provare a creare una altra attività con la navigazione tra fragmenti!!!! e in questo modo si possono anche mettere dei campi di testo, o modificare i valori!!!!

    - SPINNER.SETSELECTION(position) (permette di selezionare una opzione dal menu!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!)
*/

/* CLICCANDO SUL MENU A TENDINA SI DOVREBBE POTER VEDERE SOLO LA SCHERMATA CORRISPONDENTE NASCONDENDO LE ALTRE SCHERMATE!!!!!!

    E lasciando la voce "Visualizza tutte le schermate"!!!!!!
    In modo tale che si eviti di fare confusione (al molteplicarsi dei contenuti)
    e comunque mettere degli elementi di separazione o padding
    SI POSSONO METTERE DUE MENU: uno che nasconde le altre view e l'altro che naviga al punto interessato
    SI LAVORA SOLO SULLE PRIME DUE SCHERMATE (cercando di ottimizzare la grafica e gli elementi separatori)
    SI PUO' ANCHE METTERE DELLE SCRITTE CLICCABILI AL POSTO DEL MENU A TENDINA!!!!!
    la barra piu' visibile forse non e' necessaria (in quanto si naviga facilmente e poi si puo' selezionare solo una schermata) (e' piu' un segnalatore)
    LA FUNZIONE CHE MOSTRA SOLO LA VIEW INTERESSATA DOVREBBE ESSERE MOLTO SEMPLICE E PRENDERE COME PARAMETRO (TIPIZZATO LINEARLAYOUT) SOLO LA VIEW DA MOSTRARE (prende da un array interno gli altri elementi)

    - SE SI CLICCA SU UN ELEMENTO DELLA DESCRIZIONE DOVREBBE MOSTRARLO ALL'INTERNO DELLA SCHERMATA (cambiando lo sfondo!!!) o MOSTRANDO UN BORDINO COLORATO PER EVIDENZIARE!!
    SI PUO' ANCHE METTERE UNA NUVOLETTA SPECIALE O UN BORDINO
*/

/* SPINNER CHE NASCONDE PROGRAMMATICAMENTE LE VIEWS

    - CI DOVREBBE ESSERE ANCHE un pulsante che VISUALIZZA LA SEQUENZA DELLE SCHERMATE!!!! o una qualche voce del menu a tendina!!! START E NEXT (e poi all) e modifica anche il contenuto di testo del pulsante

    - SI DEVE CUSTOMIZZARE LO SPINNER COME LAYOUT GENERALE E DEI SINGOLI ELEMENTI

    - Links su spinner:
        (DOCUMENTAZIONE UFFICIALE): https://developer.android.com/develop/ui/views/components/spinner
        (INTERESSANTE ONITEMSELECTED IN KOTLIN): https://www.solutionspacenet.com/post/create-a-spinner-in-android-using-kotlin
        https://www.html.it/pag/49010/spinner-menu-a-tendina/
        https://www.geeksforgeeks.org/spinner-in-android-using-java-with-example/
        https://www.javatpoint.com/android-spinner-example
        https://www.tutorialspoint.com/android/android_spinner_control.htm
*/

/* GRAFICA:
    - Si puo' anche togliere lo sfondo opaco sugli elementi selezionati
    - METTERE SFONDI PERSONALIZZATI (pulsanti dentro immagini) E PENSARE UNA SOLUZIONE!!! cambiare pulsanti in textView o altro
        potrei provare con delle immagini in PNG (con sfondo trasparente) per i pulsanti. DA POTER SOSTITUIRE FACILMENTE LA GRAFICA IN PNG DELL'ELEMENTO DI EVIDENZIAZIONE (valutare se si puo' cliccare su delle immagini)
        SOLUZIONE OTTIMALE DA VEDERE CON IL CLIENTE: single elementi semplici (come relativeLayout o constraint) con dentro button e immagine. Area di button piu' limitata e immagine nascosta da mostrare
                da vedere la dispendiosità in termini di risorse del tablet
    - VEDERE INDICAZIONE NEI COMMENTI DENTRO XML E XML DELLE STRINGHE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 */

/* LA DESCRIZIONE SOTTO ALLA SCHERMATA DOVREBBE AVERE INFORMAZIONI PIU' DETTAGLIATE ( rispetto al semplice Toast )!!!! E SI DOVREBBE AVVISARE CON UN MESSAGGIO GLI UTENTI DELLA COSA

 */

/* SI DOVREBBE POTER NAVIGARE AD ALTRE SCHERMATE (CON IL CLICK BREVE O PROLUNGATO) (PASSANDO COME PARAMETRI DELLE LAMBDA)
    in questo modo si simulaziona la navigazione
    (si possono anche semplificare i bindings iniziali)
 */

/* CUSTOMIZZAZIONE GRAFICA SPINNER

    Documentazione:
        https://stackoverflow.com/questions/16694786/how-to-customize-a-spinner-in-android


     1 - SI METTE DENTRO UN RELATIVE LAYOUT O CONSTRAINT LAYOUT  (da personalizzare con bordo - creato apposito, spinner e immagine custom) - Questo per la grafica del relative layout
     2 - SI CREA ANCHE UNA GRAFICA PER IL SINGOLO ELEMENTO

 */

/* DIALOG WITH CUSTOM LAYOUT
    Link: https://www.geeksforgeeks.org/how-to-create-a-custom-alertdialog-in-android/

 */

/* LINK INTERESSANTI :
    - TRIPLE: https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-triple/
    - OTTENERE IL VALORE DI UNA STRINGA DAL SUO ID:
            https://stackoverflow.com/questions/7493287/android-how-do-i-get-string-from-resources-using-its-name
            https://stackoverflow.com/questions/2183962/how-to-read-value-from-string-xml-in-android
            https://stackoverflow.com/questions/34528116/get-view-name-programmatically-in-android

    - MIGLIORI PRATICHE LEGATE ALLE STRINGHE:
            https://medium.com/@sourav.bh/best-practices-for-using-string-resources-in-android-c947fdd1606a

    - MIGLIORI PRATICHE SU CONVENZIONI NOMI PER LE STRINGHE:
            https://stackoverflow.com/questions/35923922/best-practices-for-id-naming-conventions-in-android

    - ALERT DIALOG (interessante) setDismissListener, setShowListener ed altri
            OPPURE CON LISTE MULTISCELTA
            https://www.digitalocean.com/community/tutorials/android-alert-dialog-using-kotlin

    - DATABINDING IN ALERT DIALOG :
            https://stackoverflow.com/questions/34967868/how-to-use-data-binding-in-dialog

    - DISMISS ALERT DIALOG CON PULSANTI INTERNI OD ALTRO (basta metterlo successivamente alla invocazione .show()
            https://stackoverflow.com/questions/30253886/how-to-dismiss-alertdialog-builder-with-custom-button

    - CAMBIARE LO SFONDO DI ALERT DIALOG:
            https://stackoverflow.com/questions/57687214/cannot-change-background-color-of-alertdialog-button
            https://www.geeksforgeeks.org/how-to-change-the-background-color-after-clicking-the-button-in-android/

    - ALERT DIALOG e ottenere il riferimento al pulsante positivo o negativo per cambiare stile
            https://stackoverflow.com/questions/4604025/alertdialog-getbutton-method-gives-null-pointer-exception-android
            https://stackoverflow.com/questions/31762905/android-alert-dialog-button-background

    - CAMBIARE TEMA DI STILE PER ALERT DIALOG
            https://stackoverflow.com/questions/2422562/how-to-change-theme-for-alertdialog
            https://stackoverflow.com/questions/5814998/set-a-background-to-buttons-in-a-dialog

    - ALERT DIALOG E CUSTOM LAYOUT:
            https://www.geeksforgeeks.org/how-to-create-dialog-with-custom-layout-in-android/
            https://android.pcsalt.com/create-alertdialog-with-custom-layout-using-xml-layout/

    - SPINNER CUSTOMIZATION:
            https://stackoverflow.com/questions/16694786/how-to-customize-a-spinner-in-android
            https://abhiandroid.com/ui/custom-spinner-examples.html
            https://developer.android.com/develop/ui/views/components/spinner   (documentazione ufficiale)

    - DATABINDING E LAYOUT EXPRESSIONS!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            https://medium.com/@lucasnrb/dos-and-don-ts-with-android-data-binding-45916e0b415e

    - QUADRUPLE, QUINTUPLE (Destrutturando)
    https://stackoverflow.com/questions/46202147/kotlin-quadruple-quintuple-etc-for-destructuring



*/

/*  MULTIMEDIA DIALOG:
    SI POSSONO CARICARE ANCHE GIFS CON UN ELEMENTO DI LAYOUT DIVERSO CHE NON CARICA IMMAGINI NORMALI
    SE NON SI USA SI DOVREBBE TOGLIERE IMPLEMENTAZIONE IN BUILD!!!!!!!
    vedere meglio se si possono caricare immagini normali!!!! da documentazioni ufficiali!!!!!
    Mettere condizioni dentro la funzione in modo da nascondere (concatenando verticalmente e nascondendo)
    di modo tale che si puo' mettere una tripla (immagini, gifs, video) con possibilità di metterli tutti e tre!!! vediamo come

    Si puo' anche mettere uno sfondo scrollabile se l'elemento messo sopra e' "trasparent"
    L'EFFETTO E' QUELLO DI TENERE UN ELEMENTO IN PRIMO PIANO (immagine Gif o Video) e avere un testo dietro scrollabile
    PER POTER LEGGERE INFORMAZIONI RIGUARDO ALL'ELEMENTO IN PRIMO PIANO!!!!!!!
    occorre migliorare le impostazioni grafiche!!!!

    METTERE IMMAGINE DI CARICAMENTO!!!!! prima carica una immagine prima del video di youtube

    https://stackoverflow.com/questions/6533942/adding-gif-image-in-an-imageview-in-android

    per i video
    https://johncodeos.com/how-to-embed-youtube-videos-into-your-android-app-using-kotlin/

    Cambiare sorgente della immagine:
    https://stackoverflow.com/questions/2974862/changing-imageview-source

 */

/* ANCHE DENTRO ALERT DIALOG!!!!!! SI PUO' METTERE UNO SFONDO SCROLLABILE (LO SFONDO E' SCROLLABILE PERCHE' QUELLO SOPRA E' TRASPARENTE!!!!!!)
    CON I PULSANTI TRASPARENTI CON BORDO DORATO!!!!! giallo per evidenziare
    Si puo' anche mettere uno sfondo scrollabile se l'elemento messo sopra e' "trasparent"
    L'EFFETTO E' QUELLO DI TENERE UN ELEMENTO IN PRIMO PIANO (immagine Gif o Video) e avere un testo dietro scrollabile
    PER POTER LEGGERE INFORMAZIONI RIGUARDO ALL'ELEMENTO IN PRIMO PIANO!!!!!!!
    occorre migliorare le impostazioni grafiche!!!!

*/

// METTERE ENUM INVECE DEI NUMERI DOVE CI VUOLE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
// ALTRIMENTI SI LEGGE PEGGIO!!!!!

class HelpFragment : Fragment() {

    lateinit var listaScreens : Array<LinearLayout>
    lateinit var screensSpinner : Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<HelpFragmentBinding>(inflater, R.layout.help_fragment, container, false)


        //val packageName: String = requireActivity().packageName
        //val resId = requireActivity().resources.getIdentifier("settingsIconHomescreenHelp", "string", packageName)
        //val stringa : String = requireActivity().getString(resId)
        //val stringaId : String = getStringFromId(requireActivity(), "settings_homescreen_help")
        //Toast.makeText(requireActivity(), "STRINGAID: ${stringaId}  ", Toast.LENGTH_SHORT).show()

        //PARTE DICHIARATIVA ED ATTRIBUTIVA!!!!!
        //bindings di tutti i layout
        val homescreenLayoutView: LinearLayout = binding.homescreenLayoutHelp
        val selectionscreenLayoutView: LinearLayout = binding.selectionscreenLayoutHelp
        val livescreenLayoutView: LinearLayout = binding.livescreenLayoutHelp
        val chartsscreenLayoutView: LinearLayout = binding.chartscreenLayoutHelp
        val historicalscreenLayoutView: LinearLayout = binding.historicalscreenLayoutHelp
        val historicalActiveLayoutView: LinearLayout = binding.historicalActiveLayoutHelp
        val settingsscreenLayoutView: LinearLayout = binding.settingsscreenLayoutHelp
        val couplingscreenLayoutView: LinearLayout = binding.couplingscreenLayoutHelp
        listaScreens = arrayOf(
            homescreenLayoutView,
            selectionscreenLayoutView,
            livescreenLayoutView,
            chartsscreenLayoutView,
            historicalscreenLayoutView,
            historicalActiveLayoutView,
            settingsscreenLayoutView,
            couplingscreenLayoutView)

        //BINDINGS DI HOMESCREEN e LISTA BUTTONS E TEXTS
        val settingsButtonHomescreen: Button = binding.settingsHomeHelp
        val historicalButtonHomescreen: Button = binding.historicalHomeHelp
        val selectionButtonHomescreen: Button = binding.selectionHomeHelp
        val helpButtonHomescreen: Button = binding.helpHomeHelp
        val settingsTextHomescreen: TextView = binding.settingsHomeHelpText
        val historicalTextHomescreen: TextView = binding.historicalHomeHelpText
        val selectioneTextHomescreen: TextView = binding.selectionHomeHelpText
        val helpTextHomescreen: TextView = binding.helpHomeHelpText
        val listaButtonHomescreen: Array<Button> = arrayOf(settingsButtonHomescreen, historicalButtonHomescreen, selectionButtonHomescreen, helpButtonHomescreen)
        val listaTextHomescreen: Array<TextView> = arrayOf(settingsTextHomescreen, historicalTextHomescreen, selectioneTextHomescreen, helpTextHomescreen)
        //il terzo parametro si riferisce alla schermata dentro lista screens da mettere in enum!!!!!!!!!!!!!!!!!!!!!!!!
        val listaHome: Array<Triple<Button, TextView, Int?>> = arrayOf(
            Triple(settingsButtonHomescreen, settingsTextHomescreen, 6 ),
            Triple(historicalButtonHomescreen, historicalTextHomescreen, 4 ),
            Triple(selectionButtonHomescreen, selectioneTextHomescreen, 1 ),
            Triple(helpButtonHomescreen, helpTextHomescreen, null )
            )

        //BINDINGS DI SELECTIONSCREEN e LISTA BUTTONS E TEXTS
        val sidebarButtonSelectionscreen: Button = binding.sidebarSelectionHelp
        val btButtonSelectionscreen: Button = binding.btSelectionHelp
        val searchButtonSelectionscreen: Button = binding.searchSelectionHelp
        val backButtonSelectionscreen: Button = binding.backSelectionHelp
        val unitButtonSelectionscreen : Button = binding.unitSelectionHelp

        val sidebarTextSelectionscreen : TextView = binding.sidebarSelectionHelpText
        val btTextSelectionscreen : TextView = binding.btSelectionHelpText
        val searchTextSelectionscreen : TextView = binding.searchSelectionHelpText
        val backTextSelectionscreen : TextView = binding.backSelectionHelpText
        val unitTextSelectionscreen : TextView = binding.unitSelectionHelpText

        val listaButtonSelectionscreen : Array<Button> = arrayOf(sidebarButtonSelectionscreen, btButtonSelectionscreen, searchButtonSelectionscreen, backButtonSelectionscreen, unitButtonSelectionscreen)
        val listaTextSelectionscreen : Array<TextView> = arrayOf(sidebarTextSelectionscreen, btTextSelectionscreen, searchTextSelectionscreen, backTextSelectionscreen, unitTextSelectionscreen)
        val listaSelection: Array<Triple<Button, TextView, Int?>> = arrayOf(
            Triple(sidebarButtonSelectionscreen, sidebarTextSelectionscreen, null ),
            Triple(btButtonSelectionscreen, btTextSelectionscreen, null ),
            Triple(searchButtonSelectionscreen, searchTextSelectionscreen, null ),
            Triple(backButtonSelectionscreen, backTextSelectionscreen, 0 ),
            Triple(unitButtonSelectionscreen, unitTextSelectionscreen, 2 )
        )


        //BINDINGS DI LIVESCREEN E LISTA BUTTONS E TEXTS
        val sidebarButtonLivescreen: Button = binding.sidebarLiveHelp
        val backButtonLivescreen: Button = binding.backLiveHelp
        val patientButtonLivescreen: Button = binding.patientLiveHelp
        val timeButtonLivescreen: Button = binding.timeLiveHelp
        val btButtonLivescreen : Button = binding.btLiveHelp
        val liquidButtonLivescreen: Button = binding.liquidLiveHelp
        val airButtonLivescreen: Button = binding.airLiveHelp
        val pressureButtonLivescreen: Button = binding.pressureLiveHelp
        val chartsButtonLivescreen: Button = binding.chartsLiveHelp
        val suctionButtonLivescreen : Button = binding.suctionLiveHelp
        //TEXTLIVEHELP
        val sidebarTextLivescreen : TextView = binding.sidebarLiveHelpText
        val backTextLivescreen : TextView = binding.backLiveHelpText
        val patientTextLivescreen : TextView = binding.patientLiveHelpText
        val timeTextLivescreen : TextView = binding.timeLiveHelpText
        val btTextLivescreen : TextView = binding.btLiveHelpText
        val liquidTextLivescreen : TextView = binding.liquidLiveHelpText
        val airTextLivescreen : TextView = binding.airLiveHelpText
        val pressureTextLivescreen : TextView = binding.pressureLiveHelpText
        val chartsTextLivescreen : TextView = binding.chartsLiveHelpText
        val suctionTextLivescreen : TextView = binding.suctionLiveHelpText
        // liste
        val listaButtonLivescreen : Array<Button> = arrayOf(sidebarButtonLivescreen, backButtonLivescreen, patientButtonLivescreen, timeButtonLivescreen, btButtonLivescreen,
            liquidButtonLivescreen, airButtonLivescreen, pressureButtonLivescreen, chartsButtonLivescreen, suctionButtonLivescreen)
        val listaTextLivescreen : Array<TextView> = arrayOf(sidebarTextLivescreen, backTextLivescreen, patientTextLivescreen, timeTextLivescreen, btTextLivescreen,
            liquidTextLivescreen, airTextLivescreen, pressureTextLivescreen, chartsTextLivescreen, suctionTextLivescreen)
        val listaLive: Array<Triple<Button, TextView, Int?>> = arrayOf(
            Triple(sidebarButtonLivescreen, sidebarTextLivescreen, null ),
            Triple(backButtonLivescreen, backTextLivescreen, 1 ),
            Triple(patientButtonLivescreen, patientTextLivescreen, null ),
            Triple(timeButtonLivescreen, timeTextLivescreen, null ),
            Triple(btButtonLivescreen, btTextLivescreen, null ),
            Triple(liquidButtonLivescreen, liquidTextLivescreen, null ),
            Triple(airButtonLivescreen, airTextLivescreen, null ),
            Triple(pressureButtonLivescreen, pressureTextLivescreen, null ),
            Triple(chartsButtonLivescreen, chartsTextLivescreen, 3 ),
            Triple(suctionButtonLivescreen, suctionTextLivescreen, null )
        )

        //BINDINGS DI CHARTS SCREEN, LISTE E TEXTS
        val backButtonChartsscreen: Button = binding.backChartsHelp
        val patientButtonChartsscreen: Button = binding.patientChartsHelp
        val pressureButtonChartsscreen: Button = binding.pressureChartsHelp
        val airButtonChartsscreen: Button = binding.airChartsHelp
        val liquidButtonChartsscreen: Button = binding.liquidChartsHelp
        val btButtonChartsscreen: Button = binding.btChartsHelp
        val drawButtonChartsscreen: Button = binding.drawChartsHelp
        //texts
        val backTextChartsscreen: TextView = binding.backChartsHelpText
        val patientTextChartsscren: TextView = binding.patientChartsHelpText
        val pressureTextChartsscreen: TextView = binding.pressureChartsHelpText
        val airTextChartsscreen: TextView = binding.airChartsHelpText
        val liquidTextChartsscreen: TextView = binding.liquidChartsHelpText
        val btTextChartsscreen: TextView = binding.btChartsHelpText
        val drawTextChartsscreen: TextView = binding.drawChartsHelpText
        //liste
        val listaButtonChartsscreen : Array<Button> = arrayOf(backButtonChartsscreen, patientButtonChartsscreen, pressureButtonChartsscreen, airButtonChartsscreen, liquidButtonChartsscreen, btButtonChartsscreen, drawButtonChartsscreen)
        val listaTextChartsscreen : Array<TextView> = arrayOf(backTextChartsscreen, patientTextChartsscren, pressureTextChartsscreen, airTextChartsscreen, liquidTextChartsscreen, btTextChartsscreen, drawTextChartsscreen)
        val listaCharts: Array<Triple<Button, TextView, Int?>> = arrayOf(
            Triple(backButtonChartsscreen, backTextChartsscreen, 2 ),
            Triple(patientButtonChartsscreen, patientTextChartsscren, null ),
            Triple(pressureButtonChartsscreen, pressureTextChartsscreen, null ),
            Triple(airButtonChartsscreen, airTextChartsscreen, null ),
            Triple(liquidButtonChartsscreen, liquidTextChartsscreen, null ),
            Triple(btButtonChartsscreen, btTextChartsscreen, null ),
            Triple(drawButtonChartsscreen, drawTextChartsscreen, null )
        )

        //BINDINGS DI HISTORICAL SCREEN, TEXTS, LISTE
        val backButtonHistoricalscreen : Button = binding.backHistoricalHelp
        val visualizationButtonHistoricalscreen: Button = binding.visualizationHistoricalHelp
        val unitButtonHistoricalScreen: Button = binding.unitsHistoricalHelp
        val sidebarButtonHistoricalScreen: Button = binding.sidebarHistoricalHelp

        val backTextHistoricalscreen : TextView = binding.backHistoricalHelpText
        val visualizationTextHistoricalscreen : TextView = binding.visualizationHistoricalHelpText
        val unitTextHistoricalscreen : TextView = binding.unitsHistoricalHelpText
        val sidebarTextHistoricalscreen :TextView = binding.sidebarHistoricalHelpText

        val listaButtonHistoricalscren : Array<Button>  = arrayOf(backButtonHistoricalscreen, visualizationButtonHistoricalscreen, unitButtonHistoricalScreen, sidebarButtonHistoricalScreen)
        val listaTextHistoricalscreen : Array<TextView> = arrayOf(backTextHistoricalscreen, visualizationTextHistoricalscreen, unitTextHistoricalscreen, sidebarTextHistoricalscreen)
        val listaHistorical : Array<Triple<Button, TextView, Int?>> = arrayOf(
            Triple(backButtonHistoricalscreen, backTextHistoricalscreen, 0 ),
            Triple(visualizationButtonHistoricalscreen, visualizationTextHistoricalscreen, null ),
            Triple(unitButtonHistoricalScreen, unitTextHistoricalscreen, 3 ),
            Triple(sidebarButtonHistoricalScreen, sidebarTextHistoricalscreen, null )
        )

        //BINDINGS DI HISTORICAL ACTIVE SCREEN, TEXTS, LISTE
        val backButtonHistoricalActive : Button = binding.backHistoricalActiveHelp
        val visualizationButtonHistoricalActive: Button = binding.visualizationHistoricalActiveHelp
        val unitButtonHistoricalActive: Button = binding.unitHistoricalActiveHelp
        val sidebarButtonHistoricalActive: Button = binding.sidebarHistoricalActiveHelp
        val renameButtonHistoricalActive : Button = binding.renameHistoricalActiveHelp
        val deleteButtonHistoricalActive : Button = binding.deleteHistoricalActiveHelp
        val sendButtonHistoricalActive : Button = binding.sendHistoricalActiveHelp

        val backTextHistoricalActive : TextView = binding.backTextHistoricalActiveHelp
        val visualizationTextHistoricalActive : TextView = binding.visualizationTextHistoricalActiveHelp
        val unitTextHistoricalActive : TextView = binding.unitTextHistoricalActiveHelp
        val sidebarTextHistoricalActive : TextView = binding.sidebarTextHistoricalActiveHelp
        val renameTextHistoricalActive : TextView = binding.renameTextHistoricalActiveHelp
        val deleteTextHistoricalActive : TextView = binding.deleteTextHistoricalActiveHelp
        val sendTextHistoricalActive : TextView = binding.sendTextHistoricalActiveHelp

        val listaButtonHistoricalActive : Array<Button>  = arrayOf(backButtonHistoricalActive, visualizationButtonHistoricalActive, unitButtonHistoricalActive, sidebarButtonHistoricalActive, renameButtonHistoricalActive, deleteButtonHistoricalActive, sendButtonHistoricalActive)
        val listaTextHistoricalActive : Array<TextView> = arrayOf(backTextHistoricalActive, visualizationTextHistoricalActive, unitTextHistoricalActive, sidebarTextHistoricalActive, renameTextHistoricalActive, deleteTextHistoricalActive, sendTextHistoricalActive)

        //BINDINGS DI SETTINGS BUTTON, TEXT, LISTS
        val backButtonSettings : Button = binding.backSettingsHelp
        val couplingButtonSettings : Button = binding.couplingSettingsHelp
        val sidebarButtonSettings : Button = binding.sidebarSettingsHelp

        val backTextSettings : TextView = binding.backSettingsHelpText
        val couplingTextSettings : TextView = binding.couplingSettingsHelpText
        val sidebarTextSettings : TextView = binding.sidebarSettingsHelpText

        val listaButtonSettings: Array<Button> = arrayOf(backButtonSettings, couplingButtonSettings, sidebarButtonSettings)
        val listaTextSettings : Array<TextView> = arrayOf(backTextSettings, couplingTextSettings, sidebarTextSettings)
        val listaSettings : Array<Triple<Button, TextView, Int?>> = arrayOf(
            Triple(backButtonSettings, backTextSettings, 0 ),
            Triple(couplingButtonSettings, couplingTextSettings, 7 ),
            Triple(sidebarButtonSettings, sidebarTextSettings, null )
        )

        //BINDINGS DI COUPLING BUTTONS, TEXTS, LISTS
        val backButtonCoupling : Button = binding.backCouplingHelp
        val searchButtonCoupling : Button = binding.searchCouplingHelp
        val btButtonCoupling : Button = binding.btCouplingHelp
        val pairedButtonCoupling : Button = binding.pairedCouplingHelp
        val unpairedButtonCoupling : Button = binding.unpairedCouplingHelp
        val sidebarButtonCoupling : Button = binding.sidebarCouplingHelp

        val backTextCoupling : TextView = binding.backCouplingHelpText
        val searchTextCoupling : TextView = binding.searchCouplingHelpText
        val btTextCoupling : TextView = binding.btCouplingHelpText
        val pairedTextCoupling : TextView = binding.pairedCouplingHelpText
        val unpairedTextCoupling : TextView = binding.unpairedCouplingHelpText
        val sidebarTextCoupling : TextView = binding.sidebarCouplingHelpText

        val listaButtonCoupling : Array<Button> = arrayOf(backButtonCoupling, searchButtonCoupling, btButtonCoupling, pairedButtonCoupling, unpairedButtonCoupling, sidebarButtonCoupling)
        val listaTextCoupling : Array<TextView> = arrayOf(backTextCoupling, searchTextCoupling, btTextCoupling, pairedTextCoupling, unpairedTextCoupling, sidebarTextCoupling)
        val listaCoupling: Array<Triple<Button, TextView, Int?>> = arrayOf(
            Triple(backButtonCoupling, backTextCoupling, 5 ),
            Triple(searchButtonCoupling, searchTextCoupling, null ),
            Triple(btButtonCoupling, btTextCoupling, null ),
            Triple(pairedButtonCoupling, pairedTextCoupling, null ),
            Triple(unpairedButtonCoupling, unpairedTextCoupling, null ),
            Triple(sidebarButtonCoupling, sidebarTextCoupling, null )
        )

        //per lo spinner - DA CUSTOMIZZARE COME LAYOUT SPINNER E ELEMENTI!!!!! e si puo' anche cambiare dimensioni e sfondo!!!!!
        screensSpinner = binding.screensSpinner
        // ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.screens_array,
            R.layout.help_spinner_text
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(R.layout.help_spinner_dropdown)
            // Apply the adapter to the spinner
            screensSpinner.adapter = adapter
        }
        //Listener Spinner
        screensSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                pos: Int,
                id: Long
            ) {
                var stringaScelta = parent?.getItemAtPosition(pos)
                Toast.makeText(requireActivity(), "Hai selezionato ${stringaScelta}", Toast.LENGTH_SHORT).show()

                when (stringaScelta) {
                    "All screens" -> showSelectedScreen(listaScreens, homescreenLayoutView, true ) //il secondo argomento e' messo solo per convenzione
                    "Home screen" -> showSelectedScreen(listaScreens,homescreenLayoutView, false)
                    "Selection screen" -> showSelectedScreen(listaScreens, selectionscreenLayoutView, false )
                    "Livedata screen" -> showSelectedScreen(listaScreens, livescreenLayoutView, false )
                    "Charts screen" -> showSelectedScreen(listaScreens, chartsscreenLayoutView, false )
                    "Historical screen" -> showSelectedScreen(listaScreens, historicalscreenLayoutView, false)
                    "Settings screen" -> showSelectedScreen(listaScreens, settingsscreenLayoutView, false)
                    "Coupling screen" -> showSelectedScreen(listaScreens, couplingscreenLayoutView, false)
                    else -> return
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //no activity or action when nothing is selected
            }
        }

        //questi sono i listeners del menu laterale
        binding.patientLogoHelp.setOnClickListener {
            this.findNavController().navigate(R.id.connectFragment)
        }
        binding.historicalLogoHelp.setOnClickListener {
            this.findNavController().navigate(R.id.historicalFragment)
        }
        binding.settingsLogoHelp.setOnClickListener {
            this.findNavController().navigate(R.id.settingsFragment)
        }
        binding.helpLogoHelp.setOnClickListener {
            //this.findNavController().navigate(R.id.helpFragment)
        }

        //----------------------------------------------- LISTENERS --------------------------------------------------------------
        /*
            Ogni elemento generalmente ha una parte di testo da centralizzare in risorse, e possibile navigazione
            hightlight sempre presente!!!!!!!!!!!!!!!!!!!
            infodialog sempre presente!!!
            possibile...
            non si possono creare coppie separatamente, ma dentro array di coppie e' possibile
            Array<PAIR<Button, Layout>> arrayOf(Pair(id, screenslist[0]))
            SI DEVE METTERE ENUM PER AUTOCOMPILAZIONE!!!!
        */
        //Listeners schermata Homescreen
        //si potrebbe in qualche modo semplificare ulteriormente l'accesso alle stringhe del contenuto e titolo


        /*
        settingsButtonHomescreen.setOnClickListener {
            //rintraccia resId da view e verifica
            /*val name = if (it.id != View.NO_ID) resources.getResourceEntryName(
                it.id
            ) else ""
            //verifica per accedere a risorsa stringa da resId
            val stringaId : String = getStringFromView(requireActivity(), it)
            val titolo : String = getStringFromView(requireActivity(), name+"_titolo")*/
            Toast.makeText(requireActivity(), "STRINGAID: ${getStringFromView(requireActivity(), it)} ", Toast.LENGTH_SHORT).show()

            //Toast.makeText(requireActivity(), "ICONA IMPOSTAZIONI: Cliccando su questa icona si passa alla schermata delle impostazioni", Toast.LENGTH_SHORT).show()
            highlightElements(listaButtonHomescreen, listaTextHomescreen,  it as Button, settingsTextHomescreen)
            //screensSpinner.setSelection(6)
            infoDialog(requireActivity(), it,  destination = listaScreens[5])
        }
        helpButtonHomescreen.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONA HELP: Cliccando su questa icona si passa alla schermata delle istruzioni", Toast.LENGTH_SHORT).show()
            highlightElements(listaButtonHomescreen, listaTextHomescreen, it as Button, helpTextHomescreen)
            infoDialog(requireActivity(), button = it)
        }
        //si naviga alla sezione corrispondente
        selectionButtonHomescreen.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONA SELEZIONE UNITA': Cliccando su questa icona si passa alla schermata della selezione unita'", Toast.LENGTH_SHORT).show()
            highlightElements(listaButtonHomescreen, listaTextHomescreen, it as Button, selectioneTextHomescreen)
            //proviamo con la navigazione
            //screensSpinner.setSelection(2)
            //infoDialog(requireActivity(), "schermata selection", "questa e' la schermata selection", it,  listaScreens[1])
        }
        historicalButtonHomescreen.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONA DATI STORICI: Cliccando su questa icona si passa alla schermata dei dati storici dei pazienti", Toast.LENGTH_SHORT).show()
            highlightElements(listaButtonHomescreen, listaTextHomescreen, it as Button, historicalTextHomescreen)
            infoDialog(requireActivity(), button = it, destination = listaScreens[4])
        }

        //....e homescreen textview
        settingsTextHomescreen.setOnClickListener {
            highlightElements(listaButtonHomescreen, listaTextHomescreen,  settingsButtonHomescreen, it as TextView)
        }
        helpTextHomescreen.setOnClickListener {
            highlightElements(listaButtonHomescreen, listaTextHomescreen, helpButtonHomescreen, it as TextView)
        }
        selectioneTextHomescreen.setOnClickListener {
            highlightElements(listaButtonHomescreen, listaTextHomescreen, selectionButtonHomescreen, it as TextView)

        }
        historicalTextHomescreen.setOnClickListener {
            highlightElements(listaButtonHomescreen, listaTextHomescreen, historicalButtonHomescreen, it as TextView)
        }
        */

        //prova a creare listeners programmaticamente
        /*for(i in listaHome) {
            i.first.setOnClickListener {
                val message: String = getStringFromView(requireActivity(), it, 3)
                Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
                //da cambiare
                highlightElements(listaButtonHomescreen, listaTextHomescreen,  it as Button, settingsTextHomescreen)
                //screensSpinner.setSelection(6)
                infoDialog(requireActivity(), it,  destination = listaScreens[5])
            }
        }*/
        createListeners(requireActivity(), listaHome)

        /*
        //listeners schermata selection
        sidebarButtonSelectionscreen.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONE SIDEBAR: Cliccare su una di queste icone permette la navigazione veloce alla schermata corrispondente", Toast.LENGTH_SHORT).show()
            highlightElements(listaButtonSelectionscreen, listaTextSelectionscreen,  it as Button, sidebarTextSelectionscreen)
        }
        btButtonSelectionscreen.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONA BLUETOOTH: Il colore di questa icona permette di sapere se siamo collegati o meno: se di colore verde significa che siamo collegati. Tenendo premuto ci si disconnette", Toast.LENGTH_SHORT).show()
            highlightElements(listaButtonSelectionscreen, listaTextSelectionscreen,  it as Button, btTextSelectionscreen)
        }
        searchButtonSelectionscreen.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONA SEARCH: questa icona permette di sapere se il dispositivo e' ancora in fase di ricerca oppure l'ha completata. Cliccando lancia nuovamente la ricerca", Toast.LENGTH_SHORT).show()
            highlightElements(listaButtonSelectionscreen, listaTextSelectionscreen,  it as Button, searchTextSelectionscreen)
        }
        backButtonSelectionscreen.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONA BACK: cliccando su questa icona si ritorna alla homescreen", Toast.LENGTH_SHORT).show()
            highlightElements(listaButtonSelectionscreen, listaTextSelectionscreen,  it as Button, backTextSelectionscreen)
            screensSpinner.setSelection(1)
        }
        unitButtonSelectionscreen.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONE SINGOLE UNITA: queste sono le icone che sono disponibili per la connessione. Cliccando su una di esse si apre la connessione e si naviga in Livedata", Toast.LENGTH_SHORT).show()
            highlightElements(listaButtonSelectionscreen, listaTextSelectionscreen,  it as Button, unitTextSelectionscreen)
        }

        //....e selection textview
        sidebarTextSelectionscreen.setOnClickListener {
            highlightElements(listaButtonSelectionscreen, listaTextSelectionscreen,  sidebarButtonSelectionscreen, it as TextView)
        }
        btTextSelectionscreen.setOnClickListener {
            highlightElements(listaButtonSelectionscreen, listaTextSelectionscreen,  btButtonSelectionscreen, it as TextView)
        }
        searchTextSelectionscreen.setOnClickListener {
            highlightElements(listaButtonSelectionscreen, listaTextSelectionscreen,  searchButtonSelectionscreen, it as TextView)
        }
        backTextSelectionscreen.setOnClickListener {
            highlightElements(listaButtonSelectionscreen, listaTextSelectionscreen,  backButtonSelectionscreen, it as TextView)
        }
        unitTextSelectionscreen.setOnClickListener {
            highlightElements(listaButtonSelectionscreen, listaTextSelectionscreen,  unitButtonSelectionscreen, it as TextView)
        }
        */
        createListeners(requireActivity(), listaSelection)

        //listeners schermata live
        createListeners(requireActivity(), listaLive)

        /*sidebarButtonLivescreen.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONE SIDEBAR: Cliccare su una di queste icone permette la navigazione veloce alla schermata corrispondente", Toast.LENGTH_SHORT).show()
            highlightElements(listaButtonLivescreen, listaTextLivescreen,  it as Button, sidebarTextLivescreen)
        }
        backButtonLivescreen.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONE SIDEBAR: Cliccare su una di queste icone permette la navigazione veloce alla schermata corrispondente", Toast.LENGTH_SHORT).show()
            highlightElements(listaButtonLivescreen, listaTextLivescreen,  it as Button, backTextLivescreen)
        }
        patientButtonLivescreen.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONE SIDEBAR: Cliccare su una di queste icone permette la navigazione veloce alla schermata corrispondente", Toast.LENGTH_SHORT).show()
            highlightElements(listaButtonLivescreen, listaTextLivescreen,  it as Button, patientTextLivescreen)
        }
        timeButtonLivescreen.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONE SIDEBAR: Cliccare su una di queste icone permette la navigazione veloce alla schermata corrispondente", Toast.LENGTH_SHORT).show()
            highlightElements(listaButtonLivescreen, listaTextLivescreen,  it as Button, timeTextLivescreen)
        }
        btButtonLivescreen.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONE SIDEBAR: Cliccare su una di queste icone permette la navigazione veloce alla schermata corrispondente", Toast.LENGTH_SHORT).show()
            highlightElements(listaButtonLivescreen, listaTextLivescreen,  it as Button, btTextLivescreen)
        }
        liquidButtonLivescreen.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONE SIDEBAR: Cliccare su una di queste icone permette la navigazione veloce alla schermata corrispondente", Toast.LENGTH_SHORT).show()
            highlightElements(listaButtonLivescreen, listaTextLivescreen,  it as Button, liquidTextLivescreen)
        }
        airButtonLivescreen.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONE SIDEBAR: Cliccare su una di queste icone permette la navigazione veloce alla schermata corrispondente", Toast.LENGTH_SHORT).show()
            highlightElements(listaButtonLivescreen, listaTextLivescreen,  it as Button, airTextLivescreen)
        }
        pressureButtonLivescreen.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONE SIDEBAR: Cliccare su una di queste icone permette la navigazione veloce alla schermata corrispondente", Toast.LENGTH_SHORT).show()
            highlightElements(listaButtonLivescreen, listaTextLivescreen,  it as Button, pressureTextLivescreen)
        }
        chartsButtonLivescreen.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONE SIDEBAR: Cliccare su una di queste icone permette la navigazione veloce alla schermata corrispondente", Toast.LENGTH_SHORT).show()
            highlightElements(listaButtonLivescreen, listaTextLivescreen,  it as Button, chartsTextLivescreen)
        }
        suctionButtonLivescreen.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONE SIDEBAR: Cliccare su una di queste icone permette la navigazione veloce alla schermata corrispondente", Toast.LENGTH_SHORT).show()
            highlightElements(listaButtonLivescreen, listaTextLivescreen,  it as Button, suctionTextLivescreen)
        }
        //....e selection textview
        sidebarTextLivescreen.setOnClickListener {
            highlightElements(listaButtonLivescreen, listaTextLivescreen,  sidebarButtonLivescreen, it as TextView)
        }
        backTextLivescreen.setOnClickListener {
            highlightElements(listaButtonLivescreen, listaTextLivescreen,  backButtonLivescreen, it as TextView)
        }
        patientTextLivescreen.setOnClickListener {
            highlightElements(listaButtonLivescreen, listaTextLivescreen,  patientButtonLivescreen, it as TextView)
        }
        timeTextLivescreen.setOnClickListener {
            highlightElements(listaButtonLivescreen, listaTextLivescreen,  timeButtonLivescreen, it as TextView)
        }
        btTextLivescreen.setOnClickListener {
            highlightElements(listaButtonLivescreen, listaTextLivescreen,  btButtonLivescreen, it as TextView)
        }
        liquidTextLivescreen.setOnClickListener {
            highlightElements(listaButtonLivescreen, listaTextLivescreen,  liquidButtonLivescreen, it as TextView)
        }
        airTextLivescreen.setOnClickListener {
            highlightElements(listaButtonLivescreen, listaTextLivescreen,  airButtonLivescreen, it as TextView)
        }
        pressureTextLivescreen.setOnClickListener {
            highlightElements(listaButtonLivescreen, listaTextLivescreen,  pressureButtonLivescreen, it as TextView)
        }
        chartsTextLivescreen.setOnClickListener {
            highlightElements(listaButtonLivescreen, listaTextLivescreen,  chartsButtonLivescreen, it as TextView)
        }
        suctionTextLivescreen.setOnClickListener {
            highlightElements(listaButtonLivescreen, listaTextLivescreen,  suctionButtonLivescreen, it as TextView)
        }*/

        //listeners ChARTS SCREEN
        /*
        backButtonChartsscreen.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONE SIDEBAR: Cliccare su una di queste icone permette la navigazione veloce alla schermata corrispondente", Toast.LENGTH_SHORT).show()
            highlightElements(listaButtonChartsscreen, listaTextChartsscreen,  it as Button, backTextChartsscreen)
        }
        patientButtonChartsscreen.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONE SIDEBAR: Cliccare su una di queste icone permette la navigazione veloce alla schermata corrispondente", Toast.LENGTH_SHORT).show()
            highlightElements(listaButtonChartsscreen, listaTextChartsscreen,  it as Button, patientTextChartsscren)
        }
        pressureButtonChartsscreen.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONE SIDEBAR: Cliccare su una di queste icone permette la navigazione veloce alla schermata corrispondente", Toast.LENGTH_SHORT).show()
            highlightElements(listaButtonChartsscreen, listaTextChartsscreen,  it as Button, pressureTextChartsscreen)
        }
        airButtonChartsscreen.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONE SIDEBAR: Cliccare su una di queste icone permette la navigazione veloce alla schermata corrispondente", Toast.LENGTH_SHORT).show()
            highlightElements(listaButtonChartsscreen, listaTextChartsscreen,  it as Button, airTextChartsscreen)
        }
        liquidButtonChartsscreen.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONE SIDEBAR: Cliccare su una di queste icone permette la navigazione veloce alla schermata corrispondente", Toast.LENGTH_SHORT).show()
            highlightElements(listaButtonChartsscreen, listaTextChartsscreen,  it as Button, liquidTextChartsscreen)
        }
        btButtonChartsscreen.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONE SIDEBAR: Cliccare su una di queste icone permette la navigazione veloce alla schermata corrispondente", Toast.LENGTH_SHORT).show()
            highlightElements(listaButtonChartsscreen, listaTextChartsscreen,  it as Button, btTextChartsscreen)
        }
        drawButtonChartsscreen.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONE SIDEBAR: Cliccare su una di queste icone permette la navigazione veloce alla schermata corrispondente", Toast.LENGTH_SHORT).show()
            highlightElements(listaButtonChartsscreen, listaTextChartsscreen,  it as Button, drawTextChartsscreen)
        }

        //....text
        backTextChartsscreen.setOnClickListener {
            highlightElements(listaButtonChartsscreen, listaTextChartsscreen,  backButtonChartsscreen, it as TextView)
        }
        patientTextChartsscren.setOnClickListener {
            highlightElements(listaButtonChartsscreen, listaTextChartsscreen,  patientButtonChartsscreen, it as TextView)
        }
        pressureTextChartsscreen.setOnClickListener {
            highlightElements(listaButtonChartsscreen, listaTextChartsscreen,  pressureButtonChartsscreen, it as TextView)
        }
        airTextChartsscreen.setOnClickListener {
            highlightElements(listaButtonChartsscreen, listaTextChartsscreen,  airButtonChartsscreen, it as TextView)
        }
        liquidTextChartsscreen.setOnClickListener {
            highlightElements(listaButtonChartsscreen, listaTextChartsscreen,  liquidButtonChartsscreen, it as TextView)
        }
        btTextChartsscreen.setOnClickListener {
            highlightElements(listaButtonChartsscreen, listaTextChartsscreen,  btButtonChartsscreen, it as TextView)
        }
        drawTextChartsscreen.setOnClickListener {
            highlightElements(listaButtonChartsscreen, listaTextChartsscreen,  drawButtonChartsscreen, it as TextView)
        }
        */
        createListeners(requireActivity(), listaCharts)

        /*
        //schermata Settings
        backButtonSettings.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONE SIDEBAR: Cliccare su una di queste icone permette la navigazione veloce alla schermata corrispondente", Toast.LENGTH_SHORT).show()
            highlightElements(listaButtonSettings, listaTextSettings,  it as Button, backTextSettings)
        }
        couplingButtonSettings.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONE SIDEBAR: Cliccare su una di queste icone permette la navigazione veloce alla schermata corrispondente", Toast.LENGTH_SHORT).show()
            highlightElements(listaButtonSettings, listaTextSettings,  it as Button, couplingTextSettings)
        }
        sidebarButtonSettings.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONE SIDEBAR: Cliccare su una di queste icone permette la navigazione veloce alla schermata corrispondente", Toast.LENGTH_SHORT).show()
            highlightElements(listaButtonSettings, listaTextSettings,  it as Button, sidebarTextSettings)
        }
        backTextSettings.setOnClickListener {
            highlightElements(listaButtonSettings, listaTextSettings,  backButtonSettings, it as TextView)
        }
        couplingTextSettings.setOnClickListener {
            highlightElements(listaButtonSettings, listaTextSettings,  couplingButtonSettings, it as TextView)
        }
        sidebarTextSettings.setOnClickListener {
            highlightElements(listaButtonSettings, listaTextSettings,  sidebarButtonSettings, it as TextView)
        }
        */
        createListeners(requireActivity(),listaSettings)

        /*
        //schermata coupling
        backButtonCoupling.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONE SIDEBAR: Cliccare su una di queste icone permette la navigazione veloce alla schermata corrispondente", Toast.LENGTH_SHORT).show()
            highlightElements(listaButtonCoupling, listaTextCoupling,  it as Button, backTextCoupling)
        }
        searchButtonCoupling.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONE SIDEBAR: Cliccare su una di queste icone permette la navigazione veloce alla schermata corrispondente", Toast.LENGTH_SHORT).show()
            highlightElements(listaButtonCoupling, listaTextCoupling,  it as Button, searchTextCoupling)
        }
        btButtonCoupling.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONE SIDEBAR: Cliccare su una di queste icone permette la navigazione veloce alla schermata corrispondente", Toast.LENGTH_SHORT).show()
            highlightElements(listaButtonCoupling, listaTextCoupling,  it as Button, btTextCoupling)
        }
        pairedButtonCoupling.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONE SIDEBAR: Cliccare su una di queste icone permette la navigazione veloce alla schermata corrispondente", Toast.LENGTH_SHORT).show()
            highlightElements(listaButtonCoupling, listaTextCoupling,  it as Button, pairedTextCoupling)
        }
        unpairedButtonCoupling.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONE SIDEBAR: Cliccare su una di queste icone permette la navigazione veloce alla schermata corrispondente", Toast.LENGTH_SHORT).show()
            highlightElements(listaButtonCoupling, listaTextCoupling,  it as Button, unpairedTextCoupling)
        }
        sidebarButtonCoupling.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONE SIDEBAR: Cliccare su una di queste icone permette la navigazione veloce alla schermata corrispondente", Toast.LENGTH_SHORT).show()
            highlightElements(listaButtonCoupling, listaTextCoupling,  it as Button, sidebarTextCoupling)
        }

        backTextCoupling.setOnClickListener {
            highlightElements(listaButtonSettings, listaTextSettings,  backButtonCoupling, it as TextView)
        }
        searchTextCoupling.setOnClickListener {
            highlightElements(listaButtonSettings, listaTextSettings,  searchButtonCoupling, it as TextView)
        }
        btTextCoupling.setOnClickListener {
            highlightElements(listaButtonSettings, listaTextSettings,  btButtonCoupling, it as TextView)
        }
        pairedTextCoupling.setOnClickListener {
            highlightElements(listaButtonSettings, listaTextSettings,  pairedButtonCoupling, it as TextView)
        }
        unpairedTextCoupling.setOnClickListener {
            highlightElements(listaButtonSettings, listaTextSettings,  unpairedButtonCoupling, it as TextView)
        }
        sidebarTextCoupling.setOnClickListener {
            highlightElements(listaButtonSettings, listaTextSettings,  sidebarButtonCoupling, it as TextView)
        }
        */
        createListeners(requireActivity(), listaCoupling)


        //schermata Coupling
        /*binding.backIconCouplingscreenHelp.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONA BACK: Cliccando su questa icona si torna alla schermata delle impostazioni", Toast.LENGTH_SHORT).show()
        }
        binding.sidebarIconCouplingscreenHelp.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONE LATERALI: Cliccando su una di queste icone si torna velocemente alla schermata corrispondente", Toast.LENGTH_SHORT).show()
        }
        binding.pairIconCouplingscreenHelp.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONE UNITA' ACCOPPIATE: Cliccando su una di queste icone si effettua il disaccoppiamento della unità corrispondente", Toast.LENGTH_SHORT).show()
        }
        binding.unpairIconCouplingscreenHelp.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONE UNITA' DISACCOPPIATE: Cliccando su una di queste icone si effettua l'accoppiamente della unità corrispondente", Toast.LENGTH_SHORT).show()
        }
        binding.btIconCouplingscreenHelp.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONA CONNESSIONE BLUETOOTH: Questa Icona mostra l'app e' attualmente connessa con una unità", Toast.LENGTH_SHORT).show()
        }
        binding.searchIconCouplingscreenHelp.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONA RICERCA UNITA': Questa icona mostra se e' in atto una ricerca di unità nelle vicinanze", Toast.LENGTH_SHORT).show()
        }*/

        /*
        //schermata historical
        backButtonHistoricalscreen.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONE SIDEBAR: Cliccare su una di queste icone permette la navigazione veloce alla schermata corrispondente", Toast.LENGTH_SHORT).show()
            highlightElements(listaButtonHistoricalscren, listaTextHistoricalscreen,  it as Button, backTextHistoricalscreen)
        }
        visualizationButtonHistoricalscreen.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONE SIDEBAR: Cliccare su una di queste icone permette la navigazione veloce alla schermata corrispondente", Toast.LENGTH_SHORT).show()
            highlightElements(listaButtonHistoricalscren, listaTextHistoricalscreen,  it as Button, visualizationTextHistoricalscreen)
        }
        unitButtonHistoricalScreen.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONE SIDEBAR: Cliccare su una di queste icone permette la navigazione veloce alla schermata corrispondente", Toast.LENGTH_SHORT).show()
            highlightElements(listaButtonHistoricalscren, listaTextHistoricalscreen,  it as Button, unitTextHistoricalscreen)
        }
        //se si tiene premuto una delle unità compare il menu di selezione ACTIVE
        unitButtonHistoricalScreen.setOnLongClickListener {
            Toast.makeText(requireActivity(), "LUNGO.......", Toast.LENGTH_SHORT).show()
            showSelectedScreen(listaScreens, historicalActiveLayoutView, false)
            true
        }

        sidebarButtonHistoricalScreen.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONE SIDEBAR: Cliccare su una di queste icone permette la navigazione veloce alla schermata corrispondente", Toast.LENGTH_SHORT).show()
            highlightElements(listaButtonHistoricalscren, listaTextHistoricalscreen,  it as Button, sidebarTextHistoricalscreen)
        }

        backTextChartsscreen.setOnClickListener {
            highlightElements(listaButtonHistoricalscren, listaTextHistoricalscreen,  backButtonHistoricalscreen, it as TextView)
        }
        visualizationTextHistoricalscreen.setOnClickListener {
            highlightElements(listaButtonHistoricalscren, listaTextHistoricalscreen,  visualizationButtonHistoricalscreen, it as TextView)
        }
        unitTextHistoricalscreen.setOnClickListener {
            highlightElements(listaButtonHistoricalscren, listaTextHistoricalscreen,  unitButtonHistoricalScreen, it as TextView)
        }
        sidebarTextHistoricalscreen.setOnClickListener {
            highlightElements(listaButtonHistoricalscren, listaTextHistoricalscreen,  sidebarButtonHistoricalScreen, it as TextView)
        }
        */
        createListeners(requireActivity(), listaHistorical)

        //schermata historicalActive
        backButtonHistoricalActive.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONE SIDEBAR: Cliccare su una di queste icone permette la navigazione veloce alla schermata corrispondente", Toast.LENGTH_SHORT).show()
            highlightElements(listaButtonHistoricalActive, listaTextHistoricalActive,  it as Button, backTextHistoricalActive)
        }
        visualizationButtonHistoricalActive.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONE SIDEBAR: Cliccare su una di queste icone permette la navigazione veloce alla schermata corrispondente", Toast.LENGTH_SHORT).show()
            highlightElements(listaButtonHistoricalActive, listaTextHistoricalActive,  it as Button, visualizationTextHistoricalActive)
        }
        unitButtonHistoricalActive.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONE SIDEBAR: Cliccare su una di queste icone permette la navigazione veloce alla schermata corrispondente", Toast.LENGTH_SHORT).show()
            highlightElements(listaButtonHistoricalActive, listaTextHistoricalActive,  it as Button, unitTextHistoricalActive)
        }
        //se si tiene premuto una delle unità scompare il menu di selezione ACTIVE
        unitButtonHistoricalActive.setOnLongClickListener {
            Toast.makeText(requireActivity(), "ICONE SIDEBAR: Cliccare su una di queste icone permette la navigazione veloce alla schermata corrispondente", Toast.LENGTH_SHORT).show()
            showSelectedScreen(listaScreens, historicalscreenLayoutView, false)
            true
        }
        sidebarButtonHistoricalActive.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONE SIDEBAR: Cliccare su una di queste icone permette la navigazione veloce alla schermata corrispondente", Toast.LENGTH_SHORT).show()
            highlightElements(listaButtonHistoricalActive, listaTextHistoricalActive,  it as Button, sidebarTextHistoricalActive)
        }
        renameButtonHistoricalActive.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONE SIDEBAR: Cliccare su una di queste icone permette la navigazione veloce alla schermata corrispondente", Toast.LENGTH_SHORT).show()
            highlightElements(listaButtonHistoricalActive, listaTextHistoricalActive,  it as Button, renameTextHistoricalActive)
        }
        deleteButtonHistoricalActive.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONE SIDEBAR: Cliccare su una di queste icone permette la navigazione veloce alla schermata corrispondente", Toast.LENGTH_SHORT).show()
            highlightElements(listaButtonHistoricalActive, listaTextHistoricalActive,  it as Button, deleteTextHistoricalActive)
        }
        sendButtonHistoricalActive.setOnClickListener {
            Toast.makeText(requireActivity(), "ICONE SIDEBAR: Cliccare su una di queste icone permette la navigazione veloce alla schermata corrispondente", Toast.LENGTH_SHORT).show()
            highlightElements(listaButtonHistoricalActive, listaTextHistoricalActive,  it as Button, sendTextHistoricalActive)
        }


        backTextHistoricalActive.setOnClickListener {
            highlightElements(listaButtonHistoricalActive, listaTextHistoricalActive,  backButtonHistoricalActive, it as TextView)
        }
        visualizationTextHistoricalActive.setOnClickListener {
            highlightElements(listaButtonHistoricalActive, listaTextHistoricalActive,  visualizationButtonHistoricalActive, it as TextView)
        }
        unitTextHistoricalActive.setOnClickListener {
            highlightElements(listaButtonHistoricalActive, listaTextHistoricalActive,  unitButtonHistoricalActive, it as TextView)
        }
        sidebarTextHistoricalActive.setOnClickListener {
            highlightElements(listaButtonHistoricalActive, listaTextHistoricalActive,  sidebarButtonHistoricalActive, it as TextView)
        }
        renameTextHistoricalActive.setOnClickListener {
            highlightElements(listaButtonHistoricalActive, listaTextHistoricalActive,  renameButtonHistoricalActive, it as TextView)
        }
        deleteTextHistoricalActive.setOnClickListener {
            highlightElements(listaButtonHistoricalActive, listaTextHistoricalActive,  deleteButtonHistoricalActive, it as TextView)
        }
        sendTextHistoricalActive.setOnClickListener {
            highlightElements(listaButtonHistoricalActive, listaTextHistoricalActive,  sendButtonHistoricalActive, it as TextView)
        }

        return binding.root
    }

    //funzione che cambia selettivamente la View  SULLA BASE DI UN PARAMETRO TIPIZZATO
    //si puo' fare una funzione piu' generale E DA METTERE DENTRO UNA CLASSE DI FUNZIONI DI UTILITà GENERALE (che prende un secondo parametro Array di Layout)
    //se si sceglie la voce mostra tutte, mostra tutte le schermate!!! oppure si puo' anche mettere la voce Next!!! E LE MOSTRA IN ORDINE!!!
    //CON MOSTRA TUTTE SI PUO' ANCHE TOGLIERE HISTORICALACTIVE!!!
    fun showSelectedScreen(elencoLayout: Array<LinearLayout>, selezione:LinearLayout, mostraTutte: Boolean = false){
        Log.d("giuseppeIstruzioni", "SELEZIONATO ALL SCREEN ${elencoLayout} selezione: ${selezione} e booleano: ${mostraTutte}")
        if(mostraTutte) {
            //Log.d("giuseppeIstruzioni", "SELEZIONATO ALL SCREEN ${elencoLayout} selezione: ${selezione} e booleano: ${mostraTutte}")
            for (i in elencoLayout) {i.setVisible(true)}
            return
        }
        //si deve togliere visibilità a tutti gli elementi tranne uno
        for (i in elencoLayout) {
            if(i!=selezione) {
                i.setVisible(false)
                Log.d("giuseppeShowSelected", "trovato un elemento DIVERSO ${selezione}")
            }
            else {
                i.setVisible(true)
                Log.d("giuseppeShowSelected", "Trovato un elemento uguale ${selezione}")
            }
            //i.setVisible(false)
        }
    }

    /* Si puo' evidenziare anche il TextView cambiando il colore del testo in giallo
        TUTTI I PULSANTI SONO GIA' LEGGERMENTE SOPRADIMENSIONATI e hanno già BACKGROUNDTINT E STROKE
        DA UNIFORMARE IN LAYOUT XML COLORE E SPESSORE!!!!!!!!!
        SI EVIDENZIA ANCHE IL TEXTVIEW IN QUALCHE MODO!!!!!!
        NB: PRENDE ANCHE COME PARAMETRO UNA TEXTVIEW ASSOCIATA DA EVIDENZIARE!!!!
    */
    fun highlightElements(listaButton: Array<Button>, listaText: Array<TextView>, selezione: Button, testo: TextView){
        for(i in listaButton) {
            if(i!=selezione) { i.setBackgroundResource(R.drawable.border_help)}
            else { i.setBackgroundResource(R.drawable.highlight_border_help)}
        }
        for (i in listaText) {
            if(i!=testo) {
                i.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.full_trasparent))
                i.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
            else {
                i.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.yellow))
                i.setTextColor(ContextCompat.getColor(requireContext(), R.color.azzurro))
            }
        }
    }


    //SUFFICIENTE IL SOLO PULSANTE COME ELEMENTO CHE CONTRADDISTINGUE LA SELEZIONE  (e una lista ovviamente)
    /**
     * Funzione che evidenzia i due elementi selezionati rimuovendo altre evidenziazioni
     */
    fun highlightElement( listElements: Array<Triple<Button, TextView, Int?>>, selection: View) {
        for(i in listElements) {
            //se diverso dalla selezione rimuovi evidenziazione a button e Text
            if(i.first != selection) {
                //metti sfondo trasparente a pulsante
                i.first.setBackgroundResource(R.drawable.border_help)
                //cambia sfondo e colore alla textView
                i.second.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.full_trasparent))
                i.second.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
            else {
                //cambia sfondo a pulsanti e alla textview
                i.first.setBackgroundResource(R.drawable.highlight_border_help)
                i.second.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.yellow))
                i.second.setTextColor(ContextCompat.getColor(requireContext(), R.color.azzurro))
            }
        }
    }

    /*DIALOG CHE PERMETTE DI VISUALIZZARE INFORMAZIONI  (STRINGHE DA RISORSE) E PERMETTE DI NAVIGARE o rimuovere il dialogo
        AL POSTO DEL TOAST!!!!
        SI PUO' COMPIERE QUALSIASI ALTRA FUNZIONE
        SI PUO' METTERE UNA LAMBDA COME PARAMETRO (che compie delle cose) e la rende universale e semplifica la scrittura!!!!
        Alcuni elementi non hanno la possibilità di navigare (quindi potrebbe prendere un parametro relativo alla schermata a cui navigare
        e dove non presente il dialog mostrerebbe un solo pulsante!!!!!!!!!!!!!!!!!!!!!
        TITOLO E CONTENUTO NON DOVREBBERO ESSERE HARDCODED!!!!! da risorse

        Si instanzia con un binding si usa getRoot() e si accede agli altri elementi interni

        SI E' CREATO UN CUSTOM LAYOUT DI ALERT DIALOG CON DEI PULSANTI E SI SONO MESSI I LISTENERS SUCCESSIVAMENTE
        (si sono creati una serie di pulsanti che compiono delle azioni!!!!!!
        con visulizzazione dinamica e sfondo dinamico etc...
        IN QUESTO MODO SI PUO' CAMBIARE ANCHE IL FONDO DEL BUTTON !!!!
        sostituendo Button con androidx.appcompat.widget.AppCompatButton

        SI DOVREBBE METTERE BINDING DENTRO ALERT DIALOG !!!!! oppure no?

        In questo modo sono stati tolti titolo e message

        INTERESSANTE: https://www.digitalocean.com/community/tutorials/android-alert-dialog-using-kotlin

        VEDIAMO IL BORDO ARROTONDATO SE POSSIBILE!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        VERIFICARE CHE NON CRASHI RIPETENDO DIVERSE VOLTE L'OPERAZIONE DI APERTURA DI ALERTDIALOG!!!!!!!!!!!!!!!!!!!!!!!!

        senza usate il binding si puo' usare un altro modo:
        val customLayout: View = layoutInflater.inflate(R.layout.infodialog_help, null)

        IL LISTENER CON IL DISMISS DEL DIALOGO SI METT SUCCESSIVAMENTE

        Se non si vuole controllare il tipe si puo' anche usare DatabindingUtil
        val infoDialogBinding: InfodialogHelpBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
        R.layout.infodialog_help, null, false)

        si puo' mettere titolo e messaggio, pulsanti semplicemente in questo modo:
            .setTitle(titolo)
            .setMessage(contenuto)

            .setPositiveButton("Naviga",
                DialogInterface.OnClickListener { dialog, which ->
                    showSelectedScreen(listaScreens, listaScreens[1])
                    dialog.dismiss()
                })
            .setNegativeButton("Rimani",  DialogInterface.OnClickListener { dialog, which ->  dialog.dismiss() })

        CON UN CUSTOM LAYOUT DI ALERT DIALOG E' STATO POSSIBILE CAMBIARE BUTTON CON UN ALTRO ELEMENTO
         e cosi in questo modo cambiare anche lo sfondo

        I DUE PULSANTI DOVREBBERO ESSERE CONCATENATI ORIZZONTALMENTE A LIVELLO GRAFICO
        Cosi' rimuovendo GONE uno, l'altro viene posizionato al centro della linea

     */
    fun infoDialog(c: Context, button: Button, destination: Int? = null ) {

        val stringaTitle: String = getStringFromView(c, button, 0)
        val stringaContenuto: String = getStringFromView(c, button, 1)

        //binding al layout e aggiornamento titoli e contenuto (verificare che non ci voglia onCreate in alertDialog)
        val infoDialogBinding: InfodialogHelpBinding = InfodialogHelpBinding.inflate(LayoutInflater.from(c))
        infoDialogBinding.titleTextInfoDialog.text = stringaTitle
        infoDialogBinding.contentTextInfoDialog.text = stringaContenuto

        val navigateButton : Button = infoDialogBinding.navigateButtonHelp
        val infoImage : ImageView = infoDialogBinding.imageInfoDialogHelp

        //e qui carica l'immagine!!!
        infoImage.setImageResource(R.drawable.scroll)

        //rintraccia id dalla View (button) attuale e id
        //SE IL NOME E' GENERICO DOVREBBE RESTITUIRE ERRORE!! (per verifica) oppure creare generico
        /*val name = if (button.id != View.NO_ID) resources.getResourceEntryName(
            button.id
        ) else ""*/
        //SI PUO' ANCHE FORMATTARE IL NOME AL CONTRARIO, CON TITLE PRIMA, DIPENDE DALLA LEGGIBILITA'
        /*val stringaTitle: String = getStringFromView(c, button)
        val stringaContenuto: String = getStringFromView(c, button)*/
        //val titolo :String = c.getString(R.string.settings_homescreen_help)

        //val res: Resources = c.resources
        //AGGIUSTARE E VERIFICARE COME CONDIZIONI IN CASO NON TROVASSE
        //val titoloName : String = res.getString(res.getIdentifier(title, "string", c.getPackageName() ?: "" ))
        //val titleString = getString(R.string.)
        //val packageName: String = c.getPackageName()
        //val resId = resources.getIdentifier(title, "string", packageName)
        //val stringaId = c.getString(resId)

        //val titolo : String = getStringFromId(c, name+"_titolo")
        //Toast.makeText(c, "NOME DELLA VIEW ATTUALE ${button} e stringa titolo: ${titolo} ", Toast.LENGTH_SHORT).show()

        //settingsIconHomescreenHelp
        //Toast.makeText(c, "NOME DELLA VIEW ATTUALE ${name} e nome titolo: ${titolo} e stringa: ${resId} ", Toast.LENGTH_SHORT).show()

        val dialog: AlertDialog = AlertDialog.Builder(c, R.style.AlertDialogTheme)
            .setView(infoDialogBinding.root)
            .create()
        dialog.show()

        //se destination e' null nascondi il pulsante di naviga
        if(destination == null) { navigateButton.setVisible(false)}
        else { navigateButton.setVisible(true)}

        //listeners dei due pulsanti - modificano anche lo spinner
        navigateButton.setOnClickListener {
            dialog.dismiss()
            //naviga usando lo spinner
            //showSelectedScreen(listaScreens, listaScreens[1])
            //DOVE SI PUO' NAVIGA USANDO LO SPINNER, ALTRIMENTI, con riferimento alla navigazione interna alle schermate utilizza showSelected
            when (destination) {
                0 -> screensSpinner.setSelection(1)
                1 -> screensSpinner.setSelection(2)
                2 -> screensSpinner.setSelection(3)
                3 -> screensSpinner.setSelection(4)
                4 -> screensSpinner.setSelection(5)
                5 -> screensSpinner.setSelection(6)
                6 -> screensSpinner.setSelection(7)
                else -> screensSpinner.setSelection(0)
            }
            Toast.makeText(c, "Toast da listener", Toast.LENGTH_SHORT).show()
        }

        infoDialogBinding.resumeButtonHelp.setOnClickListener {
            dialog.dismiss()
        }
    }


    //extension function per far apparire e scomparire il layout con le tre icone in alto a destra
    //SI PUO' METTERE ALL'INTERNO DI UNA CLASSE DI FUNZIONI UTILITA PIU' GENERALI
    fun View.setVisible(visible: Boolean) {
        visibility = if (visible) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    //restituisce una stringa da id
    //facciamo in modo che restituisca una stringa da View invece che da id
    //SI DEVE METTERE UNA ENUM!!!!!!!!!
    //DOVREBBE RESTITUIRE IN OGNI CASO QUALCHE COSA   (una stringa placeholder) !!!!!!!!!!!!!!!!!!!!!!!!
    /**
     * RESTITUISCE UNA STRINGA ASSOCIATA ALLA VIEW:
     *
     * 0 corrisponde al nome della View + "_title"
     *
     * 1 corrisponde al nome della View + "_content
     *
     * 2 corrisponde al nome della View + "_description"
     *
     * 3 corrisponde al nome della View + "_message"
     *
     * Come valore di default ho impostato la stringa con il titolo
     */
    fun getStringFromView(c: Context, view: View, richiesta: Int = 0 ): String {
        val viewId = if (view.id != View.NO_ID) resources.getResourceEntryName(
            view.id
        ) else ""

        var stringId : String = viewId
        when(richiesta) {
            0 -> stringId += "_title"
            1 -> stringId += "_content"
            2 -> stringId += "_description"
            3 -> stringId += "_message"
        }
        //titolo sulla base della richiesta
        //val stringId: String = viewId+"_title"
        val packageName = c.packageName
        //METTERE CONDIZIONE E VERIFICARE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! TRY CATCH
        val resId: Int = getResources().getIdentifier(stringId, "string", packageName)
        return getString(resId)
    }

    //CREA LISTENERS A PARTIRE DA UNA TRIPLA DI ELEMENTI ASSOCIATI
    //SI PUO' ANCHE PASSARE UNA LISTA DIRETTAMENTE!!!!!!!!!!!!!!!!!!!!!!!!!!
    fun createListeners( c: Context, lista: Array<Triple<Button, TextView, Int?>>) {

        //per ogni triade metti listener su pulsante e su testo (e all'interno di questo emetti Toast, evidenzia, dialogo con navigazione
        for(i in lista){
            //listener pulsante
            i.first.setOnClickListener {
                //recupera il messaggio a partire dalla View DEL PULSANTE!!!!
                val message: String = getStringFromView(c, it, 3)
                Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()

                //evidenzia pulsante e textview corrispondente rimuovendo le altre evidenziazioni degli altri elementi in lista!!!!
                highlightElement(lista, i.first)

                //Emetti InfoDialog con navigazione o meno
                infoDialog(c, it as Button, i.third )
            }
            i.second.setOnClickListener {
                highlightElement(lista, i.first)
            }
        }
    }
}
