package com.chiachat.kbls.bls.util

import com.chiachat.kbls.bls.constants.BLS12381.defaultEc
import com.chiachat.kbls.bls.constants.BLS12381.defaultEcTwist
import com.chiachat.kbls.bls.ec.AffinePoint
import com.chiachat.kbls.bls.ec.EC
import com.chiachat.kbls.bls.ec.JacobianPoint
import com.chiachat.kbls.bls.fields.Field
import com.chiachat.kbls.bls.fields.Fq
import com.chiachat.kbls.bls.fields.Fq2
import com.chiachat.kbls.bls.fields.NotImplementedException
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger

object EcUtil {

    fun yForX(x: Field, ec: EC = defaultEc): Field {
        val u = x.pow(3.toBigInteger()).plus(ec.a.times(x)).plus(ec.b)
        val y = when(u) {
            is Fq -> u.modSqrt()
            is Fq2 -> u.modSqrt()
            else -> throw Exception("Invalid u")
        }
        if (y.equals(ZERO) || !AffinePoint(x, y, false, ec).isOnCurve())
        throw Exception("No y for point x.");
        return y;
    }

    fun scalarMultJacobian(
        value: BigInteger,
        point: JacobianPoint,
        ec: EC = defaultEc
    ): JacobianPoint {
        var value = value
        var result = JacobianPoint(
            point.x.one(ec.q),
            point.x.one(ec.q),
            point.x.zero(ec.q),
            true,
            ec
        )
        if (point.isInfinity || value mod ec.q == ZERO) return result
        var addend = point
        while (value > ZERO) {
            if (value.and(ONE) != ZERO)
                result = result.plus(addend)
            addend = addend.plus(addend)
            value = value.shr(1)
        }
        return result
    }

    fun scalarMultJacobian(
        value: Any,
        point: JacobianPoint,
        ec: EC = defaultEc
    ): JacobianPoint {
        return when (value) {
            is Int -> scalarMultJacobian(value.toBigInteger(), point, ec)
            is BigInteger -> scalarMultJacobian(value, point, ec)
            is Fq -> scalarMultJacobian(value.value, point, ec)
            else -> throw NotImplementedException()
        }
    }

    fun signFq(element: Fq, ec: EC = defaultEc): Boolean {
        return element > (Fq(ec.q, (ec.q - ONE) / TWO))
    }

    fun signFq2(element: Fq2, ec: EC = defaultEcTwist): Boolean {
        if (element.elements[1] == Fq(ec.q, ZERO)) {
            return signFq(element.elements[0] as Fq)
        }
        return element.elements[1] > (Fq(ec.q, (ec.q - ONE) / TWO))
    }


}
