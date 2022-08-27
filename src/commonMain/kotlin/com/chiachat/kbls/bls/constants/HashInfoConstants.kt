package com.chiachat.kbls.bls.constants

import com.chiachat.kbls.bls.util.HashInfo
import com.soywiz.krypto.sha256
import com.soywiz.krypto.sha512

object HashInfoConstants {
    val sha256 = HashInfo(
        byteSize = 32,
        blockSize = 64,
        convert = { buffer -> buffer.toByteArray().sha256().bytes.toUByteArray() }
    )

    val sha512 = HashInfo(
        byteSize = 64,
        blockSize = 128,
        convert = { buffer -> buffer.toByteArray().sha512().bytes.toUByteArray() }
    )
}
