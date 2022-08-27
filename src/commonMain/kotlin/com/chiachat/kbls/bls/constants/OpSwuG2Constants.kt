package com.chiachat.kbls.bls.constants

import com.chiachat.kbls.bech32.toHex
import com.chiachat.kbls.bls.constants.BLS12381.q
import com.chiachat.kbls.bls.fields.Fq
import com.chiachat.kbls.bls.fields.Fq2
import com.ionspin.kotlin.bignum.integer.toBigInteger

object OpSwuG2Constants {
    val xi_2 = Fq2(q, Fq(q, (-2).toBigInteger()), Fq(q, (-1).toBigInteger()))
    val Ell2p_a = Fq2(q, Fq(q, 0.toBigInteger()), Fq(q, 240.toBigInteger()))
    val Ell2p_b = Fq2(q, Fq(q, 1012.toBigInteger()), Fq(q, 1012.toBigInteger()))
    val ev1 =
        "0x699be3b8c6870965e5bf892ad5d2cc7b0e85a117402dfd83b7f4a947e02d978498255a2aaec0ac627b5afbdf1bf1c90n".toHex().bigInt
    val ev2 =
        "0x8157cd83046453f5dd0972b6e3949e4288020b5b8a9cc99ca07e27089a2ce2436d965026adad3ef7baba37f2183e9b5".toHex().bigInt
    val ev3 =
        "0xab1c2ffdd6c253ca155231eb3e71ba044fd562f6f72bc5bad5ec46a0b7a3b0247cf08ce6c6317f40edbc653a72dee17".toHex().bigInt
    val ev4 =
        "0xaa404866706722864480885d68ad0ccac1967c7544b447873cc37e0181271e006df72162a3d3e0287bf597fbf7f8fc1n".toHex().bigInt
    val etas = arrayOf(
        Fq2(q, Fq(q, ev1), Fq(q, ev2)),
        Fq2(q, Fq(q, q - ev2), Fq(q, ev1)),
        Fq2(q, Fq(q, ev3), Fq(q, ev4)),
        Fq2(q, Fq(q, q - ev4), Fq(q, ev3))
    )
}
