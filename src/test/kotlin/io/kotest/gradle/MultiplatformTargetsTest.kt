package io.kotest.gradle

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.string.shouldContain
import org.gradle.testkit.runner.GradleRunner
import java.io.File

class MultiplatformTargetsTest : WordSpec(
   {
      "Applying the plugin to a multiplatform project" should {
         val projectDir = File("src/test/resources/multiplatform-single")
         val tempFile = File("src/test/resources/multiplatform-single/output.txt")

         val gradleRun = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("tasks")
            .forwardStdOutput(tempFile.bufferedWriter())
            .build()

         "Have multiple kotest tasks" {
            val output = tempFile.readLines().joinToString()
            assertSoftly(output) {
               it shouldContain "linuxX64Kotest"
               it shouldContain "mingwX64Kotest"
            }
         }
      }
   }
)
