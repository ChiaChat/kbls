package com.chiachat.kbls.bls.ec

import com.chiachat.kbls.bls.constants.BLS12381.defaultEc
import com.chiachat.kbls.bls.fields.*
import com.chiachat.kbls.bls.util.EcUtil.scalarMultJacobian
import com.chiachat.kbls.bls.util.TWO
import com.ionspin.kotlin.bignum.integer.toBigInteger

class AffinePoint(
    val x: Field,
    val y: Field,
    val isInfinity: Boolean,
    val ec: EC = defaultEc
) {

    init {
        if (x.extension != y.extension) {
            throw Exception("Tried to construct an AffinePoint with different extensions for x & y")
        }
    }

    fun isOnCurve(): Boolean {
        return (
            this.isInfinity ||
                this.y.times(this.y) == this.x
                .times(this.x)
                .times(this.x)
                .plus(this.ec.a.times(this.x))
                .plus(this.ec.b)
            )
    }

    fun toJacobian(): JacobianPoint {
        return JacobianPoint(
            this.x,
            this.y,
            this.x.one(this.ec.q),
            this.isInfinity,
            this.ec
        )
    }

    fun twist(): AffinePoint {
        val f = Fq12.nil.one(this.ec.q)
        val wsq = Fq12(this.ec.q, f.root as Fq6, Fq6.nil.zero(this.ec.q) as Fq6)
        val wcu = Fq12(this.ec.q, Fq6.nil.zero(this.ec.q) as Fq6, f.root as Fq6)
        return AffinePoint(this.x * wsq, this.y * wcu, false, this.ec)
    }

    fun untwist(): AffinePoint {
        val f = Fq12.nil.one(this.ec.q)
        val wsq = Fq12(this.ec.q, f.root as Fq6, Fq6.nil.zero(this.ec.q) as Fq6)
        val wcu = Fq12(this.ec.q, Fq6.nil.zero(this.ec.q) as Fq6, f.root as Fq6)
        val point = AffinePoint.fromFieldExt(
            this.x.div(wsq) as FieldExt,
            this.y.div(wcu) as FieldExt,
            false,
            this.ec
        )
        return point
    }

    fun double(): AffinePoint {
        val left = this.x
            .times(this.x)
            .times(Fq(this.ec.q, 3.toBigInteger()))
            .plus(this.ec.a)
        val s = left.div(this.y.times(Fq(this.ec.q, TWO)))
        val newX = s.times(s).minus(this.x).minus(this.x)
        val newY = s.times(this.x.minus(newX)).minus(this.y)
        return AffinePoint(
            newX,
            newY,
            false,
            this.ec
        )
    }

    operator fun plus(value: AffinePoint): AffinePoint {
        if (!this.isOnCurve() || !value.isOnCurve()) throw Exception("Point not on curve")
        if (this.isInfinity) return value
        else if (value.isInfinity) return this
        else if (this.equals(value)) return this.double()
        val s = value.y.minus(this.y).div(value.x.minus(this.x))
        val newX = s.times(s).minus(this.x).minus(value.x)
        val newY = s.times(this.x.minus(newX)).minus(this.y)
        return AffinePoint(
            newX,
            newY,
            false,
            this.ec
        )
    }

    operator fun minus(value: AffinePoint): AffinePoint {
        return this.plus(-value)
    }

    operator fun times(value: Any): AffinePoint {
        return scalarMultJacobian(value, this.toJacobian(), this.ec).toAffine()
    }

    operator fun unaryMinus(): AffinePoint {
        return AffinePoint(
            this.x,
            -this.y,
            this.isInfinity,
            this.ec
        )
    }

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is AffinePoint -> this.x == other.x && this.y == other.y && this.isInfinity == other.isInfinity
            is JacobianPoint -> equals(other.toAffine())
            else -> false
        }
    }

    fun clone(): AffinePoint {
        return AffinePoint(
            this.x,
            this.y,
            this.isInfinity,
            this.ec
        )
    }

    override fun toString(): String {
        return "AffinePoint(x=${this.x}, y=${this.y}, i=${this.isInfinity})"
    }

    companion object {
        @Suppress
        fun fromFieldExt(x: Field, y: Field, isInfinity: Boolean, ec: EC = defaultEc): AffinePoint {
            return when (x) {
                is Fq -> AffinePoint(x, y, isInfinity, ec)
                is Fq2 -> AffinePoint(x, y, isInfinity, ec)
                is FieldExt -> {
                    if (y is FieldExt) {
                        val newX = x.getExtensions(2).minByOrNull { it.isZero() } ?: throw Exception("Invalid x field")
                        val newY = y.getExtensions(2).minByOrNull { it.isZero() } ?: throw Exception("Invalid y field")
                        AffinePoint(newX, newY, isInfinity, ec)
                    } else throw Exception("Invalid y field")
                }
            }
        }
    }
}
