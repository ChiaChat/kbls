package com.chiachat.kbls

import Bech32
import com.chiachat.kbls.bech32.KHex
import com.chiachat.kbls.bls.constants.defaultEc
import kotlin.test.Test
import kotlin.test.assertEquals

class KeyGenTest {
    val seed =  ByteArray(32).also { it.fill(0x08) }

    val puzzleHash = "4843c869bba5f65aa1e806cd372dae5668ca3b69640d067e86837ca96b324e71"
    val actualAddress = "xch1fppus6dm5hm94g0gqmxnwtdw2e5v5wmfvsxsvl5xsd72j6ejfecsdnkf2e"

    @Test
    fun genPk() {

    }

    @Test
    fun bech32m(){
        val hex  = KHex(puzzleHash).toUByteArray()
        val encoded: String = Bech32.encode(humanReadablePart = "xch", hex)
        assertEquals(encoded, actualAddress)
    }

    @Test
    fun testEc(){
        val q = defaultEc.q
        val g = G1Generator()

        assert g.is_on_curve()
        assert 2 * g == g + g
        assert (3 * g).is_on_curve()
        assert 3 * g == g + g + g

        g2 = G2Generator()
        assert g2.x * (Fq(q, 2) * g2.y) == Fq(q, 2) * (g2.x * g2.y)
        assert g2.is_on_curve()
        s = g2 + g2
        assert untwist(twist(s.to_affine())) == s.to_affine()
        assert untwist(5 * twist(s.to_affine())) == (5 * s).to_affine()
        assert 5 * twist(s.to_affine()) == twist((5 * s).to_affine())
        assert s.is_on_curve()
        assert g2.is_on_curve()
        assert g2 + g2 == 2 * g2
        assert g2 * 5 == (g2 * 2) + (2 * g2) + g2
        y = y_for_x(g2.x, default_ec_twist, Fq2)
        assert y == g2.y or -y == g2.y

        g_j = G1Generator()
        g2_j = G2Generator()
        g2_j2 = G2Generator() * 2
        assert g.to_affine().to_jacobian() == g
        assert (g_j * 2).to_affine() == g.to_affine() * 2
        assert (g2_j + g2_j2).to_affine() == g2.to_affine() * 3
    }
}
