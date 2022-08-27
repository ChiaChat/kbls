package com.chiachat.kbls.bls.schemes

import com.chiachat.kbls.bls.util.HDKeys
import com.chiachat.kbls.bls.keys.PrivateKey

class AugSchemeMPL {
    companion object {
        fun keyGen(seed: UByteArray): PrivateKey = HDKeys.keyGen(seed)

        fun sign(privateKey: PrivateKey, message: UByteArray){
            val publicKey = privateKey.getG1()
            return coreSignMpl()
        }
    }
}