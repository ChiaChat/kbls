package com.chiachat.kbls.bls.fields

import com.ionspin.kotlin.bignum.integer.BigInteger

class Fq12(override val Q: BigInteger, x: Fq6, y: Fq6) : FieldExt(listOf(x, y)) {
    override val extension: Int = 12
    override val embedding: Int = 2
    override var root: Field = Fq6(Q, Fq2.nil.zero(Q) as Fq2, Fq2.nil.one(Q) as Fq2, Fq2.nil.zero(Q) as Fq2)

    override fun construct(Q: BigInteger, elements: List<Field>): FieldExt {
        val els = elements.map { it as Fq6 }
        return Fq12(Q, els[0], els[1])
    }

    override fun inverse(): Field {
        val (a, b) = this.elements.map { it as Fq6 };
        val factor = (a * a)
            .minus(((b * b) as Fq6).mulByNonResidue())
            .inverse()
        return Fq12(
            this.Q,
            (a * factor) as Fq6,
            (b.unaryMinus().times(factor)) as Fq6
        )
    }

    companion object {
        val nil = Fq12(ONE, Fq6.nil, Fq6.nil)
    }
}