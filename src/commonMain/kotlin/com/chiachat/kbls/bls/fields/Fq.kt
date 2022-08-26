package com.chiachat.kbls.bls.fields

import com.chiachat.kbls.bech32.KHex
import com.chiachat.kbls.bls.util.Math.modPow
import com.chiachat.kbls.bls.util.ONE
import com.chiachat.kbls.bls.util.TWO
import com.chiachat.kbls.bls.util.ZERO
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign
import com.ionspin.kotlin.bignum.integer.toBigInteger

class Fq(override val Q: BigInteger, otherValue: BigInteger) : Field() {

    override val extension: Int = 1
    val value: BigInteger

    init {
        value = otherValue.mod(Q)
    }

    override fun unaryMinus(): Fq {
        return Fq(Q, value.negate())
    }

    override fun plus(other: Any): Fq {
        return when (other) {
            is Fq -> Fq(Q, value + other.value)
            else -> throw NotImplementedException()
        }
    }

    override fun minus(other: Any): Fq {
        return when (other) {
            is Fq -> Fq(Q, value + -other.value)
            else -> throw NotImplementedException()
        }
    }

    override fun times(other: Any): Fq {
        return when (other) {
            is BigInteger -> Fq(this.Q, this.value * other)
            is Fq -> Fq(Q, value * other.value)
            else -> throw NotImplementedException()
        }
    }

    override fun compareTo(other: Field): Int {
        return when (other) {
            is Fq -> value.compareTo(other.value)
            else -> throw NotImplementedException()
        }
    }

    override fun toString(): String {
        return toStringFull()
//        val s = KHex(value).toString()
//        val s2 = if (s.length > 10) s.take(8) + ".." + s.takeLast(5) else s
//        return "Fq($s2)"
    }

    fun toStringFull(): String {
        return "Fq(${KHex(value)})"
    }

    override fun toBytes(): UByteArray {
        return value.toUByteArray()
    }

    override fun toHex(): KHex = KHex(value)
    override fun toBool(): Boolean = true

    override infix fun pow(exponent: BigInteger): Fq {
        val result = when (exponent) {
            BigInteger.ZERO -> Fq(Q, ONE)
            BigInteger.ONE -> Fq(Q, value)
            else -> {
                if (exponent mod TWO == ZERO) {
                    Fq(Q, value * value) pow (exponent / 2)
                } else {
                    (Fq(Q, value * value) pow (exponent / 2)) * this
                }
            }
        }
        return result
    }

    override fun qiPower(i: Int): Fq {
        return this
    }

    override fun inverse(): Fq {
        var (x0, x1, y0, y1) = listOf(ONE, ZERO, ZERO, ONE)
        var a = Q
        var b = value
        while (a != ZERO) {
            val q = b / a
            val tempB = b
            b = a
            a = tempB mod a
            val tempX0 = x0
            x0 = x1
            x1 = tempX0 - q * x1
            val tempY0 = y0
            y0 = y1
            y1 = tempY0 - q * y1
        }
        return Fq(Q, x0)
    }

    override operator fun div(other: Any): Fq {
        val otherFq: Fq = when (other) {
            is BigInteger -> Fq(Q, other)
            is Fq -> other
            else -> throw NotImplementedException()
        }
        return this * otherFq.inverse()
    }

    fun modSqrt(): Fq {
        if (value == ZERO) {
            return Fq(Q, ZERO)
        } else if (modPow(value, this.Q.minus(ONE).div(TWO), this.Q) != ONE) {
            throw ValueException("No sqrt exists")
        } else if (this.Q.mod(4.toBigInteger()) === 3.toBigInteger()) {
            return Fq(
                this.Q,
                modPow(this.value, (this.Q + ONE) / 4.toBigInteger(), this.Q)
            )
        } else if (this.Q.mod(8.toBigInteger()) === 5.toBigInteger()) {
            return Fq(
                this.Q,
                modPow(this.value, (this.Q + 3.toBigInteger()) / 8.toBigInteger(), this.Q)
            )
        }

        var S = ZERO
        var q = Q - ONE
        while (q mod TWO == ZERO) {
            q /= TWO
            S += ONE
        }

        var z = ZERO
        for (i in ZERO..Q) {
            val euler = modPow(i, this.Q.minus(ONE).div(TWO), this.Q)
            if (euler == BigInteger(-1) mod Q) {
                z = i
                break
            }
        }

        var M = S

        var c = modPow(z, q, this.Q)
        var t = modPow(this.value, q, this.Q)
        var R = modPow(this.value, q.plus(ONE).div(TWO), this.Q)

        while (true) {
            if (t == ZERO) return Fq(Q, ZERO)
            if (t == ONE) return Fq(Q, R)
            var i = ZERO
            var f = t
            while (f != ONE) {
                f = f.pow(TWO) mod Q
                i += ONE
            }

            val b = modPow(c, modPow(TWO, M - i - ONE, this.Q), this.Q)

            M = i
            c = b.pow(TWO) mod Q
            t = (t * c) mod Q
            R = (R * b) mod Q
        }
    }

    override fun zero(Q: BigInteger): Fq {
        return Fq(Q, ZERO)
    }

    override fun one(Q: BigInteger): Fq {
        return Fq(Q, ONE)
    }

    override fun fromHex(Q: BigInteger, hex: KHex): Fq {
        return nil.fromBytes(Q, hex.byteArray)
    }

    override fun fromFq(Q: BigInteger, fq: Fq): Fq {
        return fq
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        return other is Fq && this.value == other.value && this.Q == other.Q
    }

    override fun fromBytes(q: BigInteger, bytes: UByteArray): Fq {
        if (bytes.size != 48) {
            throw InvalidByteArraySizeException()
        } else {
            return Fq(q, BigInteger.fromUByteArray(bytes, Sign.POSITIVE))
        }
    }

    override fun nil(): Field = nil

    override fun isZero(): Boolean = this.value == ZERO

    companion object {
        val nil = Fq(ONE, ZERO)
    }
}
