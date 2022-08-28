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
import kotlin.math.max

object EcUtil {

    fun yForX(x: Field, ec: EC = defaultEc): Field {
        val u = x.pow(3).plus(ec.a.times(x)).plus(ec.b)
        val y = when (u) {
            is Fq -> u.modSqrt()
            is Fq2 -> u.modSqrt()
            else -> throw Exception("Invalid u")
        }
        if (y.equals(ZERO) || !AffinePoint(x, y, false, ec).isOnCurve()) {
            throw Exception("No y for point x.")
        }
        return y
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
            if (value.and(ONE) != ZERO) {
                result = result.plus(addend)
            }
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

    fun evalIso(
        P: JacobianPoint,
        mapCoeffs: Array<Array<Fq2>>,
        ec: EC
    ): JacobianPoint {
        val x = P.x
        val y = P.y
        val z = P.z
        val mapValues: Array<Fq2?> = arrayOfNulls(4)
        var maxOrd = mapCoeffs[0].size
        for (coeffs in mapCoeffs.slice(1 until mapCoeffs.size)) {
            maxOrd = max(maxOrd, coeffs.size)
        }
        val zPows: MutableList<Fq2?> = mutableListOf()
        for (i in 0 until maxOrd) {
            zPows.add(null)
        }
        zPows[0] = z.pow(0) as Fq2
        zPows[1] = z.pow(2) as Fq2
        for (i in 2 until zPows.size) {
            val pow = zPows[i - 1] ?: throw Exception("null zpow")
            val pow2 = zPows[1] ?: throw Exception("null zpow")
            zPows[i] = pow.times(pow2) as Fq2
        }
        mapCoeffs.forEachIndexed { i, item ->
            val coeffsZ = item
                .slice(item.indices)
                .reversed()
                .mapIndexed { i2, item2 -> item2.times(zPows[i2]!!) }
            var temp = coeffsZ[0]
            for (coeff in coeffsZ.slice( 1 until coeffsZ.size)) {
                temp = temp.times(x)
                temp = temp.plus(coeff)
            }
            mapValues[i] = temp as Fq2
        }

        mapValues[1] = mapValues[1]!!.times(zPows[1]!!) as Fq2
        mapValues[2] = mapValues[2]!!.times(y) as Fq2
        mapValues[3] = mapValues[3]!!.times(z.pow(3)) as Fq2

        val Z = mapValues[1]!!.times(mapValues[3]!!)
        val X = mapValues[0]!!.times(mapValues[3]!!).times(Z)
        val Y = mapValues[2]!!.times(mapValues[1]!!).times(Z).times(Z)

        return JacobianPoint(X, Y, Z, P.isInfinity, ec)
    }
}
