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

java {
   sourceCompatibility = JavaVersion.VERSION_1_8
   targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
   compileOnly(gradleApi())
   compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.21")
   compileOnly("io.kotest:kotest-framework-api-jvm:4.4.3")
   implementation("io.kotest:kotest-framework-engine-jvm:4.4.3")
   implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")

   testImplementation(Libs.Kotest.assertions)
   testImplementation(Libs.Kotest.junit5)
}

tasks.named<Test>("test") {
   useJUnitPlatform()
   filter {
      isFailOnNoMatchingTests = false
   }
   testLogging {
      showExceptions = true
      showStandardStreams = true
      events = setOf(
         org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
         org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
      )
      exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
   }
}

if (JavaVersion.current() != JavaVersion.VERSION_1_8) {
   tasks.withType<JavaCompile> {
      options.release.set(8)
   }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
   kotlinOptions {
      jvmTarget = "1.8"
   }
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
