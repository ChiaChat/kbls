package com.chiachat.kbls.bls.ec

import com.chiachat.kbls.bls.fields.Fq
import com.chiachat.kbls.bls.fields.Fq2
import com.ionspin.kotlin.bignum.integer.BigInteger

class ECTwist(
    q: BigInteger,
    val aTwist: Fq2,
    val bTwist: Fq2,
    gx: Fq,
    gy: Fq,
    g2x: Fq2,
    g2y: Fq2,
    n: BigInteger,
    hEff: BigInteger,
    x: BigInteger,
    k: BigInteger,
    sqrtN3: BigInteger,
    sqrtN3m1o2: BigInteger
) : EC(
    q, aTwist, bTwist, gx, gy, g2x, g2y, n, hEff, x, k, sqrtN3, sqrtN3m1o2
)
