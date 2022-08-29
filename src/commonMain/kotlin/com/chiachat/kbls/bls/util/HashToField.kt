@file:OptIn(ExperimentalUnsignedTypes::class)

package com.chiachat.kbls.bls.util

import com.chiachat.kbls.bech32.toHex
import com.chiachat.kbls.bls.constants.BLS12381.q
import com.chiachat.kbls.bls.constants.HashInfoConstants.sha256
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger

object HashToField {
    fun I2OSP(value: BigInteger, length: Int): UByteArray {
        if (value < 0.toBigInteger() || value >= (1.toBigInteger().shl(8 * length)))
            throw Exception("Bad I2OSP call: value=$value, length=$length.")
        val bytes: MutableList<Int> = mutableListOf()
        for (i in 0 until length) bytes.add(0)
        var tempValue = value
        for (i in (0 until length).reversed()) {
            bytes[i] = tempValue.and("0xff".toHex().bigInt).intValue(true)
            tempValue = tempValue.shr(8)
        }
        val result = bytes.map { it.toUByte() }.toUByteArray()
        val bytesTemp = value.toUByteArray()
        val toBytesValue = UByteArray(length - bytesTemp.size) + bytesTemp
        if(!toBytesValue.contentEquals(result))
            throw Exception("Unexpected result")
        return result
    }

    fun OS2IP(octets: UByteArray): BigInteger {
        var result = 0.toBigInteger()
        for (octet in octets) {
            result = result.shl(8)
            result += octet.toBigInteger()
        }
        return result
    }

    fun bytesXor(a: UByteArray, b: UByteArray): UByteArray {
        return a.mapIndexed { i, e -> e.xor(b[i]) }.toUByteArray()
    }

    fun expandMessageXmd(
        message: UByteArray,
        dst: UByteArray,
        length: Int,
        hash: HashInfo
    ): UByteArray {
        val ell = ((length + hash.byteSize - 1).toDouble() / hash.byteSize).toInt()
        if (ell > 255) throw Exception("Bad expandMessageXmd call: ell=$ell out of range.")
        val dst_prime = dst + I2OSP(BigInteger(dst.size), 1)
        val Z_pad = I2OSP(0.toBigInteger(), hash.blockSize)
        val lib_str = I2OSP(length.toBigInteger(), 2)
        val b_0 = hash.convert(
            Z_pad + message + lib_str + I2OSP(0.toBigInteger(), 1) + dst_prime
        )
        val bValues: MutableList<UByteArray> = mutableListOf()
        bValues.add(
            hash.convert(
                b_0 + I2OSP(1.toBigInteger(), 1) + dst_prime
            )
        )
        for (i in 1..ell) {
            bValues.add(
                hash.convert(
                    bytesXor(b_0, bValues[i - 1]) + I2OSP(BigInteger(i + 1), 1) + dst_prime
                )
            )
        }
        val pseudoRandomBytes: MutableList<Int> = mutableListOf()
        for (item in bValues) pseudoRandomBytes.addAll(item.map { it.toInt() })
        return pseudoRandomBytes.slice(0 until length).map { it.toUByte() }.toUByteArray()
    }

    fun expandMessageXof(
        message: UByteArray,
        dst: UByteArray,
        length: Int,
        hash: HashInfo
    ): UByteArray {
        val dst_prime = dst + I2OSP(BigInteger(dst.size), 1)
        val message_prime = message + I2OSP(BigInteger(length), 2) + dst_prime
        return hash.convert(message_prime.slice(0 until length).map { it.toUByte() }.toUByteArray())
    }

    fun hashToField(
        message: UByteArray,
        count: Int,
        dst: UByteArray,
        modulus: BigInteger,
        degree: Int,
        byteLength: Int,
        expand: (UByteArray, UByteArray, Int, HashInfo) -> UByteArray,
        hash: HashInfo
    ): List<List<BigInteger>> {
        val lengthInBytes = count * degree * byteLength
        val pseudoRandomBytes = expand(message, dst, lengthInBytes, hash)
        val uValues: MutableList<MutableList<BigInteger>> = mutableListOf()
        for (i in 0 until count) {
            val eValues: MutableList<BigInteger> = mutableListOf()
            for (j in 0 until degree) {
                val elmOffset = byteLength * (j + i * degree)
                val tv = pseudoRandomBytes.slice(
                    elmOffset until elmOffset + byteLength
                ).toUByteArray()
                eValues.add(OS2IP(tv).mod(modulus))
            }
            uValues.add(eValues)
        }
        return uValues
    }

    fun Hp(
        message: UByteArray,
        count: Int,
        dst: UByteArray
    ): List<List<BigInteger>> {
        return hashToField(message, count, dst, q, 1, 64, expand = ::expandMessageXmd, sha256)
    }

    fun Hp2(
        message: UByteArray,
        count: Int,
        dst: UByteArray
    ): List<List<BigInteger>> {
        return hashToField(message, count, dst, q, 2, 64, expand = ::expandMessageXmd, sha256)
    }
}
