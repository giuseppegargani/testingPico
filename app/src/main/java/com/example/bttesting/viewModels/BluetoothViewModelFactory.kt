package com.example.bttesting.viewModels

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bttesting.service.BluetoothService

/* DA METTERE PRIVATE DENTRO COSTRUTTORI E ANCHE VAL DENTRO CLASSE E TUTTE le altre

    - FACILITA' DI TESTING:  (come da video su udacity)  Siccome si inseriscono in Factory le dipendenze (service) di ViewModel si migliora la possibilità di testing!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        Testing su service e ViewModel
        https://classroom.udacity.com/courses/ud9012/lessons/fcd3f9aa-3632-4713-a299-ea39939d6fd7/concepts/15877d76-9040-40d6-8978-a8209fa6f627
 */

class BluetoothViewModelFactory (
    val btService : BluetoothService, val application: Application, val context: Context) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BluetoothViewModel::class.java)) {
            return BluetoothViewModel(btService, application, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}