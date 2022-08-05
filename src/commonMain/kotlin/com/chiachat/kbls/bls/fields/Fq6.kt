package com.chiachat.kbls.bls.fields

import com.ionspin.kotlin.bignum.integer.BigInteger

class Fq6(
    override val Q: BigInteger,
    x: Fq2,
    y: Fq2,
    z: Fq2
): FieldExt<Fq2>(Q, listOf(x, y, z)) {

    override val extension: Int = 6


}