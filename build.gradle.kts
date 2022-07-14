import org.jetbrains.kotlin.gradle.tasks.AbstractKotlinCompileTool
import org.jetbrains.kotlin.konan.target.HostManager

plugins {
    kotlin("multiplatform") version "1.7.10"
    id("org.jetbrains.dokka") version "0.9.18"
    id("maven-publish")
    id("signing")
}

repositories {
    mavenCentral()
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
            js(BOTH) {
                compilations.all {
                    kotlinOptions {
                        sourceMap = true
                        moduleKind = "umd"
                        metaInfo = true
                    }
                }
                browser()
                nodejs()
            }
            macosX64()
            macosArm64()
            iosX64()
            iosArm64()
            iosArm32()
            iosSimulatorArm64()
            watchosArm32()
            watchosArm64()
            watchosX86()
            watchosX64()
            watchosSimulatorArm64()
            tvosArm64()
            tvosX64()
            tvosSimulatorArm64()
        }
        if (HostManager.hostIsMingw || HostManager.hostIsMac) {
            mingwX64 {
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
                // can remove this once https://youtrack.jetbrains.com/issue/KT-40333 is fixed
                implementation(kotlin("stdlib-common"))
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val nix64MainSourceDirs = listOf(
            "src/nonJvmMain/kotlin",
            "src/nativeMain/kotlin",
            "src/nix64Main/kotlin"
        )

        val nix32MainSourceDirs = listOf(
            "src/nonJvmMain/kotlin",
            "src/nativeMain/kotlin",
            "src/nix32Main/kotlin"
        )

        if (HostManager.hostIsMac) {
            val jsMain by getting {
                kotlin.srcDir("src/nonJvmMain/kotlin")
            }

            val appleMain32SourceDirs = listOf(
                "src/appleMain/kotlin"
            ) + nix32MainSourceDirs

            val appleMain64SourceDirs = listOf(
                "src/appleMain/kotlin"
            ) + nix64MainSourceDirs

            val macosX64Main by getting { kotlin.srcDirs(appleMain64SourceDirs) }
            val macosX64Test by getting { kotlin.srcDir("src/appleTest/kotlin") }
            val macosArm64Main by getting { kotlin.srcDirs(appleMain64SourceDirs) }
            val macosArm64Test by getting { kotlin.srcDir("src/appleTest/kotlin") }
            val iosArm64Main by getting { kotlin.srcDirs(appleMain64SourceDirs) }
            val iosArm64Test by getting { kotlin.srcDir("src/appleTest/kotlin") }
            val iosArm32Main by getting { kotlin.srcDirs(appleMain32SourceDirs) }
            val iosArm32Test by getting { kotlin.srcDir("src/appleTest/kotlin") }
            val iosX64Main by getting { kotlin.srcDirs(appleMain64SourceDirs) }
            val iosX64Test by getting { kotlin.srcDir("src/appleTest/kotlin") }
            val iosSimulatorArm64Main by getting { kotlin.srcDirs(appleMain64SourceDirs) }
            val iosSimulatorArm64Test by getting { kotlin.srcDir("src/appleTest/kotlin") }
            val watchosArm32Main by getting { kotlin.srcDirs(appleMain32SourceDirs) }
            val watchosArm32Test by getting { kotlin.srcDir("src/appleTest/kotlin") }
            val watchosArm64Main by getting { kotlin.srcDirs(appleMain64SourceDirs) }
            val watchosArm64Test by getting { kotlin.srcDir("src/appleTest/kotlin") }
            val watchosX64Main by getting { kotlin.srcDirs(appleMain64SourceDirs) }
            val watchosX64Test by getting { kotlin.srcDir("src/appleTest/kotlin") }
            val watchosX86Main by getting { kotlin.srcDirs(appleMain32SourceDirs) }
            val watchosX86Test by getting { kotlin.srcDir("src/appleTest/kotlin") }
            val watchosSimulatorArm64Main by getting { kotlin.srcDirs(appleMain64SourceDirs) }
            val watchosSimulatorArm64Test by getting { kotlin.srcDir("src/appleTest/kotlin") }
            val tvosArm64Main by getting { kotlin.srcDirs(appleMain64SourceDirs) }
            val tvosArm64Test by getting { kotlin.srcDir("src/appleTest/kotlin") }
            val tvosX64Main by getting { kotlin.srcDirs(appleMain64SourceDirs) }
            val tvosX64Test by getting { kotlin.srcDir("src/appleTest/kotlin") }
            val tvosSimulatorArm64Main by getting { kotlin.srcDirs(appleMain64SourceDirs) }
            val tvosSimulatorArm64Test by getting { kotlin.srcDir("src/appleTest/kotlin") }
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
            val mingwX64Test by getting {
                kotlin.srcDir("src/mingwTest/kotlin")
            }
        }
        if (HostManager.hostIsLinux || HostManager.hostIsMac) {
            val linuxX64Main by getting { kotlin.srcDirs(nix64MainSourceDirs) }
            val linuxArm32HfpMain by getting { kotlin.srcDirs(nix32MainSourceDirs) }
        }
    }
}

kotlin {
    explicitApi()
    targets.all {
        compilations.all {
            // https://youtrack.jetbrains.com/issue/KT-46257
            kotlinOptions.allWarningsAsErrors = HostManager.hostIsMac
        }
    }
}

val ktlintConfig by configurations.creating

dependencies {
    ktlintConfig("com.pinterest:ktlint:0.42.1")
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
    args = listOf("-F", "src/**/*.kt", "*.kts")
}

val checkTask = tasks.named("check")
checkTask.configure {
    dependsOn(ktlint)
}

apply(from = "publish.gradle")

// Generate PROJECT_DIR_ROOT for referencing local mocks in tests

val projectDirGenRoot = "$buildDir/generated/projectdir/kotlin"
val generateProjDirValTask = tasks.register("generateProjectDirectoryVal") {
    doLast {
        mkdir(projectDirGenRoot)
        val projDirFile = File("$projectDirGenRoot/projdir.kt")
        projDirFile.writeText("")
        projDirFile.appendText(
            """
            |package com.benasher44.uuid
            |
            |import kotlin.native.concurrent.SharedImmutable
            |
            |@SharedImmutable
            |internal const val PROJECT_DIR_ROOT = ""${'"'}${projectDir.absolutePath}""${'"'}
            |
        """.trimMargin()
        )
    }
}

kotlin.sourceSets.named("commonTest") {
    this.kotlin.srcDir(projectDirGenRoot)
}

// Ensure this runs before any test compile task
tasks.withType<AbstractCompile>().configureEach {
    if (name.toLowerCase().contains("test")) {
        dependsOn(generateProjDirValTask)
    }
}

tasks.withType<AbstractKotlinCompileTool<*>>().configureEach {
    if (name.toLowerCase().contains("test")) {
        dependsOn(generateProjDirValTask)
    }
}

