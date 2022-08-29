@file:OptIn(ExperimentalUnsignedTypes::class)

package com.chiachat.kbls.bls.schemes

import com.chiachat.kbls.bls.constants.Schemes.basicSchemeDst
import com.chiachat.kbls.bls.ec.JacobianPoint
import com.chiachat.kbls.bls.keys.PrivateKey
import com.chiachat.kbls.bls.util.HDKeys
import com.chiachat.kbls.bls.util.HDKeys.deriveChildG1Unhardened
import com.chiachat.kbls.bls.util.Signing.coreAggregateMpl
import com.chiachat.kbls.bls.util.Signing.coreAggregateVerify
import com.chiachat.kbls.bls.util.Signing.coreSignMpl
import com.chiachat.kbls.bls.util.Signing.coreVerifyMpl

object BasicSchemeMPL {
    fun keyGen(seed: UByteArray): PrivateKey = HDKeys.keyGen(seed)

    fun sign(
        privateKey: PrivateKey,
        message: UByteArray
    ): JacobianPoint {
        return coreSignMpl(privateKey, message, basicSchemeDst)
    }

    fun verify(
        publicKey: JacobianPoint,
        message: UByteArray,
        signature: JacobianPoint
    ): Boolean {
        return coreVerifyMpl(publicKey, message, signature, basicSchemeDst)
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
            basicSchemeDst
        )
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
