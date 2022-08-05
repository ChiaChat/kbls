package com.chiachat.kbls.bls.fields


class NotImplementedException: Exception("Operation is not implemented")

class InvalidByteArraySizeException: Exception("Input array contained an invalid number of bytes")

class ValueException(message: String): Exception(message)