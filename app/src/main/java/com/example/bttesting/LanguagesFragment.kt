package com.example.bttesting

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.example.bttesting.databinding.LanguagesFragmentBinding
import com.example.bttesting.viewModels.BluetoothViewModel


/* CONCETTI:
    Si deve creare un file english ed un file generale?!?!? SIIII, perche' se si seleziona erroneamente una lingua si puo' tornare alla versione in inglese VERIFICA TORNARE AD INGLESE!!!!
        Si duplicano le stringhe
    In caso non trovi una stringa carica la versione in inglese!!!!
 */

class LanguagesFragment : Fragment() {

    //delegato che indica che il ciclo di vita e' quello della Activity correlata e non del fragment
    private val btViewModel: BluetoothViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<LanguagesFragmentBinding>(inflater, R.layout.languages_fragment, container, false)

        //impostazioni per le lingua
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity() /* Activity context */)
        val linguaggioShared = requireActivity().getSharedPreferences("lingue", Context.MODE_PRIVATE)
        //val linguaggio: String = linguaggioShared.getString("", "en") !!

        //SE NULL SI DEVE IMPOSTARE IL VALORE DI DEFAULT!!!!!! COMUNQUE PREFERENCES VA IN SHARED PREFERENCES!!!!!! e quindi si puo' impostare con una schermata di icone ad hoc!!!!!
        //val linguaggio: String = linguaggioShared.getString("linguaScelta", "en")!!


        //Listeners per i pulsanti di navigazione
        //listeners per pulsanti laterali
        binding.patientLogLanguages.setOnClickListener {
            this.findNavController().navigate(R.id.connectFragment)
        }

        binding.historicalLogoLanguages.setOnClickListener {
            this.findNavController().navigate(R.id.historicalFragment)
        }

        binding.settingsLogoLanguages.setOnClickListener {
            this.findNavController().navigate(R.id.settingsFragment)
        }

        binding.helpLogoLanguages.setOnClickListener {
            this.findNavController().navigate(R.id.helpFragment)
        }

        binding.backBtnLanguages.setOnClickListener {
            this.findNavController().navigate(R.id.settingsFragment)
        }

        //al momento della scelta si mette già la nuova lingua!!!!

        binding.englishLayoutLanguages.setOnClickListener {
            modificaLingua(linguaggioShared,"en")
            //Toast.makeText(requireActivity(), "SELEZIONATO LINGUA INGLESE", Toast.LENGTH_SHORT).show()
            restartDialog(requireActivity(), getString(R.string.title_english_dialog), getString(R.string.description_english_dialog), getString(R.string.positive_english_dialog), getString(R.string.negative_english_dialog))
        }
        binding.italianLayoutLanguages.setOnClickListener {
            modificaLingua(linguaggioShared, "it")
            //Toast.makeText(requireActivity(), "SELEZIONATO LINGUA ITALIANA", Toast.LENGTH_SHORT).show()
            restartDialog(requireActivity(),getString(R.string.title_italian_dialog), getString(R.string.description_italian_dialog), getString(R.string.positive_italian_dialog), getString(R.string.negative_italian_dialog))
        }
        binding.frenchLayoutLanguages.setOnClickListener {
            //modificaLingua(sharedPreferences,"en")
            //Toast.makeText(requireActivity(), "SELEZIONATO LINGUA FRANCESE", Toast.LENGTH_SHORT).show()
            restartDialog(requireActivity(), "Choose a language", "descrizione", "Riavvia Adesso", "Riavvia dopo")
        }
        binding.settingsBtnLanguages.setOnClickListener {
            startActivityForResult(Intent(Settings.ACTION_LOCALE_SETTINGS), 0);
        }

        return binding.root
    }

    //VERIFICARE E MIGLIORARE QUESTO CODICE!!!!!
    //CAPIRE DOVE SI DEVE METTERE putString e getString (e mettere stringhe)
    //DOVE SI DEVE METTERE RECREATE?? !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    fun modificaLingua(sharedLanguage: SharedPreferences, lingua: String){
        with (sharedLanguage.edit()) {
            putString("linguaScelta", lingua)
            apply()
        }
        val linguaggio: String = sharedLanguage.getString("linguaScelta", "en")!!
        Log.d("giuseppe", "SCRITTO DENTRO MODIFICA ${lingua} e impostato ${linguaggio}")
        //requireActivity().recreate()
    }

    //DEVE SVUOTARE GLI ELEMENTI SELEZIONATI IN RECYCLER UNA VOLTA CHIUSO
    fun restartDialog(c: Context, titolo:String, descrizione: String, positivo: String, negativo: String) {
        //val folder = requireActivity().getExternalFilesDir("Documents")
        //val nomeAttuale = file.toString().replace("$folder/", "").take(17) //pulito da eventuali altri nomi precedenti

        //.setTitle(getString(R.string.title_language_dialog))
        val dialog: AlertDialog = AlertDialog.Builder(c)
            .setTitle(titolo)
            .setMessage(descrizione)
            .setPositiveButton(positivo,
                DialogInterface.OnClickListener { dialog, which ->
                    btViewModel.disconnect()
                    //Questo si puo' anche non mettere? il seguente comando
                    //requireActivity().finish()
                    startActivity(Intent(requireActivity(), MainActivity::class.java))
                })
            .setNegativeButton(negativo,  DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
            .create()
        dialog.show()
    }
}