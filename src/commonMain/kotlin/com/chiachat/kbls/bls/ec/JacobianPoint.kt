package com.chiachat.kbls.bls.ec

import com.chiachat.kbls.bech32.KHex
import com.chiachat.kbls.bls.constants.BLS12381.defaultEc
import com.chiachat.kbls.bls.constants.BLS12381.defaultEcTwist
import com.chiachat.kbls.bls.fields.*
import com.chiachat.kbls.bls.util.EcUtil.scalarMultJacobian
import com.chiachat.kbls.bls.util.EcUtil.signFq
import com.chiachat.kbls.bls.util.EcUtil.signFq2
import com.chiachat.kbls.bls.util.EcUtil.yForX
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign
import com.ionspin.kotlin.bignum.integer.toBigInteger
import com.soywiz.krypto.SHA256
import com.soywiz.krypto.sha256

class JacobianPoint(
    val x: Field,
    val y: Field,
    val z: Field,
    val isInfinity: Boolean,
    val ec: EC = defaultEc
) {
    fun isOnCurve(): Boolean {
        return this.isInfinity || this.toAffine().isOnCurve()
    }

    fun isValid(): Boolean {
        return this.isOnCurve() && this.times(this.ec.n) == infinityG2()
    }

    fun getFingerprint(): BigInteger {
        val hash = this.toBytes().toByteArray().sha256().bytes.toUByteArray()
        return BigInteger.fromUByteArray(hash, Sign.POSITIVE)
    }

    fun toAffine(): AffinePoint {
        return if (this.isInfinity) AffinePoint(
            Fq.nil.zero(this.ec.q),
            Fq.nil.zero(this.ec.q),
            true,
            this.ec
        )
        else AffinePoint(
            this.x.div(this.z.pow(2.toBigInteger())),
            this.y.div(this.z.pow(3.toBigInteger())),
            false,
            this.ec
        )
    }

    fun toBytes(): UByteArray {
        val point = this.toAffine()
        val output = point.x.toBytes()
        if (point.isInfinity) {
            val bytes: MutableList<UByte> = mutableListOf(0xc0.toUByte())
            for (i in output.indices) {
                bytes.plus(0.toUByte())
            }
            return bytes.toUByteArray()
        }
        val sign = if (point.y is Fq2) signFq2(point.y, this.ec) else signFq(point.y as Fq, this.ec)
        output[0] = if (sign) 0xa0.toUByte() else 0x80.toUByte()
        return output
    }

    fun toHex(): KHex {
        return KHex(this.toBytes())
    }

    override fun toString(): String {
        return "JacobianPoint(x=${this.x}, y=${this.y}, z=${this.z}, i=${this.isInfinity})"
    }

    fun double(): JacobianPoint {
        if (this.isInfinity || this.y.equals(this.x.zero(this.ec.q))) return JacobianPoint(
            this.x.one(this.ec.q),
            this.x.one(this.ec.q),
            this.x.zero(this.ec.q),
            true,
            this.ec
        )
        val S = this.x.times(this.y).times(this.y).times(Fq(this.ec.q, 4.toBigInteger()))
        val Z_sq = this.z.times(this.z)
        val Z_4th = Z_sq.times(Z_sq)
        val Y_sq = this.y.times(this.y)
        val Y_4th = Y_sq.times(Y_sq)
        val M = this.x.times(this.x).times(Fq(this.ec.q, 3.toBigInteger())).plus(this.ec.a.times(Z_4th))
        val X_p = M.times(M).minus(S.times(Fq(this.ec.q, 2.toBigInteger())))
        val Y_p = M.times(S.minus(X_p)).minus(
            Y_4th.times(Fq(this.ec.q, 8.toBigInteger()))
        )
        val Z_p = this.y.times(this.z).times(Fq(this.ec.q, 2.toBigInteger()))
        return JacobianPoint(
            X_p,
            Y_p,
            Z_p,
            false,
            this.ec
        )
    }

    operator fun unaryMinus(): JacobianPoint {
        return this.toAffine().unaryMinus().toJacobian()
    }

    operator fun plus(value: JacobianPoint): JacobianPoint {
        if (this.isInfinity) return value
        else if (value.isInfinity) return this
        val U1 = this.x.times(value.z.pow(2.toBigInteger()))
        val U2 = value.x.times(this.z.pow(2.toBigInteger()))
        val S1 = this.y.times(value.z.pow(3.toBigInteger()))
        val S2 = value.y.times(this.z.pow(3.toBigInteger()))
        if (U1.equals(U2)) {
            if (!S1.equals(S2)) {
                return JacobianPoint(
                    this.x.one(this.ec.q),
                    this.x.one(this.ec.q),
                    this.x.zero(this.ec.q),
                    true,
                    this.ec
                )
            } else return this.double()
        }
        val H = U2.minus(U1)
        val R = S2.minus(S1)
        val H_sq = H.times(H)
        val H_cu = H.times(H_sq)
        val X3 = R.times(R).minus(H_cu).minus(
            U1.times(H_sq).times(Fq(this.ec.q, 2.toBigInteger()))
        )
        val Y3 = R
            .times(U1.times(H_sq).minus(X3))
            .minus(S1.times(H_cu))
        val Z3 = H.times(this.z).times(value.z)
        return JacobianPoint(
            X3,
            Y3,
            Z3,
            false,
            this.ec
        )
    }

    operator fun times(value: Any): JacobianPoint {
        return scalarMultJacobian(value, this, this.ec)
    }

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is AffinePoint -> this.toAffine() == other
            is JacobianPoint -> this.toAffine() == other.toAffine()
            else -> return false
        }
    }

