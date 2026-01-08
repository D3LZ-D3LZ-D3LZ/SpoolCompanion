package com.hexxotest.spoolcompanion.opentag3d

import androidx.compose.ui.graphics.Color

data class OpenTag3D(
    val tagVersion: Int = 20,
    val materialBase: String,
    val materialMod: String = "",
    val manufacturer: String,
    val colorName: String,
    val colors: List<Color>,
    val targetDiameter: Double,
    val targetWeight: Double,
    val printTemp: Double,
    val bedTemp: Double,
    val density: Double
) {
    fun encodeToBytes(): ByteArray {
        val data = ByteArray(111)

        data[0] = ((tagVersion shr 8).toByte())
        data[1] = (tagVersion and 0xFF).toByte()

        materialBase.toByteArray(Charsets.UTF_8).take(5).forEachIndexed { i, byte -> data[2 + i] = byte }
        materialMod.toByteArray(Charsets.UTF_8).take(5).forEachIndexed { i, byte -> data[7 + i] = byte }
        manufacturer.toByteArray(Charsets.UTF_8).take(16).forEachIndexed { i, byte -> data[12 + i] = byte }
        colorName.toByteArray(Charsets.UTF_8).take(32).forEachIndexed { i, byte -> data[28 + i] = byte }

        encodeColor(colors.getOrElse(0) { Color.Black }).forEachIndexed { i, byte -> data[43 + i] = byte }
        encodeColor(colors.getOrElse(1) { Color.Transparent }).forEachIndexed { i, byte -> data[47 + i] = byte }
        encodeColor(colors.getOrElse(2) { Color.Transparent }).forEachIndexed { i, byte -> data[51 + i] = byte }
        encodeColor(colors.getOrElse(3) { Color.Transparent }).forEachIndexed { i, byte -> data[55 + i] = byte }

        val diameterBytes = ((targetDiameter * 1000).toInt()).toBigEndianBytes()
        data[58] = diameterBytes[0]
        data[59] = diameterBytes[1]

        val weightBytes = (targetWeight.toInt()).toBigEndianBytes()
        data[60] = weightBytes[0]
        data[61] = weightBytes[1]

        data[62] = (printTemp / 5).toInt().toByte()
        data[63] = (bedTemp / 5).toInt().toByte()

        val densityBytes = ((density * 1000).toInt()).toBigEndianBytes()
        data[64] = densityBytes[0]
        data[65] = densityBytes[1]

        val tdBytes = 0.toBigEndianBytes()
        data[70] = tdBytes[0]
        data[71] = tdBytes[1]

        return data
    }

    private fun encodeColor(color: Color): List<Byte> {
        val argb = (color.value and 0xFFFFFFFFUL)
        return listOf(
            ((argb shr 16) and 0xFFUL).toInt().toByte(),
            ((argb shr 8) and 0xFFUL).toInt().toByte(),
            ((argb shr 0) and 0xFFUL).toInt().toByte(),
            ((argb shr 24) and 0xFFUL).toInt().toByte()
        )
    }

    private fun Int.toBigEndianBytes(): ByteArray {
        return byteArrayOf(
            (this shr 8).toByte(),
            (this and 0xFF).toByte()
        )
    }

    companion object {
        const val MIME_TYPE = "application/opentag3d"
    }
}
