package com.hexxotest.spoolcompanion.opentag3d

import androidx.compose.ui.graphics.Color

/**
 * OpenTag3D data class for encoding filament spool data to NFC tags.
 * 
 * Memory Map (Core - 111 bytes, addresses 0x00-0x6E):
 * 0x00-0x01: Tag Version (2 bytes, big-endian)
 * 0x02-0x06: Base Material Name (5 bytes UTF-8)
 * 0x07-0x0B: Material Modifiers (5 bytes UTF-8)
 * 0x0C-0x1A: Reserved/Padding (15 bytes)
 * 0x1B-0x2A: Manufacturer (16 bytes UTF-8)
 * 0x2B-0x4A: Color Name (32 bytes UTF-8)
 * 0x4B-0x4E: Color 1 RGBA (4 bytes)
 * 0x50-0x53: Color 2 RGBA (4 bytes) - Note: 0x4F is skipped per spec
 * 0x54-0x57: Color 3 RGBA (4 bytes)
 * 0x58-0x5B: Color 4 RGBA (4 bytes)
 * 0x5C-0x5D: Target Diameter (2 bytes, big-endian, in µm)
 * 0x5E-0x5F: Target Weight (2 bytes, big-endian, in g)
 * 0x60: Print Temp (1 byte, temp/5)
 * 0x61: Bed Temp (1 byte, temp/5)
 * 0x62-0x63: Density (2 bytes, big-endian, in µg/cm³)
 * 0x64-0x65: Transmission Distance (2 bytes, big-endian)
 */
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
        // Core section is 111 bytes (0x00-0x6E)
        val data = ByteArray(111)
        
        // 0x00-0x01: Tag Version (2 bytes, big-endian)
        data[0x00] = (tagVersion shr 8).toByte()
        data[0x01] = (tagVersion and 0xFF).toByte()
        
        // 0x02-0x06: Base Material Name (5 bytes UTF-8)
        writeUtf8(data, 0x02, materialBase, 5)
        
        // 0x07-0x0B: Material Modifiers (5 bytes UTF-8)
        writeUtf8(data, 0x07, materialMod, 5)
        
        // 0x0C-0x1A: Reserved/Padding (15 bytes) - already zero-initialized
        
        // 0x1B-0x2A: Manufacturer (16 bytes UTF-8)
        writeUtf8(data, 0x1B, manufacturer, 16)
        
        // 0x2B-0x4A: Color Name (32 bytes UTF-8)
        writeUtf8(data, 0x2B, colorName, 32)
        
        // 0x4B-0x4E: Color 1 RGBA (4 bytes)
        writeRgba(data, 0x4B, colors.getOrElse(0) { Color.Black })
        
        // 0x4F: Unused byte (gap between color blocks per spec)
        
        // 0x50-0x53: Color 2 RGBA (4 bytes)
        writeRgba(data, 0x50, colors.getOrElse(1) { Color.Transparent })
        
        // 0x54-0x57: Color 3 RGBA (4 bytes)
        writeRgba(data, 0x54, colors.getOrElse(2) { Color.Transparent })
        
        // 0x58-0x5B: Color 4 RGBA (4 bytes)
        writeRgba(data, 0x58, colors.getOrElse(3) { Color.Transparent })
        
        // 0x5C-0x5D: Target Diameter (2 bytes, big-endian, value in µm)
        writeBigEndianShort(data, 0x5C, (targetDiameter * 1000).toInt())
        
        // 0x5E-0x5F: Target Weight (2 bytes, big-endian, value in g)
        writeBigEndianShort(data, 0x5E, targetWeight.toInt())
        
        // 0x60: Print Temp (1 byte, temp/5)
        data[0x60] = (printTemp / 5).toInt().toByte()
        
        // 0x61: Bed Temp (1 byte, temp/5)
        data[0x61] = (bedTemp / 5).toInt().toByte()
        
        // 0x62-0x63: Density (2 bytes, big-endian, value in µg/cm³)
        writeBigEndianShort(data, 0x62, (density * 1000).toInt())
        
        // 0x64-0x65: Transmission Distance (2 bytes, big-endian) - default 0
        writeBigEndianShort(data, 0x64, 0)
        
        return data
    }

    /**
     * Write a UTF-8 string to the byte array at the specified position.
     * Truncates if too long, pads with zeros if too short.
     */
    private fun writeUtf8(data: ByteArray, startPos: Int, text: String, maxLength: Int) {
        val bytes = text.toByteArray(Charsets.UTF_8)
        val bytesToCopy = minOf(bytes.size, maxLength)
        
        // Copy the string bytes
        for (i in 0 until bytesToCopy) {
            data[startPos + i] = bytes[i]
        }
        
        // Pad remaining bytes with zeros
        for (i in bytesToCopy until maxLength) {
            data[startPos + i] = 0
        }
    }

    /**
     * Write an RGBA color to the byte array at the specified position.
     * Format: R, G, B, A (4 bytes total)
     */
    private fun writeRgba(data: ByteArray, startPos: Int, color: Color) {
        // Compose Color stores as ARGB in a ULong
        val colorValue = color.value
        
        // Extract RGBA components (Compose Color is ARGB format internally)
        val alpha = ((colorValue shr 56) and 0xFFUL).toInt()
        val red = ((colorValue shr 48) and 0xFFUL).toInt()
        val green = ((colorValue shr 40) and 0xFFUL).toInt()
        val blue = ((colorValue shr 32) and 0xFFUL).toInt()
        
        // Write as RGBA order per OpenTag3D spec
        data[startPos + 0] = red.toByte()
        data[startPos + 1] = green.toByte()
        data[startPos + 2] = blue.toByte()
        data[startPos + 3] = alpha.toByte()
    }

    /**
     * Write a 2-byte big-endian short to the byte array at the specified position.
     */
    private fun writeBigEndianShort(data: ByteArray, startPos: Int, value: Int) {
        data[startPos + 0] = ((value shr 8) and 0xFF).toByte()
        data[startPos + 1] = (value and 0xFF).toByte()
    }

    companion object {
        const val MIME_TYPE = "application/opentag3d"
    }
}
