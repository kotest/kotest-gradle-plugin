package io.kotest.gradle

import io.kotest.core.spec.style.WordSpec
import io.kotest.gradle.helpers.kotest
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldNotBe
import org.gradle.testfixtures.ProjectBuilder

class KotestPluginTest : WordSpec({

   "Using the Plugin ID" should {

      "Apply the Plugin" {
         val project = ProjectBuilder.builder()
            .build()
//         project.plugins.apply("io.kotest")
//         project.pluginManager.apply("io.kotest")

         project.tasks.findByName("kotest").shouldNotBeNull()

         project.plugins.getPlugin(KotestPlugin::class.java) shouldNotBe null
      }

      "Register the 'kotest' extension" {
         val project = ProjectBuilder.builder().build()
         project.pluginManager.apply(KotestPlugin::class.java)

         project.kotest() shouldNotBe null
      }
   }
})
