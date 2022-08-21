package com.chiachat.kbls.bls.fields

import com.ionspin.kotlin.bignum.integer.BigInteger

class Fq2(
    override val Q: BigInteger,
    x: Fq,
    y: Fq
): FieldExt<Fq>(Q, listOf(x, y)) {
    override val extension: Int = 2
    override val embedding = 2
    override var root  = Fq(Q, BigInteger(-1))

    override fun inverse(): FieldExt {
        val a = this.elements[0] as Fq
        val b = this.elements[1] as Fq
        val factor: Field = (a * a + b * b).inverse()
        return Fq2(Q, a * factor, -b * factor)
    }

    fun mulByNonresidue(): Fq2 {
        val a = this.elements[0] as Fq
        val b = this.elements[1] as Fq
        return Fq2(Q, a - b, a + b)
    }

    override fun modSqrt(): FieldExt {
        val a0 = this.elements[0] as Fq
        val a1 = this.elements[1] as Fq
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

    companion object {
        val nil = Fq2(ONE, Fq.nil, Fq.nil)
    }
}