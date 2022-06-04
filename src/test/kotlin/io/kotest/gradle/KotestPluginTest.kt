package io.kotest.gradle

import io.kotest.core.spec.style.WordSpec
import io.kotest.gradle.assertions.shouldHavePlugin
import org.gradle.testfixtures.ProjectBuilder
import shouldHaveExtension
import shouldNotHaveTask

class KotestPluginTest : WordSpec({

   "Using the Plugin ID" should {
      val project = ProjectBuilder.builder().build()

      project.plugins.apply("io.kotest")

      "Not register any tasks since there were no applicable targets" {
         project.shouldNotHaveTask<KotestTask>()
      }

      "Register the Plugin" {
         project.shouldHavePlugin<KotestPlugin>()
      }

      "Register the 'kotest' extension" {
         project.shouldHaveExtension<KotestExtension>()
      }
   }
})
