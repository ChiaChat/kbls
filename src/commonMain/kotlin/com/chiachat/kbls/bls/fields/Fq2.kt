package com.chiachat.kbls.bls.fields

import com.chiachat.kbls.bls.util.N1
import com.chiachat.kbls.bls.util.ONE
import com.chiachat.kbls.bls.util.TWO
import com.ionspin.kotlin.bignum.integer.BigInteger

class Fq2(
    override val Q: BigInteger,
    x: Fq,
    y: Fq
) : FieldExt(listOf(x, y)) {
    override val extension: Int = 2
    override val embedding = 2
    override fun construct(Q: BigInteger, elements: List<Field>): FieldExt {
        return Fq2(Q, elements[0] as Fq, elements[1] as Fq)
    }

    override var root: Field = Fq(Q, N1)

    override fun inverse(): FieldExt {
        val (a, b) = this.elements.map { it as Fq }
        val factor: Field = (a * a + b * b).inverse()
        return Fq2(Q, a * factor, -b * factor)
    }

    fun mulByNonResidue(): Fq2 {
        val (a, b) = this.elements.map { it as Fq }
        return Fq2(Q, a - b, a + b)
    }

    fun modSqrt(): Fq2 {
        val a0 = this.elements[0] as Fq
        val a1 = this.elements[1] as Fq
        if (a1 == this.basefield.one(this.Q)) {
            return this.fromFq(this.Q, a0.modSqrt()) as Fq2
        }

        var alpha = a0.pow(TWO) + a1.pow(TWO)
        var gamma = alpha.pow((this.Q - ONE) / TWO)
        if (Fq(this.Q, BigInteger(-1)) == gamma) {
            throw ValueException("No sqrt exists.")
        }
        alpha = alpha.modSqrt()
        var delta = (a0 + alpha) * Fq(this.Q, TWO).inverse()
        gamma = delta.pow((this.Q - 1) / 2)
        if (gamma == Fq(this.Q, BigInteger(-1))) delta = (a0 - alpha) * (Fq(this.Q, TWO).inverse())
        val x0 = delta.modSqrt()
        val x1 = a1 * ((Fq(this.Q, TWO) * x0).inverse())
        return Fq2(this.Q, x0, x1)
    }

    override fun nil(): Field = nil

    companion object {
        val nil = Fq2(ONE, Fq.nil, Fq.nil)
    }
}
