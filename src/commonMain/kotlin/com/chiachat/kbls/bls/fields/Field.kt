package com.chiachat.kbls.bls.fields

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

    abstract fun fromBytes(bytes: UByteArray, q: BigInteger): T

    abstract fun pow(other: Any): T

    abstract fun qi_power(i: BigInteger): T

    abstract fun inverse(): T

    abstract fun floorDiv(other: Any): T

    abstract fun modSqrt(): T

    abstract fun zero(Q: BigInteger): T

    abstract fun one(Q: BigInteger): T

    abstract fun fromFq(Q: BigInteger, fq: Fq): T
}
