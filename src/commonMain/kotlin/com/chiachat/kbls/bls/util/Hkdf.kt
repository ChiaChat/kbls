@file:OptIn(ExperimentalUnsignedTypes::class)

package com.chiachat.kbls.bls.util

import com.soywiz.krypto.HMAC
import kotlin.math.ceil

const val BLOCK_SIZE = 32

object Hkdf {
    fun extract(salt: UByteArray, ikm: UByteArray): UByteArray {
        val hash = HMAC.hmacSHA256(salt.toByteArray(), ikm.toByteArray())
        return hash.bytes.toUByteArray()
    }

    fun expand(length: Int, prk: UByteArray, info: UByteArray): UByteArray {
        val blocks = ceil(length.toDouble() / BLOCK_SIZE).toInt()
        var bytesWritten = 0
        val okm = mutableListOf<UByte>()
        var temp = UByteArray(0)
        for (i in 1..blocks) {
            val test = if (i == 1) {
                info.copyOf() + 1.toUByte()
            } else {
                temp + info + i.toUByte()
            }
            temp = extract(prk, test)
            var toWrite = length - bytesWritten
            if (toWrite > BLOCK_SIZE) {
                toWrite = BLOCK_SIZE
            }
            okm.addAll(temp.slice(0 until toWrite))
            bytesWritten += toWrite
        }
        if (bytesWritten != length) throw Exception("Failed to write $length bytes")
        return okm.toUByteArray()
    }

    fun extractExpand(length: Int, key: UByteArray, salt: UByteArray, info: UByteArray): UByteArray {
        val prk = extract(salt, key)
        return expand(length, prk, info)
    }
}
