plugins {
   kotlin("multiplatform") version "1.5.31"
   id("io.kotest") version "0.4-LOCAL"
}

repositories {
   mavenLocal()
   mavenCentral()
}

kotlin {
   targets {
      linuxX64()
      mingwX64()
   }
}