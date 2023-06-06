package com.example.bttesting

//e' una data class che utilizza essenziamente paired per la schermata Pair e selected per in cambio di sfondo quando prova a connettersi (e toglie al termine del tentativo)
data class DeviceData(val deviceName: String?,val deviceHardwareAddress: String, var paired:Boolean = false, var selected: Boolean = false /* var proximity: Boolean = false, var powerSignal: Int = 0*/){

    override fun equals(other: Any?): Boolean {
        val deviceData = other as DeviceData
        return deviceHardwareAddress == deviceData.deviceHardwareAddress
    }

    override fun hashCode(): Int {
        return deviceHardwareAddress.hashCode()
    }

}
