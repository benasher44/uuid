import org.jetbrains.kotlin.gradle.tasks.KotlinNativeLink
import org.jetbrains.kotlin.konan.target.HostManager

plugins {
    kotlin("multiplatform") version "1.3.50"
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
            macosX64("macos")
            iosX64("iosSim")
            iosArm64("iosDevice64")
            iosArm32("iosDevice32")
        }
        if (HostManager.hostIsMingw) {
            mingwX64("mingw") {
                binaries.findTest(DEBUG)!!.linkerOpts = mutableListOf("-Wl,--subsystem,windows")
            }
        }
        if (HostManager.hostIsLinux) {
            linuxX64("linux64")
            linuxArm32Hfp("linux32")
        }
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-common")
            }
        }
        commonTest {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test-common")
                implementation("org.jetbrains.kotlin:kotlin-test-annotations-common")
            }
        }

        val nix64MainSourceSets = listOf(
            "src/nativeMain/kotlin",
            "src/nix64Main/kotlin"
        )

        val nix32MainSourceSets = listOf(
            "src/nativeMain/kotlin",
            "src/nix32Main/kotlin"
        )

        if (HostManager.hostIsMac) {
            val jvmMain by getting {
                dependencies {
                    implementation("org.jetbrains.kotlin:kotlin-stdlib")
                }
            }
            val jvmTest by getting {
                dependencies {
                    implementation("org.jetbrains.kotlin:kotlin-test")
                    implementation("org.jetbrains.kotlin:kotlin-test-junit")
                }
            }
            val jsMain by getting {
                dependencies {
                    implementation("org.jetbrains.kotlin:kotlin-stdlib-js")
                }
            }
            val jsTest by getting {
                dependencies {
                    implementation("org.jetbrains.kotlin:kotlin-test-js")
                }
            }

            val macosMain by getting { kotlin.srcDirs(nix64MainSourceSets) }
            val macosTest by getting { kotlin.srcDir("src/cocoaTest/kotlin") }
            val iosDevice64Main by getting { kotlin.srcDirs(nix64MainSourceSets) }
            val iosDevice64Test by getting { kotlin.srcDir("src/cocoaTest/kotlin") }
            val iosDevice32Main by getting { kotlin.srcDirs(nix32MainSourceSets) }
            val iosDevice32Test by getting { kotlin.srcDir("src/cocoaTest/kotlin") }
            val iosSimMain by getting { kotlin.srcDirs(nix64MainSourceSets) }
            val iosSimTest by getting { kotlin.srcDir("src/cocoaTest/kotlin") }
        }
        if (HostManager.hostIsMingw) {
            val mingwMain by getting {
                kotlin.srcDirs(
                    listOf(
                        "src/nativeMain/kotlin",
                        "src/mingwMain/kotlin"
                    )
                )
            }
        }
        if (HostManager.hostIsLinux) {
            val linux64Main by getting { kotlin.srcDirs(nix64MainSourceSets) }
            val linux32Main by getting { kotlin.srcDirs(nix32MainSourceSets) }
        }
    }
}

kotlin {
    targets.all {
        compilations.all {
            kotlinOptions.allWarningsAsErrors = true
        }
    }
}

val ktlintConfig by configurations.creating

dependencies {
    ktlintConfig("com.pinterest:ktlint:0.34.2")
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

val checkTask = tasks.named("check")
checkTask.configure {
    dependsOn(ktlint)
}

if (HostManager.hostIsMac) {
    val linkDebugTestIosSim by tasks.getting(KotlinNativeLink::class)
    val testIosSim by tasks.registering(Exec::class) {
        group = "verification"
        dependsOn(linkDebugTestIosSim)
        executable = "xcrun"
        setArgs(listOf(
            "simctl",
            "spawn",
            "iPad Air 2",
            linkDebugTestIosSim.outputFile.get()
        ))
    }

    checkTask.configure {
        dependsOn(testIosSim)
    }
}

apply(from = "publish.gradle")
