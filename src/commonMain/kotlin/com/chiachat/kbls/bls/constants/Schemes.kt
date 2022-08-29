@file:OptIn(ExperimentalUnsignedTypes::class)

package com.chiachat.kbls.bls.constants

object Schemes {
    val basicSchemeDst = "BLS_SIG_BLS12381G2_XMD:SHA-256_SSWU_RO_NUL_".encodeToByteArray().toUByteArray()
    val augSchemeDst = "BLS_SIG_BLS12381G2_XMD:SHA-256_SSWU_RO_AUG_".encodeToByteArray().toUByteArray()
    val popSchemeDst = "BLS_SIG_BLS12381G2_XMD:SHA-256_SSWU_RO_POP_".encodeToByteArray().toUByteArray()
    val popSchemePopDst = "BLS_POP_BLS12381G2_XMD:SHA-256_SSWU_RO_POP_".encodeToByteArray().toUByteArray()
}
