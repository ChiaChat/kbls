package com.chiachat.kbls.bech32

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign

class KHex(valueStr: String) {
    constructor(intValue: Int) : this(intToHex(intValue))
    constructor(bigIntValue: BigInteger) : this(bigIntToHex(bigIntValue))
    constructor(byteArray: UByteArray) : this(BigInteger.fromUByteArray(byteArray, Sign.POSITIVE))

    val value: String
    val sign: Sign

    init {
        var inputValue = valueStr
        sign = if (inputValue.contains("-")) Sign.NEGATIVE else Sign.POSITIVE
        inputValue = inputValue.replace("-", "")
        inputValue = if (inputValue.startsWith("0x")) {
            inputValue.drop(2)
        } else inputValue
        inputValue = if (inputValue.length % 2 == 0) inputValue else "0$inputValue"
        value = inputValue
    }

    val byteArray: UByteArray by lazy {
        value
            .chunked(2)
            .map { it.toUInt(16).toByte() }
            .toByteArray()
            .toUByteArray()
    }

    val bigInt: BigInteger by lazy {
        BigInteger.fromUByteArray(byteArray, sign)
    }

    override fun toString(): String {
        val signStr = if(sign == Sign.NEGATIVE) "-" else ""
        return "${signStr}0x$value"
    }

    companion object {
        private const val HEXCODE = "0123456789ABCDEF"

        private fun intToHex(intValue: Int): String {
            var num = intValue
            val builder = StringBuilder()
            while (num > 0) {
                val hexDigit: Int = num % 16
                builder.append(HEXCODE[hexDigit])
                num /= 16
            }
            return builder.reverse().toString()
        }

        private fun bigIntToHex(intValue: BigInteger): String {
            var num = intValue
            val builder = StringBuilder()
            while (num > 0) {
                val hexDigit: Int = (num % 16).intValue(true)
                builder.append(HEXCODE[hexDigit])
                num /= 16
            }
            return builder.reverse().toString()
        }
    }
}

fun String.toHex(): KHex = KHex(this)
