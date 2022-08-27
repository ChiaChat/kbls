package com.chiachat.kbls.bls.util

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign

val N1 = BigInteger(-1)
val ZERO = BigInteger.ZERO
val ONE = BigInteger.ONE
val TWO = BigInteger.TWO

fun byteListToBigInteger(vararg bytes: Int): BigInteger{
    return BigInteger.fromUByteArray(bytes.map { it.toUByte() }.toUByteArray(), Sign.POSITIVE)
}
