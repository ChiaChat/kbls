package com.chiachat.kbls.bls.util

import com.chiachat.kbls.bls.constants.BLS12381.defaultEc
import com.chiachat.kbls.bls.ec.JacobianPoint
import com.chiachat.kbls.bls.fields.Fq12
import com.chiachat.kbls.bls.keys.PrivateKey
import com.chiachat.kbls.bls.util.OpSwuG2.g2Map
import com.chiachat.kbls.bls.util.Pairing.atePairingMulti

object Signing {
    fun coreSignMpl(
        sk: PrivateKey,
        message: UByteArray,
        dst: UByteArray
    ): JacobianPoint {
        return g2Map(message, dst).times(sk.value)
    }

    fun coreVerifyMpl(
        pk: JacobianPoint,
        message: UByteArray,
        signature: JacobianPoint,
        dst: UByteArray
    ): Boolean {
        if (!signature.isValid() || !pk.isValid()) return false
        val q = g2Map(message, dst)
        val one = Fq12.nil.one(defaultEc.q)
        val pairingResult = atePairingMulti( listOf(pk, JacobianPoint.generateG1().unaryMinus()), listOf(q, signature) )
        return pairingResult == one
    }
    fun coreAggregateMpl(signatures: List<JacobianPoint>): JacobianPoint {
        if (signatures.size == 0) throw Exception("Must aggregate at least 1 signature.")
        var aggregate = signatures[0]
        if (!aggregate.isValid()) throw Exception("Aggregate is not valid")
        for (signature in signatures.slice(1 until signatures.size)) {
            if (!signature.isValid()) throw Exception("Signature is not valid")
            aggregate = aggregate.plus(signature)
        }
        return aggregate
    }

    fun coreAggregateVerify(
        pks: List<JacobianPoint>,
        ms: List<UByteArray>,
        signature: JacobianPoint,
        dst: UByteArray
    ): Boolean {
        if (pks.size != ms.size || pks.isEmpty()) return false
        if (!signature.isValid()) return false
        val qs = mutableListOf(signature)
        val ps = mutableListOf(JacobianPoint.generateG1().unaryMinus())
        for (i in 0 until pks.size) {
            if (!pks[i].isValid()) return false
            qs.add(g2Map(ms[i], dst))
            ps.add(pks[i])
        }
        return Fq12.nil.one(defaultEc.q) == atePairingMulti(ps, qs)
    }
}
