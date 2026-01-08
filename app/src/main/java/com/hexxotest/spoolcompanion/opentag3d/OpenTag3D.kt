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
        val data = mutableListOf<Byte>()

        data.add((tagVersion shr 8).toByte())
        data.add((tagVersion and 0xFF).toByte())

        data.addAll(materialBase.toByteArray(Charsets.UTF_8).take(5).toMutableList())
        data.addAll(materialMod.toByteArray(Charsets.UTF_8).take(5).toMutableList())
        data.addAll(manufacturer.toByteArray(Charsets.UTF_8).take(16).toMutableList())
        data.addAll(colorName.toByteArray(Charsets.UTF_8).take(32).toMutableList())

        data.addAll(encodeColor(colors.getOrElse(0) { Color.Black }))
        data.addAll(encodeColor(colors.getOrElse(1) { Color(0x00000000) }))
        data.addAll(encodeColor(colors.getOrElse(2) { Color(0x00000000) }))
        data.addAll(encodeColor(colors.getOrElse(3) { Color(0x00000000) }))

        val diameterBytes = ((targetDiameter * 1000).toInt()).toBigEndianBytes()
        data.addAll(diameterBytes.toList())

        val weightBytes = (targetWeight.toInt()).toBigEndianBytes()
        data.addAll(weightBytes.toList())

        data.add((printTemp / 5).toInt().toByte())
        data.add((bedTemp / 5).toInt().toByte())

        val densityBytes = ((density * 1000).toInt()).toBigEndianBytes()
        data.addAll(densityBytes.toList())

        val tdBytes = 0.toBigEndianBytes()
        data.addAll(tdBytes.toList())

        return data.toByteArray()
    }

    private fun encodeColor(color: Color): List<Byte> {
        val argb = (color.value.toLong() and 0xFFFFFFFF).toInt()
        return listOf(
            ((argb shr 16) and 0xFF).toByte(),
            ((argb shr 8) and 0xFF).toByte(),
            (argb and 0xFF).toByte(),
            ((argb shr 24) and 0xFF).toByte()
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
