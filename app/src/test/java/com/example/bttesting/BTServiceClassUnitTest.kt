package com.example.bttesting

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.example.bttesting.service.BluetoothService
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.manipulation.Ordering

/* Si deve fare il test di Flow con una FakeRepository
documentazione ufficiale
vedi https://developer.android.com/kotlin/flow/test
 */

/* Come impostare una Fake Repository e Dependency Injection
    Corso su Udacity: https://classroom.udacity.com/courses/ud940/lessons/9434e029-dce7-4550-93f2-18a224433e72/concepts/a7d06703-a6d8-4b5e-8960-d82be801702a
 */

/* USARE UN CONTEXT IN UNIT TEST:
https://stackoverflow.com/questions/2095695/android-unit-tests-requiring-context
    Adesso si deve usare applicationProvider!!!!
 */

class BTServiceClassUnitTest {

    val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    fun simple(): List<Int> = listOf(1, 2, 3)

    @Test
    fun main() {
        simple().forEach { value -> println(value) }
    }

    @Test
    fun listaInizialeVuota(){
        val serviceClass = BluetoothService(context)
        val lista = serviceClass.btDevicesList
        assertTrue(lista.isEmpty())
    }

}