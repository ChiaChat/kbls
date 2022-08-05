package com.chiachat.kbls.bls.fields

import com.chiachat.kbls.bech32.KHex
import com.ionspin.kotlin.bignum.integer.BigInteger

abstract class Field<T: Field<T>> {
    abstract val Q: BigInteger
    abstract val extension: Int


    abstract operator fun unaryMinus(): T

    abstract operator fun plus(other: Any): T

    abstract operator fun minus(other: Any): T

    abstract operator fun times(other: Any): T

    abstract operator fun compareTo(other: Any): Int

    abstract fun toStringFull(): String

    abstract fun toBytes(): UByteArray

    abstract fun toHex(): KHex


    abstract fun pow(exponent: BigInteger): T

    abstract fun qiPower(i: Int): T

    abstract fun inverse(): T

    abstract fun floorDiv(other: Any): T

    abstract fun modSqrt(): T

    abstract fun zero(Q: BigInteger): T

    abstract fun one(Q: BigInteger): T

    abstract fun fromBytes(Q: BigInteger, bytes: UByteArray): T

    abstract fun fromHex(Q: BigInteger, hex: KHex): T

    abstract fun fromFq(Q: BigInteger, fq: Fq): T

}
