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

        materialBase.toByteArray(Charsets.UTF_8).take(5).forEach { data.add(it) }
        materialMod.toByteArray(Charsets.UTF_8).take(5).forEach { data.add(it) }
        manufacturer.toByteArray(Charsets.UTF_8).take(16).forEach { data.add(it) }
        colorName.toByteArray(Charsets.UTF_8).take(32).forEach { data.add(it) }

        encodeColor(colors.getOrElse(0) { Color.Black }).forEach { data.add(it) }
        encodeColor(colors.getOrElse(1) { Color(0x00000000) }).forEach { data.add(it) }
        encodeColor(colors.getOrElse(2) { Color(0x00000000) }).forEach { data.add(it) }
        encodeColor(colors.getOrElse(3) { Color(0x00000000) }).forEach { data.add(it) }

        ((targetDiameter * 1000).toInt()).toBigEndianBytes().forEach { data.add(it) }
        (targetWeight.toInt()).toBigEndianBytes().forEach { data.add(it) }
        data.add((printTemp / 5).toInt().toByte())
        data.add((bedTemp / 5).toInt().toByte())
        ((density * 1000).toInt()).toBigEndianBytes().forEach { data.add(it) }
        0.toBigEndianBytes().forEach { data.add(it) }

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
