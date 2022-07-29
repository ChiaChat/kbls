package com.chiachat.kbls.bls.ec

import com.chiachat.kbls.bls.constants.defaultEc
import com.chiachat.kbls.bls.ec.EC
import com.ionspin.kotlin.bignum.integer.BigInteger


class JacobianPoint(
    val x: BigInteger,
    val y: BigInteger,
    val z: BigInteger,
) {
     public static generateG1(): JacobianPoint
     {
         return new AffinePoint (
                 defaultEc.gx,
         defaultEc.gy,
         false,
         defaultEc
         ).toJacobian();
     }

     public static generateG2(): JacobianPoint
     {
         return new AffinePoint (
                 defaultEcTwist.g2x,
         defaultEcTwist.g2y,
         false,
         defaultEcTwist
         ).toJacobian();
     }

     public static infinityG1(isExtension: Boolean = false): JacobianPoint
     {
         const provider = isExtension ? Fq2 : Fq;
         return new JacobianPoint (
                 provider.nil.zero(defaultEc.q),
         provider.nil.zero(defaultEc.q),
         provider.nil.zero(defaultEc.q),
         true,
         defaultEc
         );
     }

     public static infinityG2(isExtension: Boolean = true): JacobianPoint
     {
         const provider = isExtension ? Fq2 : Fq;
         return new JacobianPoint (
                 provider.nil.zero(defaultEcTwist.q),
         provider.nil.zero(defaultEcTwist.q),
         provider.nil.zero(defaultEcTwist.q),
         true,
         defaultEcTwist
         );
     }

     public static fromBytesG1(
     bytes: UByteArray,
     isExtension: Boolean = false
     ): JacobianPoint
     {
         return JacobianPoint.fromBytes(bytes, isExtension, defaultEc);
     }

     public static fromBytesG2(
     bytes: UByteArray,
     isExtension: Boolean = true
     ): JacobianPoint
     {
         return JacobianPoint.fromBytes(bytes, isExtension, defaultEcTwist);
     }

     public static fromHexG1(
     hex: string,
     isExtension: Boolean = false
     ): JacobianPoint
     {
         return JacobianPoint.fromBytesG1(fromHex(hex), isExtension);
     }

     public static fromHexG2(
     hex: string,
     isExtension: Boolean = true
     ): JacobianPoint
     {
         return JacobianPoint.fromBytesG2(fromHex(hex), isExtension);
     }

     constructor(
         public x: Fq | Fq2,
     public y: Fq | Fq2,
     public z: Fq | Fq2,
     public isInfinity: Boolean,
     public ec: EC = defaultEc
     )
     {
         assert(x instanceof y.constructor);
         assert(y instanceof z.constructor);
     }

     public isOnCurve(): Boolean
     {
         return this.isInfinity || this.toAffine().isOnCurve();
     }

     public isValid(): Boolean
     {
         return (
                 this.isOnCurve() &&
                         this.multiply(this.ec.n).equals(JacobianPoint.infinityG2())
                 );
     }

     public getFingerprint(): number
     {
         const bytes = this.toBytes();
         return bytesToInt(hash256(bytes).slice(0, 4), 'big');
     }

     public toAffine(): AffinePoint
     {
         return this.isInfinity
         ? new AffinePoint (
             Fq.nil.zero(this.ec.q),
         Fq.nil.zero(this.ec.q),
         true,
         this.ec
         )
         : new AffinePoint (
             this.x.divide(this.z.pow(2 n)) as Fq | Fq2,
         this.y.divide(this.z.pow(3 n)) as Fq | Fq2,
         false,
         this.ec
         );
     }

     public toBytes(): UByteArray
     {
         const point = this.toAffine();
         const output = point . x . toBytes ();
         if (point.isInfinity) {
             const bytes =[0xc0];
             for (let i = 0; i < output.length - 1; i++) bytes.push(0);
             return UByteArray.from(bytes);
         }
         const sign =
         point.y instanceof Fq2
         ? signFq2(point.y, this.ec)
         : signFq(point.y, this.ec);
         output[0] | = sign ? 0xa0 : 0x80;
         return output;
     }

     public toHex(): string
     {
         return toHex(this.toBytes());
     }

     public toString(): string
     {
         return `JacobianPoint(x=${this.x}, y=${this.y}, z=${this.z}, i=${this.isInfinity})`;
     }

     public double(): JacobianPoint
     {
         if (this.isInfinity || this.y.equals(this.x.zero(this.ec.q)))
             return new JacobianPoint (
                     this.x.one(this.ec.q),
         this.x.one(this.ec.q),
         this.x.zero(this.ec.q),
         true,
         this.ec
         );
         const S = this.x
         .multiply(this.y)
         .multiply(this.y)
         .multiply(new Fq (this.ec.q, 4 n));
         const Z_sq = this.z.multiply(this.z);
         const Z_4th = Z_sq . multiply (Z_sq);
         const Y_sq = this.y.multiply(this.y);
         const Y_4th = Y_sq . multiply (Y_sq);
         const M = this.x
         .multiply(this.x)
         .multiply(new Fq (this.ec.q, 3 n))
         .add(this.ec.a.multiply(Z_4th));
         const X_p = M . multiply (M).subtract(S.multiply(new Fq (this.ec.q, 2 n)));
         const Y_p = M . multiply (S.subtract(X_p)).subtract(
             Y_4th.multiply(new Fq (this.ec.q, 8 n)
         )
         );
         const Z_p = this.y.multiply(this.z).multiply(new Fq (this.ec.q, 2 n));
         return new JacobianPoint (
                 X_p as Fq | Fq2,
         Y_p as Fq | Fq2,
         Z_p as Fq | Fq2,
         false,
         this.ec
         );
     }

     public negate(): JacobianPoint
     {
         return this.toAffine().negate().toJacobian();
     }

     public add(value : JacobianPoint): JacobianPoint
     {
         if (this.isInfinity) return value;
         else if (value.isInfinity) return this;
         const U1 = this.x.multiply(value.z.pow(2 n));
         const U2 = value . x . multiply (this.z.pow(2 n));
         const S1 = this.y.multiply(value.z.pow(3 n));
         const S2 = value . y . multiply (this.z.pow(3 n));
         if (U1.equals(U2)) {
             if (!S1.equals(S2)) {
                 return new JacobianPoint (
                         this.x.one(this.ec.q),
                 this.x.one(this.ec.q),
                 this.x.zero(this.ec.q),
                 true,
                 this.ec
                 );
             } else return this.double();
         }
         const H = U2 . subtract (U1);
         const R = S2 . subtract (S1);
         const H_sq = H . multiply (H);
         const H_cu = H . multiply (H_sq);
         const X3 = R . multiply (R)
             .subtract(H_cu)
             .subtract(U1.multiply(H_sq).multiply(new Fq (this.ec.q, 2 n)));
         const Y3 = R . multiply (U1.multiply(H_sq).subtract(X3)).subtract(
             S1.multiply(H_cu)
         );
         const Z3 = H . multiply (this.z).multiply(value.z);
         return new JacobianPoint (
                 X3 as Fq | Fq2,
         Y3 as Fq | Fq2,
         Z3 as Fq | Fq2,
         false,
         this.ec
         );
     }

     public multiply(value : Fq | bigint): JacobianPoint
     {
         return scalarMultJacobian(value, this, this.ec);
     }

     public equals(value : JacobianPoint): Boolean
     {
         return this.toAffine().equals(value.toAffine());
     }

     public clone(): JacobianPoint
     {
         return new JacobianPoint (
                 this.x.clone(),
         this.y.clone(),
         this.z.clone(),
         this.isInfinity,
         this.ec
         );
     }
 }
