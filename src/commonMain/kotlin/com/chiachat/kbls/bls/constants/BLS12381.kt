package com.chiachat.kbls.bls.constants

import com.chiachat.kbls.bls.ec.EC
import com.chiachat.kbls.bls.ec.ECTwist
import com.chiachat.kbls.bls.fields.Fq
import com.chiachat.kbls.bls.fields.Fq2
import com.ionspin.kotlin.bignum.integer.BigInteger

object BLS12381 {
    val x = BigInteger.parseString("-d201000000010000", 16)
    val q =
        BigInteger.parseString(
            "1a0111ea397fe69a4b1ba7b6434bacd764774b84f38512bf6730d2a0f6b0f6241eabfffeb153ffffb9feffffffffaaab",
            16
        )
    val a = Fq(q, BigInteger.parseString("0", 10))
    val b = Fq(q, BigInteger.parseString("4", 10))
    val aTwist = Fq2(q, Fq(q, BigInteger.parseString("0", 10)), Fq(q, BigInteger.parseString("0", 10)))
    val bTwist = Fq2(q, Fq(q, BigInteger.parseString("4", 10)), Fq(q, BigInteger.parseString("4", 10)))

    val gx = Fq(
        q,
        BigInteger.parseString(
            "17f1d3a73197d7942695638c4fa9ac0fc3688c4f9774b905a14e3a3f171bac586c55e83ff97a1aeffb3af00adb22c6bb",
            16
        )
    )

    val gy = Fq(
        q,
        BigInteger.parseString(
            "08b3f481e3aaa0f1a09e30ed741d8ae4fcf5e095d5d00af600db18cb2c04b3edd03cc744a2888ae40caa232946c5e7e1",
            16
        )
    )

    val g2x = Fq2(
        q,
        Fq(
            q,
            BigInteger.parseString(
                "352701069587466618187139116011060144890029952792775240219908644239793785735715026873347600343865175952761926303160",
                10
            )
        ),
        Fq(
            q,
            BigInteger.parseString(
                "3059144344244213709971259814753781636986470325476647558659373206291635324768958432433509563104347017837885763365758",
                10
            )
        )
    )

    val g2y = Fq2(
        q,
        Fq(
            q,
            BigInteger.parseString(
                "1985150602287291935568054521177171638300868978215655730859378665066344726373823718423869104263333984641494340347905",
                10
            )
        ),
        Fq(
            q,
            BigInteger.parseString(
                "927553665492332455747201965776037880757740193453592970025027978793976877002675564980949289727957565575433344219582",
                10
            )
        )
    )

    val n =
        BigInteger.parseString("73eda753299d7d483339d80809a1d80553bda402fffe5bfeffffffff00000001", 16)
    val h = BigInteger.parseString("396c8c005555e1568c00aaab0000aaab", 16)
    val hEff =
        BigInteger.parseString(
            "bc69f08f2ee75b3584c6a0ea91b352888e2a8e9145ad7689986ff031508ffe1329c2f178731db956d82bf015d1212b02ec0ec69d7477c1ae954cbc06689f6a359894c0adebbf6b4e8020005aaa95551",
            16
        )
    val k = BigInteger.parseString("12", 10)
    val sqrtN3 =
        BigInteger.parseString(
            "1586958781458431025242759403266842894121773480562120986020912974854563298150952611241517463240701",
            10
        )
    val sqrtN3m1o2 =
        BigInteger.parseString(
            "793479390729215512621379701633421447060886740281060493010456487427281649075476305620758731620350",
            10
        )

    val defaultEc = EC(
        q,
        a,
        b,
        gx,
        gy,
        g2x,
        g2y,
        n,
        h,
        x,
        k,
        sqrtN3,
        sqrtN3m1o2
    )

    val defaultEcTwist = ECTwist(
        q,
        aTwist,
        bTwist,
        gx,
        gy,
        g2x,
        g2y,
        n,
        hEff,
        x,
        k,
        sqrtN3,
        sqrtN3m1o2
    )
}
