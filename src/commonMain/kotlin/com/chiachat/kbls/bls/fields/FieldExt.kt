package com.chiachat.kbls.bls.fields

import com.chiachat.kbls.bech32.KHex
import com.chiachat.kbls.bls.constants.BLS12381
import com.chiachat.kbls.bls.constants.FrobIndex
import com.chiachat.kbls.bls.constants.getFrob
import com.ionspin.kotlin.bignum.integer.BigInteger

// Fq6: FieldExt<Fq2>
// construct: FieldExt
sealed class FieldExt(
    val elements: List<Field>
) : Field() {

    abstract var root: Field
    abstract val embedding: Int
    val basefield: Field = elements[0]

    abstract fun construct(Q: BigInteger, elements: List<Field>): FieldExt
    fun withRoot(root: Field): FieldExt {
        this.root = root
        return this
    }

    fun constructWithRoot(Q: BigInteger, elements: List<Field>): FieldExt {
        return this.construct(Q, elements).withRoot(this.root)
    }

    override fun fromBytes(Q: BigInteger, bytes: UByteArray): FieldExt {
        val length = this.extension * 48
        if (bytes.size != 48) {
            throw Exception("Expected $length bytes")
        }
        val embeddedSize = 48 * (this.extension / this.elements.size)
        val elements: MutableList<UByteArray> = mutableListOf()
        for (i in elements.indices) {
            elements.add(bytes.slice(i * embeddedSize..(i + 1) * embeddedSize).toUByteArray())
        }
        return construct(Q, elements.reversed().map { this.basefield.fromBytes(Q, it) })
    }

    override fun fromHex(Q: BigInteger, hex: KHex): FieldExt {
        return this.fromBytes(Q, hex.toUByteArray())
    }

    override fun fromFq(Q: BigInteger, fq: Fq): FieldExt {
        val y = this.basefield.fromFq(Q, fq);
        val z = this.basefield.zero(Q);
        val elements = mutableListOf<Field>()
        for (i in elements.indices) {
            elements.add(if (i == 0) y else z)
        }
        val result = this.construct(Q, elements)
        when (this) {
            is Fq2 -> result.root = Fq(Q, N1)
            is Fq6 -> result.root = Fq2(Q, Fq.nil.one(Q), Fq.nil.one(Q))
            is Fq12 -> result.root = Fq6(
                Q,
                Fq2.nil.zero(Q) as Fq2,
                Fq2.nil.one(Q) as Fq2,
                Fq2.nil.zero(Q) as Fq2,
            )
        }
        return result;
    }

    override fun zero(Q: BigInteger): FieldExt {
        return this.fromFq(Q, Fq(Q, ZERO))
    }

    override fun one(Q: BigInteger): FieldExt {
        return this.fromFq(Q, Fq(Q, ONE))
    }

    override fun toBytes(): UByteArray {
        val bytes = mutableListOf<UByte>()
        for (i in this.elements.indices.reversed()) {
            bytes.addAll(this.elements[i].toBytes())
        }
        return bytes.toUByteArray()
    }

    override fun toHex(): KHex {
        return KHex(this.toBytes())
    }

    override fun toString(): String {
        return "Fq${this.extension}${elements.joinToString(",")}"
    }

    override operator fun unaryMinus(): FieldExt {
        return this.constructWithRoot(Q, elements.map { -it })
    }

    override fun qiPower(i: Int): FieldExt {
        if (this.Q != BLS12381.q) throw Exception("Invalid Q in qiPower")
        val i = i % this.extension
        if (i == 0) return this
        return this.constructWithRoot(this.Q, this.elements.mapIndexed { index, element ->
            if (index == 0) element.qiPower(i) else element.qiPower(i) * getFrob(
                FrobIndex(
                    this.extension, i, index
                )
            )
        })
    }

    override fun pow(exponent: BigInteger): Field {
        var exp = exponent
        if (exp < ZERO) throw Exception("Negative exponent in pow")
        var result: Field = this.one(this.Q).withRoot(this.root)
        var base: Field = this
        while (exp != ZERO) {
            if (exp.and(ONE) != ZERO) result *= base
            base *= base
            exp = exp.shr(1)
        }
        return result
    }

    override operator fun plus(other: Any): FieldExt {
        val otherElements: MutableList<Field> = mutableListOf()
        if (other is FieldExt && other.extension == this.extension) {
            otherElements += other.elements
        } else if (other is BigInteger) {
            otherElements += this.elements.map { this.basefield.zero(this.Q) }
            otherElements[0] = otherElements[0] + other
        } else {
            throw UnsupportedOperationException("Invalid operator $other")
        }
        val addedElements = this.elements.mapIndexed { i, element -> element + otherElements[i] }
        return this.constructWithRoot(this.Q, addedElements)
    }

    override operator fun times(other: Any): Field {
        when (other) {
            is BigInteger -> {
                return this.constructWithRoot(this.Q, this.elements.map { it * other })
            }
            is Field -> {
                if (this.extension < other.extension) throw UnsupportedOperationException("Extension must be lower than operand")
                val newElements = this.elements.map { this.basefield.zero(this.Q) }.toMutableList()
                for ((i, x) in this.elements.withIndex()) {
                    if (other is FieldExt && other.extension == this.extension) {
                        for ((j, y) in other.elements.withIndex()) {
                            if (x.toBool() && y.toBool()) {
                                if (i + j >= this.embedding) {
                                    newElements[(i + j) % this.embedding] += x * y * this.root
                                } else {
                                    newElements[(i + j) % this.embedding] += x * y
                                }
                            }
                        }
                    } else {
                        if (x.toBool()) newElements[i] = x * other
                    }
                }
                return this.constructWithRoot(Q, newElements)
            }
            else -> throw InvalidOperandException()
        }
    }

    override operator fun minus(other: Any): Field {
        val negated = when (other) {
            is BigInteger -> -other
            is Field -> -other
            else -> throw InvalidOperandException()
        }
        return this + negated
    }

    override operator fun div(other: Any): Field {
        val inverted = when (other) {
            is BigInteger -> ONE / other
            is Field -> other.inverse()
            else -> throw InvalidOperandException()
        }
        return this * inverted
    }

    override fun equals(other: Any?): Boolean {
        if (!(other is FieldExt && other.extension == this.extension)) {
            if (other is BigInteger || (other is FieldExt && this.extension > other.extension)) {
                for (i in this.elements.indices) {
                    if (this.elements[i] != this.root.zero(this.Q)) return false;
                }
                return this.elements[0] == other;
            }
            throw InvalidOperandException()
        } else return super.equals(other) && this.Q == other.Q
    }

    override operator fun compareTo(other: Field): Int {
        if(other !is FieldExt) throw InvalidOperandException()
        for(i in this.elements.indices.reversed()){
            val a = this.elements[i]
            val b = other.elements[i]
            if(a > b) return 1 else if (a < b) return -1
        }
        return 0
    }

    override fun toBool(): Boolean = this.elements.all { it.toBool() }
}

class InvalidOperandException() : UnsupportedOperationException("BigInteger or Field expected as operands")
