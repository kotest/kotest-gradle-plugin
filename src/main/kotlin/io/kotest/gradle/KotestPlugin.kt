package io.kotest.gradle

import io.kotest.gradle.Constants.KOTEST_EXTENSION_NAME
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin

open class KotestPlugin : Plugin<Project> {

   override fun apply(project: Project) {
      println("Applying kotest plugin to ${project.name}")

      if (project.extensions.findByName("kotest") == null) {
         project.extensions.create(
            KOTEST_EXTENSION_NAME,
            KotestExtension::class.java,
            project
         )
      }

      project.plugins.withId("org.jetbrains.kotlin.multiplatform") {
         println("Detected Kotlin MPP")
         project.setupMppTests()
      }

      // if we detect the java plugin, this is probably a kotlin JVM project
      project.plugins.withType(JavaPlugin::class.java) {
         project.javaTestSourceSet()?.let {
//            createKotestTask(project, "kotest", JavaPlugin.TEST_CLASSES_TASK_NAME, project.files(""))
         }
      }
   }
}
