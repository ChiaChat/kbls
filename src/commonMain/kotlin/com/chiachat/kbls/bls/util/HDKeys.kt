package com.chiachat.kbls.bls.util

import com.chiachat.kbls.bech32.KHex
import com.chiachat.kbls.bech32.toByte
import com.chiachat.kbls.bls.constants.BLS12381.defaultEc
import com.chiachat.kbls.bls.ec.JacobianPoint
import com.chiachat.kbls.bls.keys.PrivateKey
import com.chiachat.kbls.bls.util.Hkdf.extractExpand
import com.soywiz.krypto.sha256

@OptIn(ExperimentalUnsignedTypes::class)
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

    fun ikmToLamportSk(ikm: UByteArray, salt: UByteArray): UByteArray {
        return extractExpand(32 * 255, ikm, salt, UByteArray(0))
    }

    fun parentSkToLamportPk(
        parentSk: PrivateKey,
        index: Int
    ): UByteArray {
        val salt = index.toBytes(4)
        val ikm = parentSk.toBytes()
        val notIkm = ikm.map { e -> e.xor("0xff".toByte()) }.toUByteArray()
        val lamport0 = ikmToLamportSk(ikm, salt)
        val lamport1 = ikmToLamportSk(notIkm, salt)
        val lamportPk: MutableList<Int> = mutableListOf()
        for (i in 0 until 255) {
            val hash = lamport0.slice(i * 32..(i + 1) * 32).toUByteArray().toByteArray().sha256().bytes.toUByteArray()
                .map { it.toInt() }
            lamportPk.addAll(hash)
        }
        for (i in 0 until 255) {
            val hash = lamport1.slice(i * 32..(i + 1) * 32).toUByteArray().toByteArray().sha256().bytes.toUByteArray()
                .map { it.toInt() }
            lamportPk.addAll(hash)
        }
        return lamportPk.map { it.toByte() }.toByteArray().sha256().bytes.toUByteArray()
    }

    fun deriveChildSk(parentSk: PrivateKey, index: Int): PrivateKey {
        return keyGen(parentSkToLamportPk(parentSk, index))
    }

    fun deriveChildSkUnhardened(
        parentSk: PrivateKey,
        index: Int
    ): PrivateKey {
        val hash = (parentSk.getG1().toBytes() + index.toBytes(4)).sha256()
        return PrivateKey.aggregate(listOf(PrivateKey.fromBytes(hash), parentSk))
    }

    fun deriveChildG1Unhardened(
        parentPk: JacobianPoint,
        index: Int
    ): JacobianPoint {
        val hash = (parentPk.toBytes() + index.toBytes(4)).sha256()
        return parentPk.plus(
            JacobianPoint.generateG1().times(PrivateKey.fromBytes(hash).value)
        )
    }

    fun deriveChildG2Unhardened(
        parentPk: JacobianPoint,
        index: Int
    ): JacobianPoint {
        val hash = (parentPk.toBytes() + index.toBytes(4)).sha256()
        return parentPk.plus(
            JacobianPoint.generateG2().times(PrivateKey.fromBytes(hash).value)
        )
    }
}
