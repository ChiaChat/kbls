package com.chiachat.kbls.bls.fields

import com.chiachat.kbls.bls.Fq
import com.ionspin.kotlin.bignum.integer.BigInteger

abstract class Field<T : Field<T>> {
    abstract val Q: BigInteger
    abstract val extension: BigInteger

    abstract fun zero(Q: BigInteger): Field<T>
    abstract fun one(Q: BigInteger): Field<T>
    abstract fun fromBytes(Q: BigInteger, bytes: UByteArray): Field<T>
    abstract fun fromHex(Q: BigInteger, hex: String): Field<T>
    abstract fun fromFq(Q: BigInteger, fq: Fq): Field<T>

    abstract fun clone(): Field<T>
    abstract fun toBytes(): UByteArray
    abstract fun toBool(): Boolean
    abstract fun toHex(): String
    abstract override fun toString(): String

    abstract fun negate(): Field<T>
    abstract fun inverse(): Field<T>
    abstract fun qiPower(i: Int): Field<T>
    abstract fun pow(exponent: BigInteger): Field<T>

    abstract fun addTo(value: BigInteger): Field<T>
    abstract fun multiplyWith(value: Field<*>): Field<*>
    abstract fun multiplyWith(value: BigInteger): Field<*>

    abstract fun add(value: Field<*>): Field<*>
    abstract fun add(value: BigInteger): Field<*>
    abstract fun subtract(value: Field<*>): Field<*>
    abstract fun subtract(value: BigInteger): Field<*>
    abstract fun multiply(value: Field<*>): Field<*>
    abstract fun multiply(value: BigInteger): Field<*>
    abstract fun divide(value: Field<*>): Field<*>
    abstract fun divide(value: BigInteger): Field<*>

    abstract fun equalTo(value: Field<*>): Boolean
    abstract fun equalTo(value: BigInteger): Boolean
    abstract fun equals(value: Field<*>): Boolean
    abstract fun equals(value: BigInteger): Boolean
    abstract fun lt(value: Field<T>): Boolean
    abstract fun gt(value: Field<T>): Boolean
    abstract fun lteq(value: Field<T>): Boolean
    abstract fun gteq(value: Field<T>): Boolean
}
