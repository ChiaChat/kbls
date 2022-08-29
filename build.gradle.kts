plugins {
    kotlin("multiplatform") version "1.7.10"
    id("convention.publication")
}

group = "org.chiachat"
version = "1.0.1"

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    js(IR) {
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
    }
    ios {
        binaries {
            framework {
                baseName = "kbls"
            }
        }
    }
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    when {
        hostOs == "Mac OS X" -> {
            macosX64()
            macosArm64()
            iosArm64()
            iosX64()
        }
        hostOs == "Linux" -> linuxX64()
        isMingwX64 -> mingwX64()
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    sourceSets {
        val commonMain by getting {
            dependencies() {
                implementation("com.soywiz.korlibs.krypto:krypto:3.0.0")
                implementation("com.ionspin.kotlin:bignum:0.3.6")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting
        val jvmTest by getting
        val jsMain by getting
        val jsTest by getting
        val iosMain by getting
    }

    tasks {
        withType<Test>() {
            minHeapSize = "512m"
            maxHeapSize = "1024m"
        }
    }
}
