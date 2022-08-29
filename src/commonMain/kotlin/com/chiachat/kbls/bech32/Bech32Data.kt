@file:OptIn(ExperimentalUnsignedTypes::class)

package com.chiachat.kbls.bech32

data class Bech32Data(
    val humanReadablePart: String,
    val data: UByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this === other) return true
        if (this::class != other::class) return false

        other as Bech32Data

        if (humanReadablePart != other.humanReadablePart) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = humanReadablePart.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }
}
