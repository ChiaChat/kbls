package com.chiachat.kbls.bls.constants

import com.chiachat.kbls.bech32.toHex
import com.chiachat.kbls.bls.constants.BLS12381.q
import com.chiachat.kbls.bls.fields.Fq
import com.chiachat.kbls.bls.fields.Fq2
import com.chiachat.kbls.bls.util.ONE
import com.chiachat.kbls.bls.util.ZERO

object Iso {
    val xnum = arrayOf(
        Fq2(
            q,
            Fq(
                q,
                "5c759507e8e333ebb5b7a9a47d7ed8532c52d39fd3a042a88b58423c50ae15d5c2638e343d9c71c6238aaaaaaaa97d6".toHex().bigInt
            ),
            Fq(
                q,
                "5c759507e8e333ebb5b7a9a47d7ed8532c52d39fd3a042a88b58423c50ae15d5c2638e343d9c71c6238aaaaaaaa97d6".toHex().bigInt
            )
        ),
        Fq2(
            q,
            Fq(q, ZERO),
            Fq(
                q,
                "11560bf17baa99bc32126fced787c88f984f87adf7ae0c7f9a208c6b4f20a4181472aaa9cb8d555526a9ffffffffc71a".toHex().bigInt
            )
        ),
        Fq2(
            q,
            Fq(
                q,
                "11560bf17baa99bc32126fced787c88f984f87adf7ae0c7f9a208c6b4f20a4181472aaa9cb8d555526a9ffffffffc71e".toHex().bigInt
            ),
            Fq(
                q,
                "8ab05f8bdd54cde190937e76bc3e447cc27c3d6fbd7063fcd104635a790520c0a395554e5c6aaaa9354ffffffffe38d".toHex().bigInt
            )
        ),
        Fq2(
            q,
            Fq(
                q,
                "171d6541fa38ccfaed6dea691f5fb614cb14b4e7f4e810aa22d6108f142b85757098e38d0f671c7188e2aaaaaaaa5ed1".toHex().bigInt
            ),
            Fq(q, ZERO)
        )
    )

    val xden = arrayOf(
        Fq2(
            q,
            Fq(q, ZERO),
            Fq(
                q,
                "1a0111ea397fe69a4b1ba7b6434bacd764774b84f38512bf6730d2a0f6b0f6241eabfffeb153ffffb9feffffffffaa63".toHex().bigInt
            )
        ),
        Fq2(
            q,
            Fq(q, "c".toHex().bigInt),
            Fq(
                q,
                "1a0111ea397fe69a4b1ba7b6434bacd764774b84f38512bf6730d2a0f6b0f6241eabfffeb153ffffb9feffffffffaa9f".toHex().bigInt
            )
        ),
        Fq2(q, Fq(q, ONE), Fq(q, ZERO))
    )

    val ynum = arrayOf(
        Fq2(
            q,
            Fq(
                q,
                "1530477c7ab4113b59a4c18b076d11930f7da5d4a07f649bf54439d87d27e500fc8c25ebf8c92f6812cfc71c71c6d706".toHex().bigInt
            ),
            Fq(
                q,
                "1530477c7ab4113b59a4c18b076d11930f7da5d4a07f649bf54439d87d27e500fc8c25ebf8c92f6812cfc71c71c6d706".toHex().bigInt
            )
        ),
        Fq2(
            q,
            Fq(q, ZERO),
            Fq(
                q,
                "5c759507e8e333ebb5b7a9a47d7ed8532c52d39fd3a042a88b58423c50ae15d5c2638e343d9c71c6238aaaaaaaa97be".toHex().bigInt
            )
        ),
        Fq2(
            q,
            Fq(
                q,
                "11560bf17baa99bc32126fced787c88f984f87adf7ae0c7f9a208c6b4f20a4181472aaa9cb8d555526a9ffffffffc71c".toHex().bigInt
            ),
            Fq(
                q,
                "8ab05f8bdd54cde190937e76bc3e447cc27c3d6fbd7063fcd104635a790520c0a395554e5c6aaaa9354ffffffffe38f".toHex().bigInt
            )
        ),
        Fq2(
            q,
            Fq(
                q,
                "124c9ad43b6cf79bfbf7043de3811ad0761b0f37a1e26286b0e977c69aa274524e79097a56dc4bd9e1b371c71c718b10".toHex().bigInt
            ),
            Fq(q, ZERO)
        )
    )

    val yden = arrayOf(
        Fq2(
            q,
            Fq(
                q,
                "1a0111ea397fe69a4b1ba7b6434bacd764774b84f38512bf6730d2a0f6b0f6241eabfffeb153ffffb9feffffffffa8fb".toHex().bigInt
            ),
            Fq(
                q,
                "1a0111ea397fe69a4b1ba7b6434bacd764774b84f38512bf6730d2a0f6b0f6241eabfffeb153ffffb9feffffffffa8fb".toHex().bigInt
            )
        ),
        Fq2(
            q,
            Fq(q, ZERO),
            Fq(
                q,
                "1a0111ea397fe69a4b1ba7b6434bacd764774b84f38512bf6730d2a0f6b0f6241eabfffeb153ffffb9feffffffffa9d3".toHex().bigInt
            )
        ),
        Fq2(
            q,
            Fq(q, "12".toHex().bigInt),
            Fq(
                q,
                "1a0111ea397fe69a4b1ba7b6434bacd764774b84f38512bf6730d2a0f6b0f6241eabfffeb153ffffb9feffffffffaa99".toHex().bigInt
            )
        ),
        Fq2(q, Fq(q, ONE), Fq(q, ZERO))
    )
}
