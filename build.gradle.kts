/*
 * Copyright (C) 2023 James Yox
 *   http://www.jamesyox.dev
 * Copyright (C) 2017 Richard "Shred" Körber
 *    http://commons.shredzone.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import kotlin.time.Duration.Companion.seconds
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.detekt)
    alias(libs.plugins.dokka)
    alias(libs.plugins.benmanes.versions)
    alias(libs.plugins.kover)
    alias(libs.plugins.binarycompatibility)
    alias(libs.plugins.poko)
    alias(libs.plugins.mavenpublish)
}

group = "dev.jamesyox"
version = libs.versions.current.get()

detekt {
    buildUponDefaultConfig = true // preconfigure defaults
    autoCorrect = true
    config.from(files("$projectDir/detekt/config.yml"))

    dependencies {
        detektPlugins(libs.detekt.formatting)
        detektPlugins(libs.detekt.library)
    }
}

tasks.withType<Detekt> {
    jvmTarget = libs.versions.jvm.get()
}
tasks.withType<DetektCreateBaselineTask> {
    jvmTarget = libs.versions.jvm.get()
}

kotlin {
    explicitApi()
    jvm {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
        }
    }
    js(IR) {
        compilerOptions {
            target.set("es2015")
        }
        binaries.library()
        browser {
            testTask {
                useKarma {
                    useChromium()
                    useFirefox()
                }
            }
        }
        nodejs {
            testTask {
                useMocha {
                    timeout = 30.seconds.inWholeMilliseconds.toString()
                }
            }
        }
    }

    wasmJs {
        binaries.library()
        browser {
            testTask {
                useKarma {
                    useChromium()
                    useFirefox()
                }
            }
        }
        nodejs()
    }

    // Native: https://kotlinlang.org/docs/native-target-support.html
    // Tier 1
    linuxX64()
    macosX64()
    macosArm64()
    iosSimulatorArm64()
    iosX64()
    // Tier 2
    linuxArm64()
    watchosSimulatorArm64()
    watchosX64()
    watchosArm32()
    watchosArm64()
    tvosSimulatorArm64()
    tvosX64()
    tvosArm64()
    iosArm64()
    // Tier 3
    mingwX64()

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlinx.datetime)
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        jsTest {
            dependencies {
                implementation(npm("@js-joda/timezone", libs.versions.npm.joda.time.get()))
            }
        }
        wasmJsTest {
            dependencies {
                implementation(npm("@js-joda/timezone", libs.versions.npm.joda.time.get()))
            }
        }
    }
}

repositories {
    mavenCentral()
}

tasks.register("allDetekt") {
    allprojects {
        this@register.dependsOn(tasks.withType<Detekt>())
    }
}

dokka {
    dokkaSourceSets.commonMain {
        sourceLink {
            localDirectory.set(file("src/commonMain/kotlin"))
            remoteUrl("https://github.com/yoxjames/Kastro/blob/main/src/commonMain/kotlin")
            remoteLineSuffix.set("#L")
        }
    }
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    coordinates(groupId = project.group.toString(), artifactId = project.name)

    pom {
        name = project.name
        description = "Calculates the time of solar and lunar events"
        url = "http://www.jamesyox.dev/kastro"

        licenses {
            license {
                name = "Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }

        developers {
            developer {
                name = "James Yox"
                id = "yoxjames"
                url = "http://www.jamesyox.dev"
            }
        }

        scm {
            connection = "scm:git:github.com/yoxjames/Kastro.git"
            developerConnection = "scm:git:ssh://github.com/yoxjames/Kastro.git"
            url = "https://github.com/yoxjames/Kastro"
        }
    }

}

fun String.isNonStable(): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(this)
    return isStable.not()
}

tasks.dependencyUpdates {
    rejectVersionIf {
        candidate.version.isNonStable()
    }
}
