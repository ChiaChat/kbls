package com.chiachat.kbls.bls.fields

import com.ionspin.kotlin.bignum.integer.BigInteger

class Fq6(
    override val Q: BigInteger,
    x: Fq2,
    y: Fq2,
    z: Fq2
) : FieldExt(listOf(x, y, z)) {

    override var root: Field = Fq2(Q, Fq.nil.one(Q), Fq.nil.one(Q))
    override val extension: Int = 6
    override val embedding: Int = 3

    override fun construct(Q: BigInteger, elements: List<Field>): FieldExt {
        val els = elements.map { it as Fq2 }
        return Fq6(Q, els[0], els[1], els[2])
    }

    override fun inverse(): Field {
        val (a, b, c) = this.elements.map { it as Fq2 }
        val g0 = a * (a) - (b * (c.mulByNonResidue()))
        val g1 = ((c * c) as Fq2).mulByNonResidue() - (a * (b))
        val g2 = b * (b) - (a * (c))
        val factor = g0 * (a) + (((g1 * (c) + (g2 * b)) as Fq2).mulByNonResidue()).inverse();
        return Fq6(
            this.Q,
            (g0 * factor) as Fq2,
            (g1 * factor) as Fq2,
            (g2 * factor) as Fq2
        )
    }

    fun mulByNonResidue(): Fq6 {
        val (a, b, c) = this.elements.map { it as Fq2 }
        return Fq6(this.Q, (c * this.root) as Fq2, a, b);
    }

    companion object {
        val nil = Fq6(ONE, Fq2.nil, Fq2.nil, Fq2.nil)
    }
}