package com.chiachat.kbls.bls.fields

import com.chiachat.kbls.bech32.KHex
import com.chiachat.kbls.bls.constants.BLS12381
import com.chiachat.kbls.bls.constants.FrobIndex
import com.chiachat.kbls.bls.constants.getFrob
import com.ionspin.kotlin.bignum.integer.BigInteger

// Fq6: FieldExt<Fq2>
// construct: FieldExt
sealed class FieldExt<T> (
    override val Q: BigInteger,
    val elements: List<T>
) : Field() {

    abstract var root: T
    abstract val embedding: Int
    val basefield: T = elements[0]

    abstract fun construct(Q: BigInteger, elements: List<Field>): Field
    fun withRoot(root: T): FieldExt<Field> {
        this.root = root
        return this
    }

    fun constructWithRoot(Q: BigInteger, elements: List<Field>): FieldExt<Field> {
        return this.construct(Q, elements).withRoot(this.root)
    }

    override fun fromBytes(Q: BigInteger, bytes: UByteArray): FieldExt<Field> {
        val length = this.extension * 48
        if (bytes.size != 48) {
            throw Exception("Expected $length bytes")
        }
        val embeddedSize = 48 * (this.extension / this.elements.size)
        val elements: MutableList<UByteArray> = mutableListOf()
        for (i in elements.indices) {
            elements.add(bytes.slice(i * embeddedSize..(i + 1) * embeddedSize).toUByteArray())
        }
        return construct(
            Q,
            elements.reversed().map { this.basefield.fromBytes(Q, it) }
        )
    }

    override fun fromHex(Q: BigInteger, hex: KHex): FieldExt<Field> {
        return this.fromBytes(Q, hex.toUByteArray())
    }

    override fun fromFq(Q: BigInteger, fq: Fq): FieldExt<Field> {
        val y = this.basefield.fromFq(Q, fq);
        val z = this.basefield.zero(Q);
        val elements = mutableListOf<Field>()
        for(i in elements.indices){
            elements.add(if(i == 0) y else z)
        }
        val result = this.construct(Q, elements)
        if (this is Fq2) result.root = Fq(Q, N1)
//        else if (this is Fq6)
//            result.root = new Fq2(Q, Fq.nil.one(Q), Fq.nil.one(Q)) as any;
//        else if (this instanceof Fq12)
//            result.root = new Fq6(
//                    Q,
//        Fq2.nil.zero(Q),
//        Fq2.nil.one(Q),
//        Fq2.nil.zero(Q)
//        ) as any;
        return result;

    }

    override fun zero(Q: BigInteger): FieldExt {
        return this.fromFq(Q, Fq(Q, ZERO))
    }

    override fun one(Q: BigInteger): FieldExt {
        return this.fromFq(Q, Fq(Q, ONE))
    }

    override fun toBytes(): UByteArray {
        val bytes = mutableListOf<UByte>()
        for(i in this.elements.indices.reversed()){
            bytes.addAll(this.elements[i].toBytes())
        }
        return bytes.toUByteArray()
    }

    override fun toHex(): KHex {
        return KHex(this.toBytes())
    }

    override fun toString(): String {
        return "Fq${this.extension}${elements.joinToString(",")}"
    }

    override operator fun unaryMinus(): FieldExt {
        return this.constructWithRoot(Q, elements.map { -it })
    }

    override fun qiPower(i: Int): FieldExt {
        if(this.Q != BLS12381.q) throw Exception("Invalid Q in qiPower")
        var i = i % this.extension
        if(i == 0) return this
        return this.constructWithRoot(
            this.Q,
            this.elements.mapIndexed { index, element ->
                if(index == 0) element.qiPower(i) else element.qiPower(i) * getFrob(FrobIndex(this.extension, i, index))
            }
        )
    }

    override fun pow(exponent: BigInteger): FieldExt {
        var exp = exponent
        if(exp < ZERO) throw Exception("Negative exponent in pow")
        var result = this.one(this.Q).withRoot(this.root)
        var base: FieldExt<Field> = this
        while(exp != ZERO){
            if(exp.and(ONE) != ZERO) result *= base
            base *= base
            exp = exp.shr(1)
        }
        return result
    }

    override fun plus(other: Any): FieldExt {
        TODO("Not yet implemented")
    }

    companion object {
//        fun fieldBuilder(): FieldExtBase{
//            var newArgs = args.toList()
//            var argExtension: Int
//            try {
//                argExtension = (args[0] as FieldExtBase).extension
//                (args[1] as FieldExtBase).extension
//            }catch (e: Exception){
//                if(args.size != 2) throw(Exception("Invalid number of arguments"))
//                argExtension = 1
//                newArgs = args.map { Fq(Q, it.Q) }
//            }
//            if(argExtension != 1) {
//                if(args.size != cls.embedding) throw Exception("Invalid number of arguments")
//                for(arg in newArgs) {
//                    if(arg.extension != argExtension) throw Exception("Argument with invalid extension")
//                }
//            }
//            if(newArgs.any { it.extension != cls.basefield.extension } ) throw Exception("newargs has invalid extension")
//
//            // TODO: Test whether this is correct interpretation of python code
//            // TODO: Might just pass in Q instead of newArgs.first().Q
//            val ret = fieldBuilder(cls, newArgs.first().Q, *newArgs.drop(1).toTypedArray())
//            return ret.copy(Q = Q)
//        }
    }
}
