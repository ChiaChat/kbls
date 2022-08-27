package com.chiachat.kbls.bls.util

import com.chiachat.kbls.bls.constants.BLS12381.defaultEcTwist
import com.chiachat.kbls.bls.constants.BLS12381.hEff
import com.chiachat.kbls.bls.constants.BLS12381.q
import com.chiachat.kbls.bls.constants.Iso.xden
import com.chiachat.kbls.bls.constants.Iso.xnum
import com.chiachat.kbls.bls.constants.Iso.yden
import com.chiachat.kbls.bls.constants.Iso.ynum
import com.chiachat.kbls.bls.constants.OpSwuG2Constants.Ell2p_a
import com.chiachat.kbls.bls.constants.OpSwuG2Constants.Ell2p_b
import com.chiachat.kbls.bls.constants.OpSwuG2Constants.etas
import com.chiachat.kbls.bls.constants.OpSwuG2Constants.xi_2
import com.chiachat.kbls.bls.constants.RootsOfUnity.rootsOfUnity
import com.chiachat.kbls.bls.ec.JacobianPoint
import com.chiachat.kbls.bls.fields.Fq
import com.chiachat.kbls.bls.fields.Fq2
import com.chiachat.kbls.bls.util.EcUtil.evalIso
import com.chiachat.kbls.bls.util.HashToField.Hp2
import com.ionspin.kotlin.bignum.integer.BigInteger

object OpSwuG2 {

    fun sgn0(x: Fq2): BigInteger {
        val sign0 = (x.elements[0] as Fq).value.mod(TWO) == ONE
        val zero0 = (x.elements[0] as Fq).value == ZERO
        val sign1 = (x.elements[1] as Fq).value.mod(TWO) == ONE
        return if (sign0 || (zero0 && sign1)) ONE else ZERO
    }

    fun osswu2Help(t: Fq2): JacobianPoint {
        val numDenCommon = xi_2
            .pow(2)
            .times(t.pow(4))
            .plus(xi_2.times(t.pow(2)))
        val x0_num = Ell2p_b.times(numDenCommon.plus(Fq(q, ONE)))
        var x0_den = Ell2p_a.unaryMinus().times(numDenCommon)
        x0_den = if (x0_den.equals(ZERO)) Ell2p_a.times(xi_2) else x0_den
        val gx0_den = x0_den.pow(3)
        val gx0_num = Ell2p_b.times(gx0_den)
            .plus(Ell2p_a.times(x0_num).times(x0_den.pow(2)))
            .plus(x0_num.pow(3))
        var temp1 = gx0_den.pow(7)
        val temp2 = gx0_num.times(temp1)
        temp1 = temp1.times(temp2).times(gx0_den)
        // TODO: check order of ops here
        var sqrtCandidate = temp2.times(temp1.pow((q.pow(2) - 9) / 16))
        for (root in rootsOfUnity) {
            var y0 = sqrtCandidate.times(root) as Fq2
            if (y0.pow(2).times(gx0_den).equals(gx0_num)) {
                if (sgn0(y0) != sgn0(t)) y0 = y0.unaryMinus() as Fq2
                return JacobianPoint(
                    x0_num.times(x0_den),
                    y0.times(x0_den.pow(3)),
                    x0_den,
                    false,
                    defaultEcTwist
                )
            }
        }
        val x1_num = xi_2.times(t.pow(2)).times(x0_num)
        val x1_den = x0_den
        val gx1_num = xi_2.pow(3).times(t.pow(6)).times(gx0_num)
        val gx1_den = gx0_den
        sqrtCandidate = sqrtCandidate.times(t.pow(3))
        for (eta in etas) {
            var y1 = eta.times(sqrtCandidate) as Fq2
            if (y1.pow(2).times(gx1_den).equals(gx1_num)) {
                if (sgn0(y1) !== sgn0(t)) y1 = y1.unaryMinus() as Fq2
                return JacobianPoint(
                    x1_num.times(x1_den),
                    y1.times(x1_den.pow(3)),
                    x1_den,
                    false,
                    defaultEcTwist
                )
            }
        }
        throw Exception("Bad osswu2Help")
    }

    fun iso3(P: JacobianPoint): JacobianPoint {
        return evalIso(P, arrayOf(xnum, xden, ynum, yden), defaultEcTwist)
    }

    fun optSwu2Map(t: Fq2, t2: Fq2?): JacobianPoint {
        var Pp = iso3(osswu2Help(t))
        if(t2 != null){
            val Pp2 = iso3(osswu2Help(t2))
            Pp = Pp.plus(Pp2)
        }
        return Pp.times(hEff)
    }

    fun g2Map(alpha: UByteArray, dst: UByteArray): JacobianPoint{
        val elements = Hp2(alpha, 2,dst).map { hh ->
            val items = hh.map { Fq(q, it) }
            Fq2(q, items[0], items[1])
        }
        return optSwu2Map(elements[0], elements[1])
    }
}
