import org.jetbrains.kotlin.gradle.tasks.KotlinNativeLink
import org.jetbrains.kotlin.konan.target.HostManager

plugins {
    kotlin("multiplatform") version "1.3.61"
    id("org.jetbrains.dokka") version "0.9.18"
    id("maven-publish")
    id("signing")
}

repositories {
    jcenter()
}

tasks.dokka {
    samples = listOf("src/commonTest/kotlin")
}

kotlin {
    targets.all {
        compilations.all {
            kotlinOptions.allWarningsAsErrors = true
        }
    }

    targets {
        if (HostManager.hostIsMac) {
            jvm {
                // Intentionally left blank.
            }
            js {
                compilations.all {
                    kotlinOptions {
                        sourceMap = true
                        moduleKind = "umd"
                        metaInfo = true
                    }
                }
            }
            macosX64()
            iosX64()
            iosArm64()
            iosArm32()
        }
        if (HostManager.hostIsMingw || HostManager.hostIsMac) {
            mingwX64() {
                binaries.findTest(DEBUG)!!.linkerOpts = mutableListOf("-Wl,--subsystem,windows")
            }
        }
        if (HostManager.hostIsLinux || HostManager.hostIsMac) {
            linuxX64()
            linuxArm32Hfp()
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("stdlib-common"))
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val nix64MainSourceSets = listOf(
            "src/nonJvmMain/kotlin",
            "src/nativeMain/kotlin",
            "src/nix64Main/kotlin"
        )

        val nix32MainSourceSets = listOf(
            "src/nonJvmMain/kotlin",
            "src/nativeMain/kotlin",
            "src/nix32Main/kotlin"
        )

        if (HostManager.hostIsMac) {
            val jvmMain by getting {
                dependencies {
                    implementation(kotlin("stdlib"))
                }
            }
            val jvmTest by getting {
                dependencies {
                    implementation(kotlin("test-junit"))
                }
            }
            val jsMain by getting {
                dependencies {
                    implementation(kotlin("stdlib-js"))
                }
                kotlin.srcDir("src/nonJvmMain/kotlin")
            }
            val jsTest by getting {
                dependencies {
                    implementation(kotlin("test-js"))
                }
            }

            val macosX64Main by getting { kotlin.srcDirs(nix64MainSourceSets) }
            val macosX64Test by getting { kotlin.srcDir("src/cocoaTest/kotlin") }
            val iosArm64Main by getting { kotlin.srcDirs(nix64MainSourceSets) }
            val iosArm64Test by getting { kotlin.srcDir("src/cocoaTest/kotlin") }
            val iosArm32Main by getting { kotlin.srcDirs(nix32MainSourceSets) }
            val iosArm32Test by getting { kotlin.srcDir("src/cocoaTest/kotlin") }
            val iosX64Main by getting { kotlin.srcDirs(nix64MainSourceSets) }
            val iosX64Test by getting { kotlin.srcDir("src/cocoaTest/kotlin") }
        }
        if (HostManager.hostIsMingw || HostManager.hostIsMac) {
            val mingwX64Main by getting {
                kotlin.srcDirs(
                    listOf(
                        "src/nonJvmMain/kotlin",
                        "src/nativeMain/kotlin",
                        "src/mingwMain/kotlin"
                    )
                )
            }
        }
        if (HostManager.hostIsLinux || HostManager.hostIsMac) {
            val linuxX64Main by getting { kotlin.srcDirs(nix64MainSourceSets) }
            val linuxArm32HfpMain by getting { kotlin.srcDirs(nix32MainSourceSets) }
        }
    }
}

val ktlintConfig by configurations.creating

dependencies {
    ktlintConfig("com.pinterest:ktlint:0.35.0")
}

val ktlint by tasks.registering(JavaExec::class) {
    group = "verification"
    description = "Check Kotlin code style."
    classpath = ktlintConfig
    main = "com.pinterest.ktlint.Main"
    args = listOf("src/**/*.kt")
}

val ktlintformat by tasks.registering(JavaExec::class) {
    group = "formatting"
    description = "Fix Kotlin code style deviations."
    classpath = ktlintConfig
    main = "com.pinterest.ktlint.Main"
    args = listOf("-F", "src/**/*.kt")
}

tasks.check {
    dependsOn(ktlint)
}

if (HostManager.hostIsMac) {
    val linkDebugTestIosX64 by tasks.getting(KotlinNativeLink::class)
    val testIosSim by tasks.registering(Exec::class) {
        group = "verification"
        dependsOn(linkDebugTestIosX64)
        commandLine(
            "xcrun",
            "simctl",
            "spawn",
            "-s",
            "iPad Air 2",
            linkDebugTestIosX64.outputFile.get()
        )
    }

    tasks.check {
        dependsOn(testIosSim)
    }
}

apply(from = "publish.gradle")
