package com.example.bttesting.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel

/* DA METTERE COME FLOW!!!!!!!!!!!

 */

/* POTENZIALE VIEWMODEL SINGOLO PER DOPPIO STRATO CON VIEWMODELS DI SCOPE DIVERSO
    per ConnectFragment da mettere solo se la logica di calcolo diventa troppo per un solo ViewModel con scope di Activity
    si mette un doppio ViewModel con due scope diversi (uno generale connesso a service che fornisce i dati) e un altro che li elabora
 */

/* PRENDE LIVEDATA DA SERVICE CHE CANCELLA DISCOVERY QUANDO CONNESSO (rispetta SSOT) e ViewModel sganciato da Context
 */

/* La View non deve fare calcoli o richiedere informazioni, ma deve compiere operazioni semplici sui dati (meglio in Data Binding) e il ViewModel deve predisporre i dati
    per questo motivo, in assenza di un

 */

/*SI PUO' SPOSTARE DENTRO SERVICE E COLLEGARE I DATI CON LIVEDATA A VIEWMODEL CHE DEVE AVERE
    ma che succede se chiedono la riconnessione da altro fragment? esempio da grafico? il ciclo di vita
    e quando e' connesso cancella discovery, perciò deve essere messo in service e può operare anche in background (work manager permettendo)
 */

class ConnectFragmentViewmodel: ViewModel() {

    init {
        Log.d("giuseppeRecycler", "distrutto Viewmodel")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("giuseppeRecycler", "distrutto Viewmodel")
    }
}