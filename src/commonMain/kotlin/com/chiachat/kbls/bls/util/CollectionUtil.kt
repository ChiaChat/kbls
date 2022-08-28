package com.chiachat.kbls.bls.util


fun List<Int>.toUByteArray() = this.map { it.toUByte() }.toUByteArray()

fun uByteArrayOf(size: Int, fill: Int) = UByteArray(size).also { it.fill(fill.toUByte()) }