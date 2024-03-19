import org.jetbrains.kotlin.gradle.tasks.AbstractKotlinCompileTool
import org.jetbrains.kotlin.gradle.tasks.KotlinNativeCompile
import org.jetbrains.kotlin.konan.target.HostManager

plugins {
    kotlin("multiplatform") version "1.9.23"
    id("org.jetbrains.dokka") version "1.8.20"
    id("maven-publish")
    id("signing")
}

repositories {
    mavenCentral()
}

tasks.dokkaHtml {
    dokkaSourceSets {
        configureEach {
            samples.from("src/commonTest/kotlin")
        }
    }
}

kotlin {
    targets {
        js(IR) {
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
        jvm {
            compilations.all {
                kotlinOptions {
                    jvmTarget = "1.8"
                    apiVersion = "1.7"
                    languageVersion = "1.9"
                }
            }
        }
        wasmJs {
            d8()
        }
        macosX64()
        macosArm64()
        iosX64()
        iosArm64()
        iosSimulatorArm64()
        watchosArm32()
        watchosArm64()
        watchosX64()
        watchosSimulatorArm64()
        watchosDeviceArm64()
        tvosArm64()
        tvosX64()
        tvosSimulatorArm64()
        mingwX64 {
            binaries.findTest(DEBUG)!!.linkerOpts = mutableListOf("-Wl,--subsystem,windows")
        }
        linuxX64()
        linuxArm64()
        wasmWasi {
            nodejs {
                testTask {
                    // skip on windows, since there doesn't seem to be a canary node version for use on windows
                    this.enabled = !HostManager.hostIsMingw
                }
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.20")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val nonJvmMain by creating { dependsOn(commonMain) }
        val nonJvmTest by creating { dependsOn(commonTest) }
        val jsMain by getting { dependsOn(nonJvmMain) }
        val wasmJsMain by getting { dependsOn(nonJvmMain) }
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
        val iosSimulatorArm64Main by getting { dependsOn(apple64Main) }
        val iosSimulatorArm64Test by getting { dependsOn(apple64Test) }
        val watchosArm32Main by getting { dependsOn(apple32Main) }
        val watchosArm32Test by getting { dependsOn(apple32Test) }
        val watchosArm64Main by getting { dependsOn(apple64Main) }
        val watchosArm64Test by getting { dependsOn(apple64Test) }
        val watchosX64Main by getting { dependsOn(apple64Main) }
        val watchosX64Test by getting { dependsOn(apple64Test) }
        val watchosSimulatorArm64Main by getting { dependsOn(apple64Main) }
        val watchosSimulatorArm64Test by getting { dependsOn(apple64Test) }
        val watchosDeviceArm64Main by getting { dependsOn(apple64Main) }
        val watchosDeviceArm64Test by getting { dependsOn(apple64Test) }
        val tvosArm64Main by getting { dependsOn(apple64Main) }
        val tvosArm64Test by getting { dependsOn(apple64Test) }
        val tvosX64Main by getting { dependsOn(apple64Main) }
        val tvosX64Test by getting { dependsOn(apple64Test) }
        val tvosSimulatorArm64Main by getting { dependsOn(apple64Main) }
        val tvosSimulatorArm64Test by getting { dependsOn(apple64Test) }
        val mingwMain by creating { dependsOn(nativeMain) }
        val mingwTest by creating { dependsOn(nativeTest) }
        val mingwX64Main by getting { dependsOn(mingwMain) }
        val mingwX64Test by getting { dependsOn(mingwTest) }
        val linuxX64Main by getting { dependsOn(nix64Main) }
        val linuxX64Test by getting { dependsOn(nix64Test) }
        val linuxArm64Main by getting { dependsOn(nix64Main) }
        val linuxArm64Test by getting { dependsOn(nix64Test) }
        val wasmWasiMain by getting { dependsOn(nonJvmMain) }
    }
}

kotlin {
    explicitApi()
}

tasks.withType<KotlinNativeCompile>().configureEach {
    compilerOptions.freeCompilerArgs.add("-opt-in=kotlinx.cinterop.ExperimentalForeignApi")
}

if (!HostManager.hostIsMingw) {
    // doesn't seem to be a canary version for use on windows
    plugins.withType<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin> {
        the<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension>().download = true
        the<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension>().nodeVersion =
            "21.0.0-v8-canary20231024d0ddc81258"
        the<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension>().nodeDownloadBaseUrl =
            "https://nodejs.org/download/v8-canary"
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.targets.js.npm.tasks.KotlinNpmInstallTask>().configureEach {
    args.add("--ignore-engines")
}

val ktlintConfig by configurations.creating

dependencies {
    ktlintConfig("com.pinterest:ktlint:0.50.0")
}

val ktlint by tasks.registering(JavaExec::class) {
    group = "verification"
    description = "Check Kotlin code style."
    classpath = ktlintConfig
    mainClass.set("com.pinterest.ktlint.Main")
    args = listOf("src/**/*.kt")
}

val ktlintformat by tasks.registering(JavaExec::class) {
    group = "formatting"
    description = "Fix Kotlin code style deviations."
    classpath = ktlintConfig
    mainClass.set("com.pinterest.ktlint.Main")
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
            """.trimMargin(),
        )
    }
}

kotlin.sourceSets.named("commonTest") {
    this.kotlin.srcDir(projectDirGenRoot)
}

// Ensure this runs before any test compile task
tasks.withType<AbstractCompile>().configureEach {
    if (name.lowercase().contains("test")) {
        dependsOn(generateProjDirValTask)
    }
}

tasks.withType<AbstractKotlinCompileTool<*>>().configureEach {
    if (name.lowercase().contains("test")) {
        dependsOn(generateProjDirValTask)
    }
}
