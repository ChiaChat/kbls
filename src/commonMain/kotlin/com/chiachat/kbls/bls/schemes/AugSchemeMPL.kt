package com.chiachat.kbls.bls.schemes

import com.chiachat.kbls.bls.constants.Schemes.augSchemeDst
import com.chiachat.kbls.bls.ec.JacobianPoint
import com.chiachat.kbls.bls.util.HDKeys
import com.chiachat.kbls.bls.keys.PrivateKey
import com.chiachat.kbls.bls.util.HDKeys.deriveChildG1Unhardened
import com.chiachat.kbls.bls.util.Signing.coreAggregateMpl
import com.chiachat.kbls.bls.util.Signing.coreAggregateVerify
import com.chiachat.kbls.bls.util.Signing.coreSignMpl
import com.chiachat.kbls.bls.util.Signing.coreVerifyMpl

object AugSchemeMPL {
    fun keyGen(seed: UByteArray): PrivateKey {
        return HDKeys.keyGen(seed);
    }

    fun sign(
        privateKey: PrivateKey,
        message: UByteArray
    ): JacobianPoint {
        val publicKey = privateKey.getG1();
        return coreSignMpl(
            privateKey, publicKey.toBytes() + message, augSchemeDst
        )
    }

    fun verify(
        publicKey: JacobianPoint,
        message: UByteArray,
        signature: JacobianPoint
    ): Boolean {
        return coreVerifyMpl(
            publicKey,
            publicKey.toBytes() + message,
            signature,
            augSchemeDst
        );
    }

    fun aggregate(signatures: List<JacobianPoint>): JacobianPoint {
        return coreAggregateMpl(signatures);
    }

    fun aggregateVerify(
        publicKeys: List<JacobianPoint>,
        messages: List<UByteArray>,
        signature: JacobianPoint
    ): Boolean {
        if (publicKeys.size !== messages.size || publicKeys.isEmpty())
            return false;
        val mPrimes: MutableList<UByteArray> = mutableListOf();
        for(i in 0 until publicKeys.size) {
            mPrimes.add(
                publicKeys[i].toBytes() + messages[i]
            )
        }
        return coreAggregateVerify(
            publicKeys,
            mPrimes,
            signature,
            augSchemeDst
        );
    }

    fun deriveChildSk(
        privateKey: PrivateKey,
        index: Int
    ): PrivateKey {
        return deriveChildSk(privateKey, index);
    }

    fun deriveChildSkUnhardened(
        privateKey: PrivateKey,
        index: Int
    ): PrivateKey {
        return deriveChildSkUnhardened(privateKey, index);
    }

    fun deriveChildPkUnhardened(
        publicKey: JacobianPoint,
        index: Int
    ): JacobianPoint {
        return deriveChildG1Unhardened(publicKey, index);
    }
}