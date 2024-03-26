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

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import kotlin.time.Duration.Companion.seconds

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.detekt)
    alias(libs.plugins.dokka)
    alias(libs.plugins.benmanes.versions)
    alias(libs.plugins.kover)
    alias(libs.plugins.binarycompatibility)
    alias(libs.plugins.nexus)
    `maven-publish`
    signing
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
        jvmToolchain(libs.versions.jvm.get().toInt())
    }
    js(IR) {
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
        val commonMain by getting {
            dependencies {
                api(libs.kotlinx.datetime)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(npm("@js-joda/timezone", libs.versions.npm.joda.time.get()))
            }
        }
    }
}

allprojects {
    repositories {
        mavenCentral()
    }
}

tasks.register("allDetekt") {
    allprojects {
        this@register.dependsOn(tasks.withType<Detekt>())
    }
}

nexusPublishing {
    repositories {
        sonatype {
            val ossrhUsername: String? by project
            val ossrhPassword: String? by project
            val ossrhStagingProfileId: String? by project

            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))

            username = ossrhUsername
            password = ossrhPassword
            stagingProfileId = ossrhStagingProfileId
        }
    }
}

signing {
    isRequired = gradle.taskGraph.hasTask("publish")
    sign(publishing.publications)
}

publishing {
    publications.withType<MavenPublication> {
        groupId = project.group.toString()
        version = libs.versions.current.get()
        val publication = this
        val dokkaJar = tasks.register<Jar>("${publication.name}DokkaJar") {
            group = JavaBasePlugin.DOCUMENTATION_GROUP
            description = "Assembles Kotlin docs with Dokka into a Javadoc jar"
            archiveClassifier.set("javadoc")
            from(tasks.named("dokkaHtml"))
            // Each archive name should be distinct, to avoid implicit dependency issues.
            // We use the same format as the sources Jar tasks.
            // https://youtrack.jetbrains.com/issue/KT-46466
            archiveBaseName.set("${archiveBaseName.get()}-${publication.name}")
        }

        artifact(dokkaJar)

        pom {
            name = project.name
            description = "TODO"
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
}