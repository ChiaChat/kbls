package com.chiachat.kbls.bls.util

import com.ionspin.kotlin.bignum.integer.BigInteger

object Math {
    fun modPow(base: BigInteger, exponent: BigInteger, modulo: BigInteger): BigInteger {
        var base = base
        var exponent = exponent
        if (exponent < 1) return ONE
        else if (base < 0 || base > modulo) base = base.mod(modulo)
        var result = ONE
        while (exponent > 0) {
            if ((exponent.and(ONE) > 0)) result = result.times(base).mod(modulo)
            exponent = exponent.shr(1)
            base = base.times(base).mod(modulo)
        }
        return result
    }
}
