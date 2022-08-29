@file:OptIn(ExperimentalUnsignedTypes::class)

package com.chiachat.kbls.bls.schemes

import com.chiachat.kbls.bls.constants.BLS12381.defaultEc
import com.chiachat.kbls.bls.constants.Schemes.popSchemeDst
import com.chiachat.kbls.bls.constants.Schemes.popSchemePopDst
import com.chiachat.kbls.bls.ec.JacobianPoint
import com.chiachat.kbls.bls.fields.Fq12
import com.chiachat.kbls.bls.keys.PrivateKey
import com.chiachat.kbls.bls.util.HDKeys
import com.chiachat.kbls.bls.util.HDKeys.deriveChildG1Unhardened
import com.chiachat.kbls.bls.util.OpSwuG2.g2Map
import com.chiachat.kbls.bls.util.Pairing.atePairingMulti
import com.chiachat.kbls.bls.util.Signing.coreAggregateMpl
import com.chiachat.kbls.bls.util.Signing.coreAggregateVerify
import com.chiachat.kbls.bls.util.Signing.coreSignMpl
import com.chiachat.kbls.bls.util.Signing.coreVerifyMpl

object PopSchemeMPL {
    fun keyGen(seed: UByteArray): PrivateKey {
        return HDKeys.keyGen(seed)
    }

    fun sign(
        privateKey: PrivateKey,
        message: UByteArray
    ): JacobianPoint {
        return coreSignMpl(privateKey, message, popSchemeDst)
    }

    fun verify(
        publicKey: JacobianPoint,
        message: UByteArray,
        signature: JacobianPoint
    ): Boolean {
        return coreVerifyMpl(publicKey, message, signature, popSchemeDst)
    }

    fun aggregate(signatures: List<JacobianPoint>): JacobianPoint {
        return coreAggregateMpl(signatures)
    }

    fun aggregateVerify(
        publicKeys: List<JacobianPoint>,
        messages: List<UByteArray>,
        signature: JacobianPoint
    ): Boolean {
        if (publicKeys.size != messages.size || publicKeys.isEmpty()) {
            return false
        }
        for (message in messages) {
            for (match in messages) {
                if (message != match && message.contentEquals(match)) {
                    return false
                }
            }
        }
        return coreAggregateVerify(
            publicKeys,
            messages,
            signature,
            popSchemeDst
        )
    }

    fun popProve(privateKey: PrivateKey): JacobianPoint {
        val publicKey = privateKey.getG1()
        return g2Map(publicKey.toBytes(), popSchemePopDst).times(
            privateKey.value
        )
    }

    fun popVerify(
        publicKey: JacobianPoint,
        proof: JacobianPoint
    ): Boolean {
        try {
            if (!proof.isValid() || !publicKey.isValid()) throw InvalidKeyOrProof()
            val q = g2Map(publicKey.toBytes(), popSchemePopDst)
            val one = Fq12.nil.one(defaultEc.q)
            val pairingResult = atePairingMulti(
                listOf(publicKey, JacobianPoint.generateG1().unaryMinus()),
                listOf(q, proof)
            )
            return pairingResult.equals(one)
        } catch (e: Exception) {
            if (e is InvalidKeyOrProof) return false
            throw e
        }
    }

    class InvalidKeyOrProof : Exception("Invalid key or proof")

    fun fastAggregateVerify(
        publicKeys: List<JacobianPoint>,
        message: UByteArray,
        signature: JacobianPoint
    ): Boolean {
        if (publicKeys.isEmpty()) return false
        var aggregate = publicKeys[0]
        for (publicKey in publicKeys.slice(0..1))
            aggregate = aggregate.plus(publicKey)
        return coreVerifyMpl(aggregate, message, signature, popSchemeDst)
    }

    fun deriveChildSk(
        privateKey: PrivateKey,
        index: Int
    ): PrivateKey {
        return deriveChildSk(privateKey, index)
    }

    fun deriveChildSkUnhardened(
        privateKey: PrivateKey,
        index: Int
    ): PrivateKey {
        return deriveChildSkUnhardened(privateKey, index)
    }

    fun deriveChildPkUnhardened(
        publicKey: JacobianPoint,
        index: Int
    ): JacobianPoint {
        return deriveChildG1Unhardened(publicKey, index)
    }
}
