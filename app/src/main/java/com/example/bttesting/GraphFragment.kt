package com.example.bttesting

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.bttesting.databinding.GraphFragmentBinding
import com.example.bttesting.viewModels.BluetoothViewModel
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.MPPointF
import java.io.File
import java.util.ArrayList

class GraphFragment : Fragment() {

    private val btViewModel: BluetoothViewModel by activityViewModels()

    private lateinit var grafico: LineChart
    private lateinit var graficoCombinato: CombinedChart
    var downDialOpen:Boolean = false
    private lateinit var lungsRelative: RelativeLayout
    private lateinit var airRelative: RelativeLayout
    private lateinit var liquidRelative: RelativeLayout
    private var tempGraph: Pair<Int, MutableList<MutableList<Float>>>? = null //o meglio nullable     per poter rilanciare la media con i valori temporanei
    private lateinit var binding: GraphFragmentBinding


    //per visualizzazione offline
    var offline: Boolean? = null
    var patientUri: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val args = GraphFragmentArgs.fromBundle(requireArguments())

        //elimina la coroutine svuotanto la variabile LiveRimasti - da mettere con funzione e liveData - per la navigazione di ritorno a LiveData
        btViewModel.reimpostaNavGraph()

        //questi sono i dati che vengono inviati quando si giunge qui da historical data (uri e booleano)
        offline = args.offline
        patientUri = args.uri

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate<GraphFragmentBinding>(inflater, R.layout.graph_fragment, container, false)

        //se si naviga da historical data il valore della variabile offline viene cambiato a true
        if(offline==true){
            binding.userNumberGraph.textSize= 18f
            //per il nome del paziente
            val file= File(patientUri!!.toUri().path)
            val folder = requireActivity().getExternalFilesDir("Documents")
            val nome = file.toString().replace("$folder/", "").dropLast(4)

            if(nome.length>17){binding.userNumberGraph.text = nome.drop(18) }
            else{binding.userNumberGraph.text = nome }

            binding.patientTextViewGraph.text = "File"
            binding.patientTextViewGraph.textSize = 18f
        }
        else {
            var nomeDevice: String? = btViewModel.btService.btDevice?.name?.drop(4) ?: "-----"
            binding.userNumberGraph.text = nomeDevice
        }

        lungsRelative = binding.lungsRelativeGraph
        airRelative = binding.airRelativeGraph
        liquidRelative = binding.liquidRelativeGraph

        grafico = binding.chart

        graficoCombinato = binding.combineChart

        binding.backBtnGraph.setOnClickListener {
            requireActivity().onBackPressed()
        }

        lungsRelative.setOnClickListener {
            if(offline==true){patientUri?.let { btViewModel.leggiFileOffline(patientUri!!,1) }} //se variabile offline e' true leggi file
            else {btViewModel.lanciaGrafico(1)}
            lungsRelative.backgroundTintList = context?.let { it1 -> ContextCompat.getColorStateList(it1, R.color.orange) }
            airRelative.backgroundTintList = context?.let { it1 -> ContextCompat.getColorStateList(it1, R.color.material_on_primary_disabled) }
            liquidRelative.backgroundTintList = context?.let { it1 -> ContextCompat.getColorStateList(it1, R.color.material_on_primary_disabled) }
        }

        airRelative.setOnClickListener {
            if(offline==true){patientUri?.let { btViewModel.leggiFileOffline(patientUri!!,2) }} //se variabile offline e' true leggi file
            else {btViewModel.lanciaGrafico(2) }
            airRelative.backgroundTintList = context?.let { it1 -> ContextCompat.getColorStateList(it1, R.color.orange) }
            liquidRelative.backgroundTintList = context?.let { it1 -> ContextCompat.getColorStateList(it1, R.color.material_on_primary_disabled) }
            lungsRelative.backgroundTintList = context?.let { it1 -> ContextCompat.getColorStateList(it1, R.color.material_on_primary_disabled) }
        }

