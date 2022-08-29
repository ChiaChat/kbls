@file:OptIn(ExperimentalUnsignedTypes::class)

package com.chiachat.kbls.bls.util

import com.chiachat.kbls.bls.fields.NotImplementedException
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign
import com.ionspin.kotlin.bignum.integer.toBigInteger
import com.soywiz.krypto.sha256

val N1 = BigInteger(-1)
val ZERO = BigInteger.ZERO
val ONE = BigInteger.ONE
val TWO = BigInteger.TWO

fun byteListToBigInteger(vararg bytes: Int): BigInteger {
    return BigInteger.fromUByteArray(bytes.map { it.toUByte() }.toUByteArray(), Sign.POSITIVE)
}

fun Int.toBytes(size: Int, type: String = "big"): UByteArray {
    return when (type) {
        "big" -> {
            val bytes = this.toBigInteger().toUByteArray()
            UByteArray(size - bytes.size).also { it.fill(0.toUByte()) } + bytes
        }
        else -> throw NotImplementedException()
    }
}

fun UByteArray.sha256(): UByteArray = this.toByteArray().sha256().bytes.toUByteArray()
