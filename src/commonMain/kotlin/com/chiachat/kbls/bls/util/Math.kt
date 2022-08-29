package com.chiachat.kbls.bls.util

import com.ionspin.kotlin.bignum.integer.BigInteger

object Math {
    fun modPow(base: BigInteger, exponent: BigInteger, modulo: BigInteger): BigInteger {
        var baseVar = base
        var exponentVar = exponent
        if (exponentVar < 1) return ONE
        else if (baseVar < 0 || baseVar > modulo) baseVar = baseVar.mod(modulo)
        var result = ONE
        while (exponentVar > 0) {
            if ((exponentVar.and(ONE) > 0)) result = result.times(baseVar).mod(modulo)
            exponentVar = exponentVar.shr(1)
            baseVar = baseVar.times(baseVar).mod(modulo)
        }
        return result
    }
}