        liquidRelative.setOnClickListener {
            if(offline==true){patientUri?.let { btViewModel.leggiFileOffline(patientUri!!,3) }} //se variabile offline e' true leggi file
            else {btViewModel.richLiquidiLive=false; btViewModel.lanciaGrafico(3) }
            liquidRelative.backgroundTintList = context?.let { it1 -> ContextCompat.getColorStateList(it1, R.color.orange) }
            airRelative.backgroundTintList = context?.let { it1 -> ContextCompat.getColorStateList(it1, R.color.material_on_primary_disabled) }
            lungsRelative.backgroundTintList = context?.let { it1 -> ContextCompat.getColorStateList(it1, R.color.material_on_primary_disabled) }
        }

        btViewModel.textChart.observe(viewLifecycleOwner, Observer {
            //binding.contentChart.text = it
            tempGraph = it
            //visualizzaGrafico(4,it)
            visualizzaGrafico(it)
        })

        //se non connesso e
        btViewModel.connesso.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it){ binding.btIconGraph.setImageResource(R.drawable.ic_baseline_bluetooth_100_connected) }
            else{ binding.btIconGraph.setImageResource(R.drawable.ic_baseline_bluetooth_100);
                if(offline==false){ binding.userNumberGraph.text="----"}}
        })

        return binding.root
    }


    override fun onResume() {
        super.onResume()
        lancioIniziale()
    }

    /**
     * Funzione che lancia prima il secondo grafico a seconda se online o offline
     */
    fun lancioIniziale(){
        if(offline==true){patientUri?.let { btViewModel.leggiFileOffline(patientUri!!,2) }} //se variabile offline e' true leggi file
        else {btViewModel.lanciaGrafico(2) }
        airRelative.backgroundTintList = context?.let { it1 -> ContextCompat.getColorStateList(it1, R.color.orange) }
        liquidRelative.backgroundTintList = context?.let { it1 -> ContextCompat.getColorStateList(it1, R.color.material_on_primary_disabled) }
        lungsRelative.backgroundTintList = context?.let { it1 -> ContextCompat.getColorStateList(it1, R.color.material_on_primary_disabled) }
        this.findNavController().navigate(GraphFragmentDirections.actionGraphFragmentToDownloadChartDialogue3());
        downDialOpen=true
    }

    //da mettere istruzione when se meglio
    fun visualizzaGrafico(/*media : Int = 4,*/list:Pair<Int, MutableList<MutableList<Float>>>){

        //ULTIMO MINUTO DELLE 100 ORE da aggiustare
        if(list.second[0].size==6000){list.second[0].removeLast()}
        if(list.second[1].size==6000){list.second[1].removeLast()}

        if(list.first==1) {

            generalSetsLineChart()

            //si prendono i valori della lista minima
            //val listaMinima = list.second[0].windowed(media, 1) { it.average().toFloat() }.toMutableList()
            val listaMinima = list.second[0].windowed(4, 1) { it.average().toFloat() }.toMutableList()
            var entriesMin: MutableList<Entry> = ArrayList()
            for (i in 0 until listaMinima.size) { var entry: Entry = Entry(i.toFloat()/60, listaMinima[i])
                entriesMin.add(entry)
            }

            //e i valori della lista massima
            //val listaMassima = list.second[1].windowed(media, 1) { it.average().toFloat() }.toMutableList()
            val listaMassima = list.second[1].windowed(4, 1) { it.average().toFloat() }.toMutableList()
            var entriesMax: MutableList<Entry> = ArrayList()
            for (i in 0 until listaMassima.size) { var entry: Entry = Entry(i.toFloat()/60, listaMassima[i])
                entriesMax.add(entry)
            }
            Log.d("giuseppeConteggio", "Quantità dati Pressione prima serie: ${entriesMin.size} e seconda: ${entriesMax.size}")

            //descrizioni e dimensioni assi laterali
            //binding.leftAxis.text="Pressione Minima [cmH2O]"
            //binding.rightAxis.text= "Pressione Massima [cmH2O]"
            //Nel primo grafico si vogliono togliere descrizione sulle assi e mettere linea orizzontale sottile
            binding.leftAxis.text=""
            binding.rightAxis.text= ""

            //si e' deciso per una scala fissa come nella versione precedente (prima era su massimi e minimi)
            grafico.axisLeft.axisMinimum = -30f
            grafico.axisLeft.axisMaximum = 20f
            grafico.axisLeft.axisLineColor = requireContext().getColor(R.color.white)
            grafico.axisLeft.textColor = requireContext().getColor(R.color.white)

            grafico.axisRight.axisMinimum= -30f
            grafico.axisRight.axisMaximum = 20f
            grafico.axisRight.axisLineColor = requireContext().getColor(R.color.white)

            //si deve disegnare anche la linea dello zero
            grafico.axisLeft.setDrawZeroLine(true)

            //Nella nuova versione si e' deciso di togliere l'asse di destra
            grafico.axisRight.setDrawLabels(false)

            //primo dataset
            //var minimaDataset = LineDataSet(entriesMin, "Press.Minima [cmH2O]")
            var minimaDataset = LineDataSet(entriesMin, "Min. Press. [cmH2O]")
            minimaDataset.setColor(requireContext().getColor(R.color.azzurro_verde))//colore linea
            minimaDataset.axisDependency = YAxis.AxisDependency.LEFT
            minimaDataset.setDrawCircles(false)
            minimaDataset.setDrawValues(false)
            minimaDataset.lineWidth=3f

            //secondo Dataset
            //var massimaDataset = LineDataSet(entriesMax, ("Press.Massima [cmH2O]"))
            var massimaDataset = LineDataSet(entriesMax, ("Max. Press. [cmH2O]"))
            massimaDataset.setColor(requireContext().getColor(R.color.holo_red_light))
            massimaDataset.axisDependency = YAxis.AxisDependency.RIGHT
            massimaDataset.setDrawCircles(false)
            massimaDataset.setDrawValues(false)
            massimaDataset.lineWidth=3f

            //datasets per grafici con molteplici Datasets
            val dataSets= ArrayList<ILineDataSet>()
            dataSets.add(minimaDataset)
            dataSets.add(massimaDataset)

            grafico.moveViewToX(((list.second[0].size/60)-11).toFloat()) //vengono controllato come dimensioni quando sono scaricati e quindi si prende una sola lista

            var lineData = LineData(dataSets)
            lineData.setDrawValues(false)
            grafico.data = lineData
            grafico.invalidate()
        }

        if(list.first==2){

            generalSetsLineChart()

            //si devono rimettere gli ultimi minuti dei due DB!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! su richiesta di Zavatti
            if(list.second[0].isNotEmpty()){list.second[0].removeLast()}
            if( list.second[1].isNotEmpty()){list.second[1].removeLast() }

            //LE DUE SERIE E I MASSIMI E MINIMI
            //val listaliquidi: MutableList<Float> = list.second[0].windowed(media, 1) { it.average().toFloat() }.toMutableList()
            val listaliquidi: MutableList<Float> = list.second[0].windowed(4, 1) { it.average().toFloat() }.toMutableList()
            var entriesLiq: MutableList<Entry> = ArrayList()
            for (i in 0 until listaliquidi.size) { var entry: Entry = Entry(i.toFloat()/60, listaliquidi[i])
                entriesLiq.add(entry)
            }
            var maxLiquidi = (listaliquidi.maxOrNull()) ?: 0f
            if(maxLiquidi<1)maxLiquidi=1f

            //dalle due liste si impostano anche dei valori minimi dei grafici (per garantire la visibilità del grafico)
            val listaAeree = list.second[1]
            var entriesAer: MutableList<Entry> = ArrayList()
            for (i in 0 until listaAeree.size) { var entry: Entry = Entry(i.toFloat()/60, listaAeree[i])
                entriesAer.add(entry)
            }

            Log.d("giuseppeConteggio", "Quantità dati AEREE prima serie: ${entriesLiq.size} e seconda: ${entriesAer.size}")

            var maxAeree = (listaAeree.maxOrNull()?.toFloat()) ?: 0f
            if(maxAeree<1)maxAeree=1f

            //descrizioni assi laterali
            /*binding.leftAxis.text="Volume Aspirato [m/min] Avg"
            binding.rightAxis.text= "Vuoto Applicato [cmH2O]"
            //binding.leftAxis.text="Air Leakages [ml/min] Avg"
            binding.rightAxis.text= "Vacuum Applied [cmH2O]"
            */
            binding.leftAxis.text=getString(R.string.air_leakages_legenda_charts)
            binding.rightAxis.text= getString(R.string.air_vacuum_legenda_charts)

            //valori minimi dei grafici
            grafico.axisLeft.axisMinimum = 0f
            grafico.axisLeft.axisMaximum = maxLiquidi
            grafico.axisRight.axisMinimum = 0f
            if(maxAeree<=30) {grafico.axisRight.axisMaximum = 30f } else{grafico.axisRight.axisMaximum=60f}   //se c'è un valore dell'aspirazione che supera i 30 cambia la scala del grafico a 60 invece per i liquidi autodimensiona
            grafico.axisRight.setLabelCount(6)   //per i label count dell'asse di destra si e' impostato un valore che da' multipli di 5 se scala 30 e multipli di 10 se scala 60

            //Nella nuova versione si e' deciso di togliere l'asse di destra
            grafico.axisRight.setDrawLabels(true)

            //poi si devono caricare i valori in un Linedataset e METTERE UNA LABEL!!!!!!
            //var liquidDataset = LineDataSet(entriesLiq, "Volume Aspirato [ml/min] Avg")
            //var liquidDataset = LineDataSet(entriesLiq, "Air Leakages [ml/min] Avg")
            var liquidDataset = LineDataSet(entriesLiq, getString(R.string.air_leakages_legenda_charts))
            liquidDataset.setColor(requireContext().getColor(R.color.azzurro_verde))//colore linea
            liquidDataset.axisDependency = YAxis.AxisDependency.LEFT
            liquidDataset.setDrawCircles(false)
            liquidDataset.setDrawValues(false)
            liquidDataset.lineWidth=3f

            //secondo Dataset
            //var aereeDataset = LineDataSet(entriesAer, ("Vuoto Applicato [cmH2O]"))
            var aereeDataset = LineDataSet(entriesAer, getString(R.string.air_vacuum_legenda_charts))
            aereeDataset.setColor(requireContext().getColor(R.color.holo_red_light))
            aereeDataset.axisDependency = YAxis.AxisDependency.RIGHT
            aereeDataset.setDrawCircles(false)
            aereeDataset.setDrawValues(false)
            aereeDataset.lineWidth=3f

            //datasets per grafici con molteplici Datasets
            val dataSets= ArrayList<ILineDataSet>()
            dataSets.add(liquidDataset)
            dataSets.add(aereeDataset)

            grafico.moveViewToX(((list.second[0].size/60)-11).toFloat()) //vengono controllato come dimensioni quando sono scaricati e quindi si prende una sola lista

            var lineData = LineData(dataSets)
            lineData.setDrawValues(false)
            grafico.data = lineData
            grafico.invalidate()
        }

        if(list.first==3){

            generalSetsBarChart()

            //per consentire una lettura piu' agevole dei dati viene aggiunto uno zero all'inizio della serie
            //POICHE' DI DATI SI RIFERISCONO AL PERIODO PRECEDENTE
            val raccoltaTotale: MutableList<Float> = (/*listOf(0f) + */list.second[0]).toMutableList()

            /*...altrimenti si calcola la nuova serie invocando una funzione ad hoc
            val sommatoriaRaccolta = calcolaSommatoria(raccoltaTotale)
            */

            //aggiungendo un primo elemento 0 ad entrambe le liste
            var entriesTot: MutableList<Entry> = ArrayList()
            for (i in 0 until raccoltaTotale.size) { var entry: Entry = Entry(i.toFloat()/60, raccoltaTotale[i])
                entriesTot.add(entry)
            }

            val raccoltaOra:MutableList<Float> = (listOf<Float>(0f)+list.second[1]).toMutableList()
            var entriesHour: MutableList<BarEntry> = ArrayList()
            for (i in 0 until raccoltaOra.size) { var entry: BarEntry = BarEntry(i.toFloat(), raccoltaOra[i])
                entriesHour.add(entry)
            }

            Log.d("giuseppeConteggio", "Quantità dati liquidi prima serie: ${entriesTot.size} e seconda: ${entriesHour.size}")

            //per verifica valori in entrata nel grafico (dopo essere stati salvati)
            Log.d("giuseppeRaccolta", "DIMENSIONI TOTALE DENTRO GRAPH ${raccoltaTotale.size} e PER ORA ${raccoltaOra.size}")

            //descrizioni assi
            /* Descrizione precedente
            binding.leftAxis.text="Raccolta liquido per ora [ml]"
            binding.rightAxis.text= "Raccolta liquido totale [ml]"
            binding.leftAxis.text="Liquid Amount per Hour [ml]"
            binding.rightAxis.text= "Total Liquid Amount [ml]"
            */
            binding.leftAxis.text= getString(R.string.liquid_houraxis_charts)
            binding.rightAxis.text= getString(R.string.liquid_totalaxis_charts)

            //poi si devono caricare i valori in un Linedataset e METTERE UNA LABEL!!!!!!
            //var totalDataset = LineDataSet(entriesTot, "Liquid Amount [ml]")
            var totalDataset = LineDataSet(entriesTot, getString(R.string.liquid_legenda_charts))
            totalDataset.setColor(requireContext().getColor(R.color.holo_red_light))//colore linea
            totalDataset.axisDependency = YAxis.AxisDependency.RIGHT
            totalDataset.setDrawCircles(false)
            totalDataset.valueTextColor = requireContext().getColor(R.color.white)
            totalDataset.valueTextSize=16f
            totalDataset.setDrawValues(false)

            //secondo Dataset
            var hourDataset = BarDataSet(entriesHour, ("∆Q [ml]"))
            hourDataset.setColor(requireContext().getColor(R.color.azzurro_verde))
            hourDataset.axisDependency = YAxis.AxisDependency.LEFT //il grafico a barre va a sinistra
            hourDataset.setDrawValues(false)

            //datasets per grafici con molteplici Datasets
            val dataSets= ArrayList<ILineDataSet>()
            dataSets.add(totalDataset)

            //sposta asseX alle ultime 12 ore
            graficoCombinato.moveViewToX((entriesHour.size-12).toFloat())

            //DA TOGLIERE (SOLO PER VISUALIZZAZIONE)
            graficoCombinato.axisRight.axisMinimum= -2f

            //SI PUO' PERSONALIZZARE IN MOLTI MODI!!!!!!!!!!e si passa ad un linedata
            var lineData = LineData(totalDataset) //e permette una personalizzazione ulteriore
            var barData = BarData(hourDataset)
            barData.barWidth=0.6f

            //MARKER (verificare in caso di array nullo!!)!!!!!!!!
            //condizione - if array dati non nullo!!!
            //val marker = context?.let {  CustomMarkerView(it, R.layout.custom_marker_view, raccoltaOra) }
            /*var marker: MarkerView? = null
            activity?.let {
                Log.d("giuseppeContext", "ENTRATO DENTRO MARKER $raccoltaOra")
                marker = CustomMarkerView(it, R.layout.custom_marker_view, raccoltaOra)
            }
            marker?.let {
                Log.d("giuseppeContext", "verifica context $context e marker $it")
                graficoCombinato.marker= it
            }*/

            var combinedData = CombinedData()
            combinedData.setData(barData)
            combinedData.setData(lineData)
            graficoCombinato.data= combinedData
            graficoCombinato.invalidate()
            graficoCombinato.isHighlightPerTapEnabled = true

            //LISTENER!!!
            //si deve mostrare il solo valore corrispondente PROCEDIMENTO!!!
            graficoCombinato.setOnChartValueSelectedListener(object : OnChartValueSelectedListener{
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    Log.d("giuseppeListener","Hai selezionato un valore $e e evidenziato $h")
                    //val highlight: Highlight = Highlight(0f, 10,0)
                    //SI PUO' METTERE UN LISTENER DIRETTAMENTE SUL VALORE SELEZIONATO?
                    //hightLightValue DIRETTAMENTE SENZA CREARE UNA INTERFACCIA!!!
                    graficoCombinato.highlightValue(h)
                    var marker2: MarkerView? = null
                    activity?.let {
                        Log.d("giuseppeContext", "ENTRATO DENTRO MARKER $raccoltaOra")
                        marker2 = CustomMarkerView(it, R.layout.custom_marker_view, raccoltaOra)
                    }
                    marker2?.let {
                        Log.d("giuseppeContext", "verifica context $context e marker $it")
                        it.refreshContent(e,h)
                        graficoCombinato.marker= it
                        //graficoCombinato.invalidate()
                    }
                }

                override fun onNothingSelected() {
                    Log.d("giuseppeListener", "Non e' stato selezionato niente")
                }
            })
        }
    }

    //le seguenti due funzioni rigurdano elementi generali (sfondo, colore testo lato, personalizzazione legenda etc..)
    fun generalSetsLineChart(){

        grafico.setVisible(true)
        graficoCombinato.setVisible(false)
        //imposta in modo che sia visibile il grafico nella sua interezza (0 significa con il massimo zoom out)
        grafico.zoom(0f, 0f, 0f, 0f)
        grafico.setBackgroundColor(requireContext().getColor(R.color.full_trasparent))
        grafico.setDrawGridBackground(false)
        grafico.legend.textColor=requireContext().getColor(R.color.white)
        grafico.legend.textSize=16f

        //per togliere la griglia
        grafico.axisLeft.setDrawGridLines(false) //sono delle linee orizzontali che partono dai valori delle assi
        grafico.axisLeft.axisLineColor = requireContext().getColor(R.color.azzurro_verde)
        grafico.axisLeft.textSize= 16f
        grafico.axisLeft.textColor = requireContext().getColor(R.color.azzurro_verde)
        grafico.axisLeft.typeface = Typeface.DEFAULT_BOLD
        grafico.axisLeft.axisLineWidth= 2.5f
        grafico.axisLeft.setDrawZeroLine(false) //si e' deciso di non disegnare la linea dello zero

        grafico.axisRight.setDrawGridLines(false)
        grafico.axisRight.axisLineColor = Color.RED
        grafico.axisRight.textSize= 16f
        grafico.axisRight.textColor = Color.RED
        grafico.axisRight.typeface = Typeface.DEFAULT_BOLD
        grafico.axisRight.axisLineWidth=2.5f
        grafico.axisRight.setDrawZeroLine(false)

        grafico.xAxis.setDrawGridLines(false)
        grafico.xAxis.axisLineColor = requireContext().getColor(R.color.white)
        grafico.xAxis.textSize=16f
        grafico.xAxis.textColor = requireContext().getColor(R.color.white)
        grafico.xAxis.typeface = Typeface.DEFAULT_BOLD
        grafico.xAxis.axisLineWidth=2.5f
        grafico.xAxis.position = XAxis.XAxisPosition.BOTTOM

        grafico.xAxis.setLabelCount(13,false)
        grafico.xAxis.isGranularityEnabled = true
        grafico.xAxis.granularity=1f
        grafico.xAxis.axisMinimum=0f
        grafico.xAxis.axisMaximum=150f  //se si volevano cento ore fisse
        grafico.isScaleXEnabled = true   //si puo' anche rimettere
        //grafico.setVisibleXRange(5f, 12f)
        //grafico.setVisibleXRangeMinimum(6f)
        grafico.setVisibleXRangeMaximum(12f) //se ci si allontana massimo si possono vedere 12 valori (non si puo' per esempio vedere 25 valori insieme) - di concerto con lo zoom

    }

    //impostazioni generali CombinedChart (la stessa cosa della funzione precedente ma con riferimento al grafico combinato)
    //IMPOSTAZIONI PER IL TERZO GRAFICO
    fun generalSetsBarChart(){
        //si modifica la visibilita' dei due grafici
        graficoCombinato.setVisible(true)
        grafico.setVisible(false)
        graficoCombinato.zoom(0f,0f,0f,0f) //si azzera lo zoom
        graficoCombinato.setBackgroundColor(requireContext().getColor(R.color.full_trasparent))
        graficoCombinato.setDrawGridBackground(false)
        graficoCombinato.legend.textColor = Color.WHITE
        graficoCombinato.legend.textSize = 16f

        //per togliere la griglia
        graficoCombinato.axisLeft.setDrawGridLines(false)
        graficoCombinato.axisLeft.axisLineColor = requireContext().getColor(R.color.azzurro_verde)
        graficoCombinato.axisLeft.textSize=16f
        graficoCombinato.axisLeft.textColor = requireContext().getColor(R.color.azzurro_verde)
        graficoCombinato.axisLeft.typeface = Typeface.DEFAULT_BOLD
        graficoCombinato.axisLeft.axisLineWidth=2.5f
        graficoCombinato.axisLeft.setDrawZeroLine(false)

        graficoCombinato.axisRight.setDrawGridLines(false)
        graficoCombinato.axisRight.axisLineColor = requireContext().getColor(R.color.holo_red_light)
        graficoCombinato.axisRight.textSize=16f
        graficoCombinato.axisRight.textColor= requireContext().getColor(R.color.holo_red_light)
        graficoCombinato.axisRight.typeface = Typeface.DEFAULT_BOLD
        graficoCombinato.axisRight.axisLineWidth=2.5f
        graficoCombinato.axisRight.setDrawZeroLine(false)

        //Nella nuova versione si e' deciso di togliere l'asse di destra
        grafico.axisRight.setDrawLabels(true)

        graficoCombinato.xAxis.setDrawGridLines(false)
        graficoCombinato.xAxis.axisLineColor = requireContext().getColor(R.color.white)
        graficoCombinato.xAxis.textSize=16f
        graficoCombinato.xAxis.textColor = requireContext().getColor(R.color.white)
        graficoCombinato.xAxis.typeface = Typeface.DEFAULT_BOLD
        graficoCombinato.xAxis.axisLineWidth=2.5f
        graficoCombinato.xAxis.position = XAxis.XAxisPosition.BOTTOM

        //ma force deve essere TRUE O FALSE?
        graficoCombinato.xAxis.setLabelCount(13,false)
        graficoCombinato.xAxis.isGranularityEnabled = true
        graficoCombinato.xAxis.granularity=1f
        graficoCombinato.xAxis.axisMinimum=0f
        graficoCombinato.xAxis.axisMaximum=150f
        graficoCombinato.isScaleXEnabled = true   //si puo' anche rimettere

        graficoCombinato.axisLeft.axisMinimum=0f
        graficoCombinato.axisRight.axisMinimum=0f

        graficoCombinato.setVisibleXRange(12f, 12f)
        graficoCombinato.xAxis.valueFormatter = DefaultValueFormatter(0)

    }

    //extension function per rimuovere uno tra i due grafici sovrapposti a turno
    fun View.setVisible(visible: Boolean) {
        visibility = if (visible) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }
}

//CLASSE PER IL CUSTOMMARKERVIEW!!!
//TOGLIERE IL DEBUG!!!!
class CustomMarkerView( context: Context, layout: Int, private val dataToDisplay: MutableList<Float>) : MarkerView(context, layout) {

    private var txtViewData: TextView? = null

    init {
        txtViewData = findViewById(R.id.txtViewData)
        Log.d("giuseppeRefresh", "dentro CustomMarkerView")
    }

    //quando fa' refresh?
    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        try {
            Log.d("giuseppeRefresh", "DENTRO REFRESH")
            val xAxis = e?.x?.toInt() ?: 0
            txtViewData?.text = dataToDisplay[xAxis].toString()
        } catch (e: IndexOutOfBoundsException) {
            Log.d("giuseppeRefresh","DENTRO CATCH DI REFRESH")
        }

        super.refreshContent(e, highlight)
    }

    //DA PERSONALIZZARE CON VALORI PROPRIORZONATI ALLA SCALA
    override fun getOffset(): MPPointF {
        //return MPPointF(-(width / 2f), -height.toFloat())
        return MPPointF(1f,-10f)
    }
}