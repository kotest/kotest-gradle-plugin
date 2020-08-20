buildscript {
   repositories {
      mavenCentral()
      mavenLocal()
   }
}

plugins {
   java
   kotlin("jvm").version("1.4.0")
   id("java-library")
   id("maven-publish")
   id("java-gradle-plugin")
   id("com.gradle.plugin-publish").version("0.12.0")
}

repositories {
   mavenCentral()
   mavenLocal()
}

group = "io.kotest"
version = Ci.publishVersion

dependencies {
   compileOnly(gradleApi())
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
            implementationClass = "io.kotest.gradle.KotlinTestPlugin"
            displayName = "Gradle Kotest Runner"
            description = "Adds support to Gradle for running Kotest tests"
         }
      }
   }
}