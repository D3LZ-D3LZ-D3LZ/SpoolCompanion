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
    var diameter by mutableStateOf(0.0)
    var weight by mutableStateOf(0.0)
    var printTemp by mutableStateOf(220.0)
    var bedTemp by mutableStateOf(60.0)
    var density by mutableStateOf(1.24)

    fun getOpenTag3D(): OpenTag3D {
        return OpenTag3D(
            materialBase = materialBase,
            materialMod = materialMod,
            manufacturer = vendorName,
            colorName = colorName,
            colors = filamentColors,
            targetDiameter = diameter,
            targetWeight = weight,
            printTemp = printTemp,
            bedTemp = bedTemp,
            density = density
        )
    }

}