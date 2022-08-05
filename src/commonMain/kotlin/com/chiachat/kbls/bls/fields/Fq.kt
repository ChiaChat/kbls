package com.chiachat.kbls.bls.fields

import com.chiachat.kbls.bech32.KHex
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign

class Fq(override val Q: BigInteger, otherValue: BigInteger) : Field<Fq>() {
    override val extension: Int = 1

    val value: BigInteger

    init {
        value = otherValue % Q
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
            is Fq -> Fq(Q, value + other.value)
            else -> throw NotImplementedException()
        }
    }

    override fun times(other: Any): Fq {
        return when (other) {
            is Fq -> Fq(Q, value * other.value)
            else -> throw NotImplementedException()
        }
    }

    override fun compareTo(other: Any): Int {
        return when (other) {
            is Fq -> value.compareTo(other.value)
            else -> throw NotImplementedException()
        }
    }

    override fun toString(): String {
        val s = KHex(value).toString()
        val s2 = if (s.length > 10) s.take(8) + ".." + s.takeLast(5) else s
        return "Fq($s2)"
    }

    override fun toStringFull(): String {
        return "Fq(${KHex(value)})"
    }

    override fun toBytes(): UByteArray {
        return value.toUByteArray()
    }

    override fun fromBytes(bytes: UByteArray, q: BigInteger): Fq {
        if (bytes.size != 48) {
            throw InvalidByteArraySizeException()
        } else {
            return Fq(q, BigInteger.fromUByteArray(bytes, Sign.POSITIVE))
        }
    }

    override infix fun pow(other: Any): Fq {
        return when (other) {
            0 -> Fq(Q, ONE)
            1 -> Fq(Q, value)
            is BigInteger -> {
                if (other % 2 == ZERO) {
                    Fq(Q, value * value) `pow` (other / 2)
                } else {
                    (Fq(Q, value * value) `pow` (other / TWO)) * this
                }
            }

            else -> throw NotImplementedException()
        }
    }

    override fun qi_power(i: BigInteger): Fq {
        return this
    }

    override fun inverse(): Fq {
        var (x0, x1, y0, y1) = listOf(ONE, ZERO, ZERO, ONE)
        var a = Q
        var b = value
        while (a != ZERO) {
            var q = b / a
            b = a
            a = b % a
            x0 = x1
            x1 = x0 - q * x1
            y0 = y1
            y1 = y0 - q * y1
        }
        return Fq(Q, x0)
    }

    override fun floorDiv(other: Any): Fq {
        val otherFq: Fq = when (other) {
            is BigInteger -> Fq(Q, other)
            is Fq -> other
            else -> throw NotImplementedException()
        }
        return this * otherFq.inverse()
    }

    override fun modSqrt(): Fq {
        if (value == ZERO) {
            return Fq(Q, ZERO)
        }
        val exp = (Q - ONE) / BigInteger(2)
        if (value.pow(exp) % Q != ONE) {
            throw ValueException("No sqrt exists")
        }
        if (Q % BigInteger(4) == BigInteger(3)) {
            val exp = (Q + ONE) / BigInteger(4)
            return Fq(Q, value.pow(exp) % Q)
        }
        if (Q % BigInteger(8) == BigInteger(5)) {
            val exp = (Q + BigInteger(3)) / BigInteger(8)
            return Fq(Q, value.pow(exp) % Q)
        }

        var S = ZERO
        var q = Q - ONE
        while(q % TWO == ZERO){
            q /= TWO
            S += ONE
        }

        var z = ZERO
        for(i in ZERO .. Q){
            var euler = i.pow((Q - ONE) / TWO) % Q
            if(euler == BigInteger(-1) % Q)
                z = i
            break
        }

        var M = S

        var c = z.pow(q) % Q
        var t = value.pow(q) % Q
        var R = value.pow((q + ONE)/ TWO) % Q

        while (true){
            if(t == ZERO) return Fq(Q, ZERO)
            if(t == ONE) return Fq(Q, R)
            var i = ZERO
            var f = t
            while (f != ONE){
                f = f.pow(TWO) % Q
                i += ONE
            }
            val exp1 =  TWO.pow(M - i - ONE) % Q
            var b = c.pow(exp1) % Q

            M = i
            c = b.pow(TWO) % Q
            t = (t * c) % Q
            R = (R * b) % Q
        }
    }

    override fun zero(Q: BigInteger): Fq {
        return Fq(Q, ZERO)
    }

    override fun one(Q: BigInteger): Fq {
        return Fq(Q, ONE)
    }

    override fun fromFq(Q: BigInteger, fq: Fq): Fq {
        return fq
    }
}
