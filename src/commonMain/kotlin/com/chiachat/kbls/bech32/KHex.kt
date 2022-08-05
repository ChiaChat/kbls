package com.chiachat.kbls.bech32

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign

class KHex(val value: String) {
    constructor(intValue: Int) : this(intToHex(intValue))
    constructor(bigIntValue: BigInteger): this(bigIntToHex(bigIntValue))

    fun toUByteArray(hexStr: String = value): UByteArray {
        if (hexStr.length % 2 != 0) throw IllegalStateException("Hex string must have an even length")
        return hexStr.chunked(2).map { it.toUInt(16).toByte() }.toByteArray().toUByteArray()
    }

    fun toBigInt(): BigInteger  {
        return if(value.startsWith("-")){
            val hexStr = value.drop(1)
            BigInteger.fromUByteArray(toUByteArray(hexStr), Sign.NEGATIVE)
        }else {
            BigInteger.fromUByteArray(toUByteArray(value), Sign.POSITIVE)
        }
    }

    override fun toString(): String {
        return value
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

        private fun bigIntToHex(intValue: BigInteger): String{
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
