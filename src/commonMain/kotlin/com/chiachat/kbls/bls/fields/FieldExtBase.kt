package com.chiachat.kbls.bls.fields

import com.ionspin.kotlin.bignum.integer.BigInteger

class FieldExtBase(
    val root: Field,
    val extension: Int,
    val embedding: Int,
    val basefield: Field,
    val Q: BigInteger,
) {

    operator fun unaryMinus(){
        var ret = fieldBuilder(this, )
    }


    companion object {
        fun fieldBuilder(cls: FieldExtBase, Q: BigInteger, vararg args: Any): FieldExtBase{
            var newArgs = args.toList()
            var argExtension: Int
            try {
                argExtension = (args[0] as FieldExtBase).extension
                (args[1] as FieldExtBase).extension
            }catch (e: Exception){
                if(args.size != 2) throw(Exception("Invalid number of arguments"))
                argExtension = 1
                newArgs = args.map { Fq(Q, it.Q) }
            }
            if(argExtension != 1) {
                if(args.size != cls.embedding) throw Exception("Invalid number of arguments")
                for(arg in newArgs) {
                    if(arg.extension != argExtension) throw Exception("Argument with invalid extension")
                }
            }
            if(newArgs.any { it.extension != cls.basefield.extension } ) throw Exception("newargs has invalid extension")

            // TODO: Test whether this is correct interpretation of python code
            // TODO: Might just pass in Q instead of newArgs.first().Q
            val ret = fieldBuilder(cls, newArgs.first().Q, *newArgs.drop(1).toTypedArray())
            return ret.copy(Q = Q)
        }
    }
}