package com.chiachat.kbls.bls.fields

import com.chiachat.kbls.bech32.KHex
import com.ionspin.kotlin.bignum.integer.BigInteger

sealed class Field {
    abstract val Q: BigInteger
    abstract val extension: Int


    abstract operator fun unaryMinus(): Field

    abstract operator fun plus(other: Any): Field

    abstract operator fun minus(other: Any): Field

    abstract operator fun times(other: Any): Field

    abstract operator fun compareTo(other: Any): Int

    abstract fun toStringFull(): String

    abstract fun toBytes(): UByteArray

    abstract fun toHex(): KHex


    abstract fun pow(exponent: BigInteger): Field

    abstract fun qiPower(i: Int): Field

    abstract fun inverse(): Field

    abstract fun floorDiv(other: Any): Field

    abstract fun modSqrt(): Field

    abstract fun zero(Q: BigInteger): Field

    abstract fun one(Q: BigInteger): Field

    abstract fun fromBytes(Q: BigInteger, bytes: UByteArray): Field

    abstract fun fromHex(Q: BigInteger, hex: KHex): Field

    abstract fun fromFq(Q: BigInteger, fq: Fq): Field

}
