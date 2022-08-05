package com.chiachat.kbls.bls.fields

import com.ionspin.kotlin.bignum.integer.BigInteger

class Fq2(
    override val Q: BigInteger,
    x: Fq,
    y: Fq
): FieldExtBase<Fq>(Q, listOf(x, y)) {
    override val extension: Int = 2
    override val embedding = 2
    override val root: Fq = Fq(Q, BigInteger(-1))

    override fun inverse(): FieldExtBase<Fq> {
        val a = this.elements[0]
        val b = this.elements[1]
        val factor = (a * a + b * b).inverse()
        return Fq2(Q, a * factor, -b * factor)
    }

    fun mulByNonresidue(): Fq2 {
        val a = this.elements[0]
        val b = this.elements[1]
        return Fq2(Q, a - b, a + b)
    }

    override fun modSqrt(): FieldExtBase<Fq> {
        val a0 = this.elements[0]
        val a1 = this.elements[1]
        if (a1 == this.basefield.one(this.Q))
            return this.fromFq(this.Q, a0.modSqrt())
        
        var alpha = a0.pow(TWO) + (a1.pow(TWO))
        var gamma = alpha.pow((this.Q - ONE) / TWO);
        if (Fq(this.Q, BigInteger(-1)) == gamma)
            throw ValueException("No sqrt exists.");
        alpha = alpha.modSqrt();
        var delta = a0 + (alpha) * (Fq(this.Q, TWO).inverse())
        gamma = delta.pow((this.Q - ONE) / TWO)
        if (gamma.equals(Fq(this.Q, BigInteger(-1))))
        delta = (a0 - alpha) * (Fq(this.Q, TWO).inverse())
        val x0 = delta.modSqrt();
        val x1 = a1 * (Fq(this.Q, TWO) * (x0).inverse())
        return Fq2(this.Q, x0, x1)
    }
}