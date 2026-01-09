package com.hexxotest.spoolcompanion.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.hexxotest.spoolcompanion.opentag3d.OpenTag3D
import kotlin.collections.emptyList

class NfcTagViewModel : ViewModel() {

    var spoolId by mutableIntStateOf(-1)

    var filamentId by mutableIntStateOf(-1)

    var isDialogShown by mutableStateOf(false)

    var spoolName by mutableStateOf("")
    var vendorName by mutableStateOf("")
    var materialBase by mutableStateOf("")
    var materialMod by mutableStateOf("")
    var colorName by mutableStateOf("")
    var filamentColors by mutableStateOf(emptyList<Color>())
    var diameter by mutableStateOf(1.75)
    var weight by mutableStateOf(1000.0)
    var printTemp by mutableStateOf(220.0)
    var bedTemp by mutableStateOf(60.0)
    var density by mutableStateOf(1.24)

    fun getOpenTag3D(): OpenTag3D {
        val actualDiameter = if (diameter == 0.0) 1.75 else diameter
        val actualWeight = if (weight == 0.0) 1000.0 else weight
        val actualDensity = if (density == 0.0) 1.24 else density
        val actualPrintTemp = if (printTemp == 220.0) 220.0 else printTemp
        val actualBedTemp = if (bedTemp == 60.0) 60.0 else bedTemp

        return OpenTag3D(
            materialBase = if (materialBase.isEmpty()) "PLA" else materialBase,
            materialMod = materialMod,
            manufacturer = if (vendorName.isEmpty()) "Unknown" else vendorName,
            colorName = if (colorName.isEmpty()) "Custom" else colorName,
            colors = if (filamentColors.isEmpty()) listOf(Color.White) else filamentColors,
            targetDiameter = actualDiameter,
            targetWeight = actualWeight,
            printTemp = actualPrintTemp,
            bedTemp = actualBedTemp,
            density = actualDensity
        )
    }

}