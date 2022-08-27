package com.chiachat.kbls.bls.util

data class HashInfo(
    val convert: (UByteArray) -> UByteArray,
    val byteSize: Int,
    val blockSize: Int,
)