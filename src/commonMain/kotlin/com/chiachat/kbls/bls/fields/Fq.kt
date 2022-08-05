package com.chiachat.kbls.bls.fields

import com.chiachat.kbls.bech32.KHex
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign

class Fq(override val Q: BigInteger, otherValue: BigInteger) : Field() {
    override val extension: Int = 1

    val value: BigInteger

    init {
        value = otherValue % Q
    }

    override fun unaryMinus(): Field {
        return Fq(Q, value.negate())
    }

    override fun plus(other: Any): Field {
        return when (other) {
            is Fq -> Fq(Q, value + other.value)
            else -> throw NotImplementedException()
        }
    }

    override fun minus(other: Any): Field {
        return when (other) {
            is Fq -> Fq(Q, value + other.value)
            else -> throw NotImplementedException()
        }
    }

    override fun times(other: Any): Field {
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

    override fun fromBytes(bytes: UByteArray, q: BigInteger): Field {
        if (bytes.size != 48) {
            throw InvalidByteArraySizeException()
        } else {
            return Fq(q, BigInteger.fromUByteArray(bytes, Sign.POSITIVE))
        }
    }

    override infix fun pow(other: Any): Field {
        return when (other) {
            0 -> Fq(Q, BigInteger.ONE)
            1 -> Fq(Q, value)
            is BigInteger -> {
                if (other % 2 == BigInteger.ZERO) {
                    Fq(Q, value * value) `pow` (other / 2)
                } else {
                    (Fq(Q, value * value) `pow` (other / BigInteger.TWO)) * this
                }
            }

            else -> throw NotImplementedException()
        }
    }

    override fun qi_power(i: BigInteger): Field {
        return this
    }

    override fun invert(): Field {
        var (x0, x1, y0, y1) = listOf(BigInteger.ONE, BigInteger.ZERO, BigInteger.ZERO, BigInteger.ONE)
        var a = Q
        var b = value
        while (a != BigInteger.ZERO) {
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

    override fun floorDiv(other: Any): Field {
        val otherFq: Fq = when (other) {
            is BigInteger -> Fq(Q, other)
            is Fq -> other
            else -> throw NotImplementedException()
        }
        return this * otherFq.invert()
    }

    override fun modSqrt(other: Any): Field {
        if (value == BigInteger.ZERO) {
            return Fq(Q, BigInteger.ZERO)
        }
        val exp = (Q - BigInteger.ONE) / BigInteger(2)
        if (value.pow(exp) % Q != BigInteger.ONE) {
            throw ValueException("No sqrt exists")
        }
        if (Q % BigInteger(4) == BigInteger(3)) {
            val exp = (Q + BigInteger.ONE) / BigInteger(4)
            return Fq(Q, value.pow(exp) % Q)
        }
        if (Q % BigInteger(8) == BigInteger(5)) {
            val exp = (Q + BigInteger(3)) / BigInteger(8)
            return Fq(Q, value.pow(exp) % Q)
        }

        var S = BigInteger.ZERO
        var q = Q - BigInteger.ONE
        while(q % BigInteger.TWO == BigInteger.ZERO){
            q /= BigInteger.TWO
            S += BigInteger.ONE
        }

        var z = BigInteger.ZERO
        for(i in BigInteger.ZERO .. Q){
            var euler = i.pow((Q - BigInteger.ONE) / BigInteger.TWO) % Q
            if(euler == BigInteger(-1) % Q)
                z = i
            break
        }

        var M = S

        var c = z.pow(q) % Q
        var t = value.pow(q) % Q
        var R = value.pow((q + BigInteger.ONE)/ BigInteger.TWO) % Q

        while (true){
            if(t == BigInteger.ZERO) return Fq(Q, BigInteger.ZERO)
            if(t == BigInteger.ONE) return Fq(Q, R)
            var i = BigInteger.ZERO
            var f = t
            while (f != BigInteger.ONE){
                f = f.pow(BigInteger.TWO) % Q
                i += BigInteger.ONE
            }
            val exp1 =  BigInteger.TWO.pow(M - i - BigInteger.ONE) % Q
            var b = c.pow(exp1) % Q

            M = i
            c = b.pow(BigInteger.TWO) % Q
            t = (t * c) % Q
            R = (R * b) % Q
        }
    }

    override fun zero(Q: BigInteger): Fq {
        return Fq(Q, BigInteger.ZERO)
    }

    override fun one(Q: BigInteger): Fq {
        return Fq(Q, BigInteger.ONE)
    }

    override fun fromFq(Q: BigInteger, fq: Fq): Fq {
        return fq
    }
}
