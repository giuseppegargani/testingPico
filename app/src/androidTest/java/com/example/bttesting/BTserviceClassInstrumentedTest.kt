package com.example.bttesting

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.example.bttesting.service.BluetoothService
import kotlinx.coroutines.Delay

import org.junit.Rule




/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

/* test strumentali
    dentro fragment con navigazione
    https://developer.android.com/jetpack/androidx/releases/navigation

    //ActivityScenarioRule on Kotlin
    https://stackoverflow.com/questions/54878598/how-do-i-use-activityscenarioruleactivity

 */

/* Fare tutta una serie di compiti di task

 */

@RunWith(AndroidJUnit4::class)
class BTserviceClassInstrumentedTest {

    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)
    val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.bttesting", appContext.packageName)
    }

    @Test
    fun verificaListaIniziale(){
        val serviceClass = BluetoothService(appContext)
        val lista = serviceClass.btDevicesList
        assertTrue(lista.isEmpty())
    }
}