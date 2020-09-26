buildscript {
   repositories {
      mavenCentral()
      mavenLocal()
   }
}

plugins {
   java
   kotlin("jvm").version(Libs.KotlinVersion)
   id("java-library")
   id("maven-publish")
   id("java-gradle-plugin")
   id("com.gradle.plugin-publish").version(Libs.GradlePluginPublishVersion)
}

repositories {
   mavenCentral()
   mavenLocal()
}

group = "io.kotest"
version = Ci.publishVersion

dependencies {
   compileOnly(gradleApi())
   compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.10")
}

tasks {
   pluginBundle {
      website = "http://kotest.io"
      vcsUrl = "https://github.com/kotest"
      tags = listOf("kotest", "kotlin", "testing", "integrationTesting")
   }
   gradlePlugin {
      plugins {
         create("kotestPlugin") {
            id = "io.kotest"
            implementationClass = "io.kotest.gradle.KotestPlugin"
            displayName = "Gradle Kotest Runner"
            description = "Adds support to Gradle for running Kotest tests"
         }
      }
   }
}