package com.chiachat.kbls.bls.fields

import com.ionspin.kotlin.bignum.integer.BigInteger

abstract class Field {
    abstract val Q: BigInteger
    abstract val extension: Int

    abstract operator fun unaryMinus(): Field

    abstract operator fun plus(other: Any): Field

    abstract operator fun minus(other: Any): Field

    abstract operator fun times(other: Any): Field

    abstract operator fun compareTo(other: Any): Int

    abstract fun toStringFull(): String

    abstract fun toBytes(): UByteArray

    abstract fun fromBytes(bytes: UByteArray, q: BigInteger): Field

    abstract fun pow(other: Any): Field

    abstract fun qi_power(i: BigInteger): Field

    abstract fun invert(): Field

    abstract fun floorDiv(other: Any): Field

    abstract fun modSqrt(other: Any): Field

    abstract fun zero(Q: BigInteger): Field

    abstract fun one(Q: BigInteger): Field

    abstract fun fromFq(Q: BigInteger, fq: Fq): Fq
}
