package com.chiachat.kbls

import com.chiachat.kbls.bech32.toHex
import com.ionspin.kotlin.bignum.integer.toBigInteger
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class Hex {
    val a = "1A"
    val b = "16AFCDF"
    val c = "21AF"
    val cArr = listOf(33, 175).map { it.toUByte() }.toUByteArray()

    @Test
    fun fromHex() {
        assertEquals(a.toBigInteger(16), a.toHex().bigInt)
        assertEquals(b.toBigInteger(16), b.toHex().bigInt)
        val cHex = c.toHex().byteArray
        assertContentEquals(cArr, cHex)
    }
}