//    fun clone(): JacobianPoint {
//        return JacobianPoint(
//            this.x.clone(),
//            this.y.clone(),
//            this.z.clone(),
//            this.isInfinity,
//            this.ec
//        )
//    }

    companion object {
        fun fromBytes(
            bytes: UByteArray,
            isExtension: Boolean,
            ec: EC = defaultEc
        ): JacobianPoint {
            val provider: Field = if (isExtension) Fq2.nil else Fq.nil
            if (isExtension) {
                if (bytes.size !== 96) throw Exception("Expected 96 bytes.")
            } else {
                if (bytes.size !== 48) throw Exception("Expected 48 bytes.")
            }
            val mByte = bytes[0].and(0xe0.toUByte())
            if (listOf(
                    0x20.toUByte(),
                    0x60.toUByte(),
                    0xe0.toUByte()
                ).contains(mByte)
            ) throw Exception("Invalid first three bits.")
            val compressed = (mByte.and(0x80.toUByte())) != 0.toUByte()
            val infinity = (mByte.and(0x40.toUByte())) != 0.toUByte()
            val signed = (mByte.and(0x20.toUByte())) != 0.toUByte()
            if (!compressed) throw Exception("Compression bit must be 1.")
            bytes[0] = bytes[0].and(0x1f.toUByte())
            if (infinity) {
                for (byte in bytes) {
                    if (byte != 0.toUByte()) throw Exception(
                        "Point at infinity, but found non-zero byte."
                    )
                }
                return AffinePoint(
                    provider.nil().zero(ec.q),
                    provider.nil().zero(ec.q),
                    true,
                    ec
                ).toJacobian()
            }
            val x = (if (isExtension) Fq2.nil else Fq.nil).nil().fromBytes(ec.q, bytes)
            val yValue = yForX(x, ec)
            val sign = if (isExtension) signFq2(yValue as Fq2, ec) else signFq(yValue as Fq, ec)
            val y = if (sign == signed) yValue else -yValue
            return AffinePoint(x, y, false, ec).toJacobian()
        }

        fun fromHex(
            hex: KHex,
            isExtension: Boolean,
            ec: EC = defaultEc
        ): JacobianPoint {
            return fromBytes(hex.byteArray, isExtension, ec)
        }

        fun generateG1(): JacobianPoint {
            return AffinePoint(
                defaultEc.gx,
                defaultEc.gy,
                false,
                defaultEc
            ).toJacobian()
        }

        fun generateG2(): JacobianPoint {
            return AffinePoint(
                defaultEcTwist.g2x,
                defaultEcTwist.g2y,
                false,
                defaultEcTwist
            ).toJacobian()
        }

        fun infinityG1(isExtension: Boolean = false): JacobianPoint {
            val provider: Field = if (isExtension) Fq2.nil else Fq.nil
            return JacobianPoint(
                provider.nil().zero(defaultEc.q),
                provider.nil().zero(defaultEc.q),
                provider.nil().zero(defaultEc.q),
                true,
                defaultEc
            )
        }

        fun infinityG2(isExtension: Boolean = true): JacobianPoint {
            val provider = if (isExtension) Fq2.nil else Fq.nil
            return JacobianPoint(
                provider.nil().zero(defaultEcTwist.q),
                provider.nil().zero(defaultEcTwist.q),
                provider.nil().zero(defaultEcTwist.q),
                true,
                defaultEcTwist
            )
        }

        fun fromBytesG1(
            bytes: UByteArray,
            isExtension: Boolean = false
        ): JacobianPoint {
            return JacobianPoint.fromBytes(bytes, isExtension, defaultEc)
        }

        fun fromBytesG2(
            bytes: UByteArray,
            isExtension: Boolean = true
        ): JacobianPoint {
            return JacobianPoint.fromBytes(bytes, isExtension, defaultEcTwist)
        }

        fun fromHexG1(
            hex: KHex,
            isExtension: Boolean = false
        ): JacobianPoint {
            return JacobianPoint.fromBytesG1(hex.byteArray, isExtension)
        }

        fun fromHexG2(
            hex: KHex,
            isExtension: Boolean = true
        ): JacobianPoint {
            return JacobianPoint.fromBytesG2(hex.byteArray, isExtension)
        }
    }
}
