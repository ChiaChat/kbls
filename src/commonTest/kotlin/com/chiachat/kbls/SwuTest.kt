package com.chiachat.kbls

import com.chiachat.kbls.bech32.toHex
import com.chiachat.kbls.bls.fields.Fq
import com.chiachat.kbls.bls.fields.Fq2
import com.chiachat.kbls.bls.util.OpSwuG2.g2Map
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class SwuTest {
    val dst_1_expected =
        "81,85,85,88,45,86,48,49,45,67,83,48,50,45,119,105,116,104,45,66,76,83,49,50,51,56,49,71,50,95,88,77,68,58,83,72,65,45,50,53,54,95,83,83,87,85,95,82,79,95"
            .split(",")
            .map { it.toUByte() }
            .toUByteArray()
    val dst_1 = "QUUX-V01-CS02-with-BLS12381G2_XMD:SHA-256_SSWU_RO_"
        .encodeToByteArray()
        .toUByteArray()
    val msg_1 = "abcdef0123456789".encodeToByteArray().toUByteArray()
    val res = g2Map(msg_1, dst_1).toAffine()

    @Test
    fun CheckElements() {
        assertContentEquals(dst_1_expected, dst_1)
        assertEquals(
            ((res.x as Fq2).elements[0] as Fq).value,
            "0x121982811d2491fde9ba7ed31ef9ca474f0e1501297f68c298e9f4c0028add35aea8bb83d53c08cfc007c1e005723cd0".toHex().bigInt
        )
        assertEquals(
            ((res.x as Fq2).elements[1] as Fq).value,
            "0x190d119345b94fbd15497bcba94ecf7db2cbfd1e1fe7da034d26cbba169fb3968288b3fafb265f9ebd380512a71c3f2c".toHex().bigInt
        )
        assertEquals(
            ((res.y as Fq2).elements[0] as Fq).value,
            "0x05571a0f8d3c08d094576981f4a3b8eda0a8e771fcdcc8ecceaf1356a6acf17574518acb506e435b639353c2e14827c8".toHex().bigInt
        )
        assertEquals(
            ((res.y as Fq2).elements[1] as Fq).value,
            "0x0bb5e7572275c567462d91807de765611490205a941a5a6af3b1691bfe596c31225d3aabdf15faff860cb4ef17c7c3be".toHex().bigInt
        )
    }
}
