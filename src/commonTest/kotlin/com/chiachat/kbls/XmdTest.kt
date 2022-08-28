package com.chiachat.kbls

import com.chiachat.kbls.bls.constants.HashInfoConstants.sha512
import com.chiachat.kbls.bls.util.HashToField.expandMessageXmd
import kotlin.test.Test
import kotlin.test.assertEquals

class XmdTest {
    fun genArray(size: Int): UByteArray {
        return UByteArray(size).mapIndexed { index, element ->
            if (index % 3 == 0) index / 3
            else if (index % 5 == 0) index / 5
            else 0
        }.map { it.toUByte() }.toUByteArray()
    }

    val msg: UByteArray = genArray(48)
    val dst = genArray(16).reversedArray()
    val ress = mutableMapOf<UByteArray, Int>()

    @Test
    fun lengths() {
        for (length in 16 until 8192) {
            if(length == 256)
                println("test")
            val result = expandMessageXmd(msg, dst, length, sha512)
            assertEquals(length, result.size)
            var key = result.slice(0 until 16).toUByteArray()
            key = ress.keys.find { buffer -> buffer.contentEquals(key) } ?: key
            ress.set(key, ress[key] ?: 0 + 1)
        }
        for (item in ress.values) {
            assertEquals(item, 1)
        }
    }
}
