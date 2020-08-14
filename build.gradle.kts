import org.jetbrains.kotlin.konan.target.HostManager

plugins {
    kotlin("multiplatform") version "1.4.0"
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
            macosX64()
            iosX64()
            iosArm64()
            iosArm32()
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
                implementation(kotlin("stdlib-common"))
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
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

            val appleMain32SourceDirs = listOf(
                "src/appleMain/kotlin"
            ) + nix32MainSourceDirs

            val appleMain64SourceDirs = listOf(
                "src/appleMain/kotlin"
            ) + nix64MainSourceDirs

            val macosX64Main by getting { kotlin.srcDirs(appleMain64SourceDirs) }
            val macosX64Test by getting { kotlin.srcDir("src/appleTest/kotlin") }
            val iosArm64Main by getting { kotlin.srcDirs(appleMain64SourceDirs) }
            val iosArm64Test by getting { kotlin.srcDir("src/appleTest/kotlin") }
            val iosArm32Main by getting { kotlin.srcDirs(appleMain32SourceDirs) }
            val iosArm32Test by getting { kotlin.srcDir("src/appleTest/kotlin") }
            val iosX64Main by getting {kotlin.srcDirs(appleMain64SourceDirs) }
            val iosX64Test by getting { kotlin.srcDir("src/appleTest/kotlin") }
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
    targets.all {
        compilations.all {
            kotlinOptions.allWarningsAsErrors = true
        }
        compilations.getByName("main").kotlinOptions.freeCompilerArgs += listOf("-Xexplicit-api=strict")
    }
}

val ktlintConfig by configurations.creating

dependencies {
    ktlintConfig("com.pinterest:ktlint:0.37.2")
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

apply(from = "publish.gradle")

/// Generate PROJECT_DIR_ROOT for referencing local mocks in tests

val projectDirGenRoot = "$buildDir/generated/projectdir/kotlin"
val generateProjDirValTask = tasks.register("generateProjectDirectoryVal") {
    doLast {
        mkdir(projectDirGenRoot)
        val projDirFile = File("$projectDirGenRoot/projdir.kt")
        projDirFile.writeText("")
        projDirFile.appendText("""
            |package com.benasher44.uuid
            |
            |import kotlin.native.concurrent.SharedImmutable
            |
            |@SharedImmutable
            |internal const val PROJECT_DIR_ROOT = ""${'"'}${projectDir.absolutePath}""${'"'}
            |
        """.trimMargin())
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
