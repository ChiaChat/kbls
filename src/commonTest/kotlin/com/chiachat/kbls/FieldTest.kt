package com.chiachat.kbls

import com.chiachat.kbls.bls.constants.BLS12381.q
import com.chiachat.kbls.bls.fields.*
import com.chiachat.kbls.bls.util.ONE
import com.chiachat.kbls.bls.util.TWO
import com.ionspin.kotlin.bignum.integer.toBigInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class FieldTest {
    val a = Fq("17".toBigInteger(), "30".toBigInteger())
    val a_2 = Fq("17".toBigInteger(), "30".toBigInteger())
    val b = Fq("17".toBigInteger(), "-18".toBigInteger())
    val c = Fq2("17".toBigInteger(), a, b)
    val c_2 = Fq2("17".toBigInteger(), a, b)
    val d = Fq2("17".toBigInteger(), a + a, Fq("17".toBigInteger(), "-5".toBigInteger()))
    val e =
        (c * d)
            as Fq2
    val f = (e * d) as Fq2

    val eQ = 17.toBigInteger()
    val expectedE = Fq2(
        eQ,
        Fq(eQ, 10.toBigInteger()), Fq(eQ, 11.toBigInteger())
    )

    @Test
    fun testMultiplication(){
        assertEquals(expectedE, e)
    }

    @Test
    fun testEquality() {
        assertEquals(a, a_2)
        assertEquals(c, c_2)
        assertEquals(g, g_2)
        assertEquals(h, h_2)
        assertEquals(i, i_2)
    }

    @Test
    fun BasicMultiplication() {
        assertNotEquals(e, f)
    }

    val e_squared = (e * e) as Fq2
    val e_sqrt = e_squared.modSqrt()

    @Test
    fun `SquareAndRoot`() {
        assertEquals(e_sqrt.pow(TWO) as Fq2, e_squared)
    }

    @Test
    fun pow() {
        assertEquals(e * e * e, e.pow(3.toBigInteger()))
        assertEquals(e * e * e * e, e.pow(4.toBigInteger()))
    }

    val a2 = Fq(
        "172487123095712930573140951348".toBigInteger(),
        "3012492130751239573498573249085723940848571098237509182375".toBigInteger()
    )
    val b2 = Fq(
        "172487123095712930573140951348".toBigInteger(),
        "3432984572394572309458723045723849".toBigInteger()
    )
    val c2 = Fq2("172487123095712930573140951348".toBigInteger(), a2, b2)

    @Test
    fun Inequality() {
        assertTrue { !b2.equals(c2) }
    }

    val g = Fq6("17".toBigInteger(), c, d, (d * d * c) as Fq2)
    val g_2 = Fq6("17".toBigInteger(), c, d, (d * d * c) as Fq2)
    val h = Fq6(
        "17".toBigInteger(),
        ((c * a) + a) as Fq2,
        (c * b * a) as Fq2,
        (d * b * b * Fq("17".toBigInteger(), "21".toBigInteger())) as Fq2
    )
    val h_2 = Fq6(
        "17".toBigInteger(),
        ((c * a) + a) as Fq2,
        (c * b * a) as Fq2,
        (d * b * b * Fq("17".toBigInteger(), "21".toBigInteger())) as Fq2
    )
    val i = Fq12("17".toBigInteger(), g, h)
    val i_2 = Fq12("17".toBigInteger(), g, h)

    @Test
    fun DoubleNegation() {
        val inverse = i.inverse()
        val negatedInverse = inverse.inverse()
        assertEquals(i, negatedInverse)
    }

    @Test
    fun `InverseRootIdentity`() {
        assertEquals((i.root.inverse() * i.root), Fq6.nil.one("17".toBigInteger()))
    }

    val x = Fq12("17".toBigInteger(), Fq6.nil.zero("17".toBigInteger()) as Fq6, i.root as Fq6)

    @Test
    fun `InverseIdentity`() {
        assertEquals((x.inverse() * x), Fq12.nil.one("17".toBigInteger()))
    }

    val j = Fq6(
        "17".toBigInteger(),
        (c * a + a) as Fq2,
        Fq2.nil.zero("17".toBigInteger()) as Fq2,
        Fq2.nil.zero("17".toBigInteger()) as Fq2
    )
    val j2 = Fq6(
        "17".toBigInteger(),
        ((c * a) + a) as Fq2,
        Fq2.nil.zero("17".toBigInteger()) as Fq2,
        Fq2.nil.one("17".toBigInteger()) as Fq2
    )

//    @Test
//    fun `FirstEqualsElement`() {
//        assertEquals(j, c * a + a)
//    }

    @Test
    fun `SecondDoesNotEqualElement`() {
        assertTrue { !j2.equals(c * a + a) }
    }

    @Test
    fun `FirstDoesNotEqualSecond`() {
        assertTrue { !j.equals(j2) }
    }

    val one = Fq(q, ONE)
    val two = one + one
    val a3 = Fq2(q, two, two)
    val b3 = Fq6(q, a3, a3, a3)
    val c3 = Fq12(q, b3, b3)

    @Test
    fun `FrobCoefficients`() {
        for (base in listOf(a3, b3, c3)) {
            for (expo in 1 until base.extension) {
                assertEquals(base.qiPower(expo), base.pow(q.pow(expo.toBigInteger())))
            }
        }
    }

    @Test
    fun ByteArrayConversion(){
        assertEquals(a3, Fq2.nil.fromBytes(a3.Q, a3.toBytes()))
        assertEquals(j, Fq6.nil.fromBytes(j.Q, j.toBytes()))
    }

}
