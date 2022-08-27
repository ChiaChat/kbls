package com.chiachat.kbls.bls.util

import com.chiachat.kbls.bech32.KHex
import com.chiachat.kbls.bls.constants.BLS12381.defaultEc
import com.chiachat.kbls.bls.keys.PrivateKey
import com.chiachat.kbls.bls.util.Hkdf.extractExpand

object HDKeys {
    fun keyGen(seed: UByteArray): PrivateKey {
        val length = 48
        val okm = extractExpand(
            length,
            seed + 0.toUByte(),
            "BLS-SIG-KEYGEN-SALT-".encodeToByteArray().toUByteArray(),
            listOf(0, length).map { it.toUByte() }.toUByteArray()
        )
        return PrivateKey(KHex(okm).bigInt.mod(defaultEc.n))
    }
}
