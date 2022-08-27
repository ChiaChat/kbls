package com.chiachat.kbls

import com.chiachat.kbls.bls.constants.BLS12381.defaultEcTwist
import com.chiachat.kbls.bls.constants.BLS12381.q
import com.chiachat.kbls.bls.ec.JacobianPoint
import com.chiachat.kbls.bls.fields.Fq
import com.chiachat.kbls.bls.fields.Fq2
import com.chiachat.kbls.bls.util.EcUtil.signFq2
import com.chiachat.kbls.bls.util.EcUtil.yForX
import com.chiachat.kbls.bls.util.TWO
import com.chiachat.kbls.bls.util.ZERO
import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class EllipticCurveTest {
    val g = JacobianPoint.generateG1()

    @Test
    fun G1Multiplication() {
        assertTrue("Is on curve") { g.isOnCurve() }
        assertEquals(g * 2, g + g)
        assertTrue("Triple on curve") { (g * 3).isOnCurve() }
        assertEquals(g * 3, g + g + g)
    }

    val g2 = JacobianPoint.generateG2()

    val s = g2 + g2

    @Test
    fun Twist() {
        assertEquals(
            s,
            s.toAffine().toJacobian()
        )
        assertEquals(
            s.toAffine(),
            s.toAffine().twist().untwist()
        )
        assertEquals(
            s.times(5).toAffine(),
            (s.toAffine().twist() * 5).untwist()
        )
        assertEquals(
            s.times(5).toAffine().twist(),
            s.toAffine().twist().times(5)
        )
    }

    @Test
    fun G2Multiplication() {
        assertEquals(
            g2.x.times(g2.y * Fq(q, TWO)),
            g2.x.times(g2.y) * Fq(q, TWO)
        )
        assertTrue("G2 is on curve") { g2.isOnCurve() }
        assertTrue("Double on curve") { s.isOnCurve() }
        assertEquals(s, g2.times(2))
        assertEquals(g2 * 5, g2 * 2 + g2 * 2 + g2)
    }

    @Test
    fun TestYForX() {
        assertTrue { true }
        val y = yForX(g2.x, defaultEcTwist)
        assertTrue { y == g2.y || y.unaryMinus() == g2.y }
    }

    val g_j = JacobianPoint.generateG1()
    val g2_j = JacobianPoint.generateG2()
    val g2_j2 = JacobianPoint.generateG2().times(2)

    @Test
    fun Conversions() {
        assertEquals(g, g.toAffine().toJacobian())
        assertEquals(g.toAffine().times(2), g_j.times(2).toAffine())
        assertEquals(g2.toAffine().times(3), g2_j.plus(g2_j2).toAffine())
    }

    @Test
    fun SignFq2() {
        val a = Fq(q, BigInteger(62323))
        val testcase1 = Fq2(q, a, Fq(q, ZERO))
        val testcase2 = Fq2(q, -a, Fq(q, ZERO))
        assertNotEquals(signFq2(testcase1), signFq2(testcase2))
        val testcase3 = Fq2(q, Fq(q, ZERO), a)
        val testcase4 = Fq2(q, Fq(q, ZERO), -a)
        assertNotEquals(signFq2(testcase3), signFq2(testcase4))
    }
}
