package com.chiachat.kbls.bls.ec

import com.chiachat.kbls.bls.constants.BLS12381.defaultEc
import com.chiachat.kbls.bls.fields.Field
import com.chiachat.kbls.bls.fields.Fq
import com.chiachat.kbls.bls.fields.Fq12
import com.chiachat.kbls.bls.fields.Fq6
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
        if (listOf(x, y).any { it.extension > 2 }) {
            throw Exception("Tried to use an extension higher than to create AffinePoint")
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
        return AffinePoint(this.x.times(wsq), this.y.times(wcu), false, this.ec)
    }

    fun untwist(): AffinePoint {
        val f = Fq12.nil.one(this.ec.q)
        val wsq = Fq12(this.ec.q, f.root as Fq6, Fq6.nil.zero(this.ec.q) as Fq6)
        val wcu = Fq12(this.ec.q, Fq6.nil.zero(this.ec.q) as Fq6, f.root as Fq6)
        return AffinePoint(
            this.x.div(wsq),
            this.y.div(wcu),
            false,
            this.ec
        )
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

    fun equals(value: AffinePoint): Boolean {
        return (
            this.x == value.x &&
                this.y == value.y &&
                this.isInfinity == value.isInfinity
            )
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
}
