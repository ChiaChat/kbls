package com.chiachat.kbls.bls.ec

import com.chiachat.kbls.bls.fields.Fq
import com.chiachat.kbls.bls.fields.Fq2
import com.ionspin.kotlin.bignum.integer.BigInteger

data class ECTwist(
    val q: BigInteger,
    val aTwist: Fq2,
    val bTwist: Fq2,
    val gx: Fq,
    val gy: Fq,
    val g2x: Fq2,
    val g2y: Fq2,
    val n: BigInteger,
    val hEff: BigInteger,
    val x: BigInteger,
    val k: BigInteger,
    val sqrtN3: BigInteger,
    val sqrtN3m1o2: BigInteger
)
