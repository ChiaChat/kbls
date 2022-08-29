@file:OptIn(ExperimentalUnsignedTypes::class)

package com.chiachat.kbls.bls.fields

import com.chiachat.kbls.bech32.KHex
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger

sealed class Field {
    abstract val Q: BigInteger
    abstract val extension: Int

    abstract operator fun unaryMinus(): Field

    abstract operator fun plus(other: Any): Field

    abstract operator fun minus(other: Any): Field

    abstract operator fun times(other: Any): Field

    abstract operator fun compareTo(other: Field): Int

    abstract fun toBytes(): UByteArray

    abstract fun toHex(): KHex

    abstract fun toBool(): Boolean

    abstract fun pow(exponent: BigInteger): Field

    fun pow(exponent: Int): Field = pow(exponent.toBigInteger())

    abstract fun qiPower(i: Int): Field

    abstract fun inverse(): Field

    abstract operator fun div(other: Any): Field

    abstract fun nil(): Field

    abstract fun zero(Q: BigInteger): Field

    fun zero(Q: Int): Field = zero(Q.toBigInteger())

    abstract fun one(Q: BigInteger): Field

    fun one(Q: Int): Field = one(Q.toBigInteger())

    abstract fun fromBytes(Q: BigInteger, bytes: UByteArray): Field

    abstract fun fromHex(Q: BigInteger, hex: KHex): Field

    abstract fun fromFq(Q: BigInteger, fq: Fq): Field

    abstract override fun equals(other: Any?): Boolean

    abstract fun isZero(): Boolean
}
