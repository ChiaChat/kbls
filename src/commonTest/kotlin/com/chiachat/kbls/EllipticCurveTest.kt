package com.chiachat.kbls

import com.chiachat.kbls.bls.constants.BLS12381.q
import com.chiachat.kbls.bls.ec.JacobianPoint
import com.chiachat.kbls.bls.fields.Fq
import com.chiachat.kbls.bls.util.TWO
import kotlin.test.Test
import kotlin.test.assertEquals
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

    @Test
    fun G2Multiplication() {
        assertEquals(
            g2.x.times(g2.y * Fq(q, TWO)),
            g2.x.times(g2.y) * Fq(q, TWO)
        )
        assertTrue("G2 is on curve") { g2.isOnCurve() }
    }

    val s = g2 + g2

    @Test
    fun Twist() {
        // Error is in the untwist
        assertEquals(
            s.toAffine(),
            s.toAffine().twist().untwist()
        )
    }
}
