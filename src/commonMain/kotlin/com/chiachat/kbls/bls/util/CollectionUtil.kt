package com.chiachat.kbls.bls.util


fun List<Int>.toUByteArray() = this.map { it.toUByte() }.toUByteArray()