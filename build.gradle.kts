// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "7.2.1" apply false
    id("com.android.library") version "7.2.1" apply false
    id("org.jetbrains.kotlin.android") version "1.7.10" apply false
    id("androidx.navigation.safeargs.kotlin") version "2.5.1" apply false
    id("org.jmailen.kotlinter") version "3.12.0" apply false
}

buildscript {
    repositories {
        google()
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
    dependencies {
        classpath("com.android.tools.build:gradle:${libs.versions.androidBuildGradle.get()}")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:${libs.versions.androidxNavigation.get()}")
        classpath("com.google.gms:google-services:${libs.versions.gmsGoogleService.get()}")
        classpath("com.google.firebase:firebase-crashlytics-gradle:${libs.versions.firebaseCrashlyticsGradle.get()}")
    }
}

tasks.create<Delete>("clean") {
    delete(rootProject.buildDir)
}