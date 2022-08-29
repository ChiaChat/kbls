# KBLS

KBLS is a kotlin multiplatform BLS12-381 implementation built for creating cross platform chia applications.

The currently supported platforms are:

* jvm (android/desktop)
* javascript
* linuxX64
* MinGWX64
* MacosX64
* MacosArm64
* iosX64
* iosArm64

This library is still early and in development, not all features are tested let alone audited.

Below is a list of features which are tested & have unit tests/examples to look at (see
src/commonTest/kotlin/chiachat/kbls)

* AugSchemeMPL, BasicSchemeMPL, PopSchemeMPL
    * Generate private keys
    * Generate public keys
    * Derive child private/public keys
    * Sign messages w/ private keys
    * verify signed messages
    * create aggregate signatures
    * verify aggregate signatures

# Getting Started

To get started using this library, just add the following dependency to your commonMain sourceset dependencies

```
sourceSets {
        val commonMain by getting {
            dependencies() {
                implementation("org.chiachat:kbls:1.0.2")
            }
        }
    }
```

Here are some examples of using a the library (more in depth examples can be found in the unit tests)

```
    // Private key seed, using all 0's as an example. Normally you'd take a 24 word phrase and encode it.
    val seed = UByteArray(32).also { it.fill(0.toUByte()) }
    val msg = "hello world".encodeToByteArray()
    val secretKey = BasicSchemeMPL.keyGen(seed1)
    val publicKey = sk.getG1()
    val sig = BasicSchemeMPL.sign(sk1, msg1)
    val valid = if(BasicSchemeMPL.verify(publicKey, msg, sig)) "Signature is valid!!" else "Invalid signature"
    println(valid)
```

# Building

To build this project simply run `./gradlew build` or `./gradlew publishToMavenLocal`

To run the unit tests for all available platforms run `./gradlew test`

# Credits

Huge thanks to Rigidity for providing a javascript implementation to reference and cross-check against the chia python implementation
https://github.com/Rigidity/bls-signatures