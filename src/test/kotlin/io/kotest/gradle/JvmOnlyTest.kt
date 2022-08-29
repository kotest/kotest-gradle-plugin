package io.kotest.gradle

import io.kotest.core.spec.style.WordSpec
import io.kotest.inspectors.forOne
import io.kotest.matchers.string.shouldStartWith
import org.gradle.testkit.runner.GradleRunner
import java.io.File

class JvmOnlyTest : WordSpec(
   {
      "Applying the plugin to a multiplatform project" should {
         val projectDir = File("src/test/resources/jvm")
         val tempFile = File("src/test/resources/jvm/output.txt")

         GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("tasks")
            .withPluginClasspath()
            .forwardStdOutput(tempFile.bufferedWriter())
            .build()

         "Have kotest tasks" {
            val lines = tempFile.readLines()
            lines.forOne {
               it shouldStartWith "kotest"
            }
         }
      }
   }
)
