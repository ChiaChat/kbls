package com.chiachat.kbls.bls.constants

import com.chiachat.kbls.bech32.toHex
import com.chiachat.kbls.bls.constants.BLS12381.q
import com.chiachat.kbls.bls.fields.Fq
import com.chiachat.kbls.bls.fields.Fq2
import com.chiachat.kbls.bls.util.ONE
import com.chiachat.kbls.bls.util.ZERO

object RootsOfUnity {

    val rv1 =
        "0x6af0e0437ff400b6831e36d6bd17ffe48395dabc2d3435e77f76e17009241c5ee67992f72ec05f4c81084fbede3cc09".toHex().bigInt

    val rootsOfUnity = arrayOf(
        Fq2(q, Fq(q, ONE), Fq(q, ZERO)),
        Fq2(q, Fq(q, ZERO), Fq(q, ONE)),
        Fq2(q, Fq(q, rv1), Fq(q, rv1)),
        Fq2(q, Fq(q, rv1), Fq(q, q - rv1))
    )
}
