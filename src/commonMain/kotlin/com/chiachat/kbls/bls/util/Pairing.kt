package com.chiachat.kbls.bls.util

import com.chiachat.kbls.bls.constants.BLS12381.defaultEc
import com.chiachat.kbls.bls.ec.AffinePoint
import com.chiachat.kbls.bls.ec.EC
import com.chiachat.kbls.bls.ec.JacobianPoint
import com.chiachat.kbls.bls.fields.Field
import com.chiachat.kbls.bls.fields.Fq
import com.chiachat.kbls.bls.fields.Fq12
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger

object Pairing {
    fun doubleLineEval(
        R: AffinePoint,
        P: AffinePoint,
        ec: EC = defaultEc
    ): Field {
        val R12 = R.untwist();
        val slope = Fq(ec.q, 3.toBigInteger())
            .times(R12.x.pow(2).plus(ec.a))
            .div(R12.y.times(Fq(ec.q, 2.toBigInteger())));
        val v = R12.y.minus(R12.x.times(slope));
        return P.y.minus(P.x.times(slope)).minus(v) as Field;
    }

    fun addLineEval(
        R: AffinePoint,
        Q: AffinePoint,
        P: AffinePoint
    ): Field {
        val R12 = R.untwist();
        val Q12 = Q.untwist();
        if (R12.equals(Q12.unaryMinus())) return P.x.minus(R12.x) as Field;
        val slope = Q12.y.minus(R12.y).div(Q12.x.minus(R12.x));
        val v = Q12.y
            .times(R12.x)
            .minus(R12.y.times(Q12.x))
            .div(R12.x.minus(Q12.x));
        return P.y.minus(P.x.times(slope)).minus(v) as Field;
    }

    fun millerLoop(
        T: BigInteger,
        P: AffinePoint,
        Q: AffinePoint,
        ec: EC = defaultEc
    ): Fq12 {
        var R = Q;
        var f = Fq12.nil.one(ec.q);
        for (i in 1 until T.toUByteArray().size * 8) {
            val lrr = doubleLineEval(R, P, ec);
            f = f.times(f).times(lrr) as Fq12;
            R = R.times(Fq(ec.q, 2.toBigInteger()));
            if (T.bitAt(i.toLong())) {
                val lrq = addLineEval(R, Q, P);
                f = f.times(lrq) as Fq12;
                R = R.plus(Q);
            }
        }
        return f as Fq12
    }

    fun finalExponentiation(element: Fq12, ec: EC = defaultEc): Fq12 {
        if (ec.k === 12.toBigInteger()) {
            var ans = element.pow((ec.q.pow(4) - ec.q.pow(2) + ONE) / ec.n)
            ans = ans.qiPower(2).times(ans) as Fq12;
            ans = ans.qiPower(6).div(ans) as Fq12;
            return ans;
        } else return element.pow((ec.q.pow(ec.k) - 1.toBigInteger()) / ec.n) as Fq12
    }

    fun atePairing(
        P: JacobianPoint,
        Q: JacobianPoint,
        ec: EC = defaultEc
    ): Fq12 {
        val t = defaultEc.x + 1
        var T = t - 1
        T = if (T < 0) -T else T
        return finalExponentiation(millerLoop(T, P.toAffine(), Q.toAffine()), ec);
    }

    fun atePairingMulti(
        Ps: List<JacobianPoint>,
        Qs: List<JacobianPoint>,
        ec: EC = defaultEc
    ): Fq12 {
        val t = defaultEc.x + 1;
        var T = t - 1;
        T = if(T < 0) -T else T
        var prod = Fq12.nil.one(ec.q);
        for(i in 0 until Qs.size){
            prod = prod.times(
                millerLoop(T, Ps[i].toAffine(), Qs[i].toAffine(), ec)
            ) as Fq12;
        }
        return finalExponentiation(prod as Fq12, ec);
    }
}