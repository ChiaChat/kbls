package com.chiachat.kbls.bls.keys

import com.chiachat.kbls.bech32.KHex
import com.chiachat.kbls.bls.constants.BLS12381.defaultEc
import com.chiachat.kbls.bls.ec.JacobianPoint
import com.chiachat.kbls.bls.util.Hkdf.extractExpand
import com.ionspin.kotlin.bignum.integer.BigInteger

const val SIZE = 32

class PrivateKey(val value: BigInteger) {

    val hex = KHex(value)

    fun getG1(): JacobianPoint {
        return JacobianPoint.generateG1().times(this.value)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is PrivateKey) return false
        return this.value == other.value
    }

    override fun toString(): String {
        return "PrivateKey($hex)"
    }

    fun toBytes(): UByteArray = hex.byteArray

    companion object {
        fun fromBytes(bytes: UByteArray): PrivateKey {
            return PrivateKey(KHex(bytes).bigInt.mod(defaultEc.n))
        }

        fun fromHex(hex: KHex) = fromBytes(hex.byteArray)

        fun fromSeed(seed: UByteArray): PrivateKey {
            val length = 48
            val okm = extractExpand(
                length,
                seed + 0.toUByte(),
                "BLS-SIG-KEYGEN-SALT-".encodeToByteArray().toUByteArray(),
                listOf(0, length).map { it.toUByte() }.toUByteArray()
            )
            return PrivateKey(KHex(okm).bigInt.mod(defaultEc.n))
        }

        fun aggregate(privateKeys: List<PrivateKey>): PrivateKey {
            val agg = privateKeys.map { it.value }.reduce { pks, pk -> pks + pk }.mod(defaultEc.n)
            return PrivateKey(agg)
        }
    }
}
