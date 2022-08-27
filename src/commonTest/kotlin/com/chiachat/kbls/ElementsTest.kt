package com.chiachat.kbls

import com.chiachat.kbls.bls.ec.JacobianPoint
import com.chiachat.kbls.bls.util.byteListToBigInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ElementsTest {
    val i1 = byteListToBigInteger(1, 2)
    val i2 = byteListToBigInteger(3, 1, 4, 1, 5, 9)
    val b1 = i1
    val b2 = i2
    val g1 = JacobianPoint.generateG1()
    val g2 = JacobianPoint.generateG2()
    val u1 = JacobianPoint.infinityG1()
    val u2 = JacobianPoint.infinityG2()
    val x1 = g1.times(b1)
    val x2 = g1.times(b2)
    val y1 = g2.times(b1)
    val y2 = g2.times(b2)

    @Test
    fun G1MultiplicationEquality() {
        assertNotEquals(x1, x2)
        assertEquals(x1.times(b1), x1.times(b1))
        assertNotEquals(x1.times(b2), x1.times(b1))
    }

    val left = x1.plus(u1)
    val right = x1

    @Test
    fun G1AdditionEquality() {
        assertEquals(left, right)
        assertEquals(x1.plus(x2), x2.plus(x1))
        assertEquals(x1.plus(-x1), u1)
        assertEquals(x1.x, x1.x.fromBytes(x1.x.Q, x1.x.toBytes()))
//        assertEquals(x1, JacobianPoint.fromBytesG1(x1.toBytes()))
    }
}
