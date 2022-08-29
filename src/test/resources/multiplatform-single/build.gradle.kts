plugins {
   kotlin("multiplatform") version "1.5.31"
   id("io.kotest")
}

kotlin {
   targets {
      linuxX64()
      mingwX64()
   }
}