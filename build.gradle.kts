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
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val nonJvmMain by creating { dependsOn(commonMain) }
        val nonJvmTest by creating { dependsOn(commonTest) }
        val jsMain by getting { dependsOn(nonJvmMain) }
        val jsTest by getting { dependsOn(nonJvmTest) }
        val nativeMain by creating { dependsOn(nonJvmMain) }
        val nativeTest by creating { dependsOn(nonJvmTest) }
        val nix64Main by creating { dependsOn(nativeMain) }
        val nix64Test by creating { dependsOn(nativeTest) }
        val nix32Main by creating { dependsOn(nativeMain) }
        val nix32Test by creating { dependsOn(nativeTest) }
        val appleMain by creating { dependsOn(nativeMain) }
        val appleTest by creating { dependsOn(nativeTest) }
        val apple64Main by creating {
            dependsOn(appleMain)
            dependsOn(nix64Main)
        }
        val apple64Test by creating {
            dependsOn(appleTest)
            dependsOn(nix64Test)
        }
        val apple32Main by creating {
            dependsOn(appleMain)
            dependsOn(nix32Main)
        }
        val apple32Test by creating {
            dependsOn(appleTest)
            dependsOn(nix32Test)
        }
        val iosX64Main by getting { dependsOn(apple64Main) }
        val iosX64Test by getting { dependsOn(apple64Test) }
        val iosArm64Main by getting { dependsOn(apple64Main) }
        val iosArm64Test by getting { dependsOn(apple64Test) }
        val macosX64Main by getting { dependsOn(apple64Main) }
        val macosX64Test by getting { dependsOn(apple64Test) }
        val macosArm64Main by getting { dependsOn(apple64Main) }
        val macosArm64Test by getting { dependsOn(apple64Test) }
        val iosArm32Main by getting { dependsOn(apple32Main) }
        val iosArm32Test by getting { dependsOn(apple32Test) }
        val iosSimulatorArm64Main by getting { dependsOn(apple64Main) }
        val iosSimulatorArm64Test by getting { dependsOn(apple64Test) }
        val watchosArm32Main by getting { dependsOn(apple32Main) }
        val watchosArm32Test by getting { dependsOn(apple32Test) }
        val watchosArm64Main by getting { dependsOn(apple64Main) }
        val watchosArm64Test by getting { dependsOn(apple64Test) }
        val watchosX64Main by getting { dependsOn(apple64Main) }
        val watchosX64Test by getting { dependsOn(apple64Test) }
        val watchosX86Main by getting { dependsOn(apple32Main) }
        val watchosX86Test by getting { dependsOn(apple32Test) }
        val watchosSimulatorArm64Main by getting { dependsOn(apple64Main) }
        val watchosSimulatorArm64Test by getting { dependsOn(apple64Test) }
        val tvosArm64Main by getting { dependsOn(apple64Main) }
        val tvosArm64Test by getting { dependsOn(apple64Test) }
        val tvosX64Main by getting { dependsOn(apple64Main) }
        val tvosX64Test by getting { dependsOn(apple64Test) }
        val tvosSimulatorArm64Main by getting { dependsOn(apple64Main) }
        val tvosSimulatorArm64Test by getting { dependsOn(apple64Test) }

        if (HostManager.hostIsMingw || HostManager.hostIsMac) {
            val mingwMain by creating { dependsOn(nativeMain) }
            val mingwTest by creating { dependsOn(nativeTest) }
            val mingwX64Main by getting { dependsOn(mingwMain) }
            val mingwX64Test by getting { dependsOn(mingwTest) }
        }

        if (HostManager.hostIsLinux || HostManager.hostIsMac) {
            val linuxX64Main by getting { dependsOn(nix64Main) }
            val linuxX64Test by getting { dependsOn(nix64Test) }
            val linuxArm32HfpMain by getting { dependsOn(nix32Main) }
            val linuxArm32HfpTest by getting { dependsOn(nix32Test) }
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

