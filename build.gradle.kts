plugins {
   java
   `java-library`
   `maven-publish`
   `java-gradle-plugin`
   kotlin("jvm") version "1.4.32"
   id("com.gradle.plugin-publish") version "0.14.0"
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
   compileOnly(libs.kotlin.gradle.plugin)
   compileOnly(libs.kotest.framework.api)
   implementation(libs.kotest.framework.engine)
   implementation(libs.kotlinx.coroutines.core)

   testImplementation(libs.kotest.assertions.core)
   testImplementation(libs.kotest.runner.junit5)
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
