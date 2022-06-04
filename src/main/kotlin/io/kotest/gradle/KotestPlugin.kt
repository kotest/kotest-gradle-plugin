package io.kotest.gradle

import io.kotest.gradle.Constants.KOTEST_EXTENSION_NAME
import io.kotest.gradle.Constants.KOTLIN_JVM_PLUGIN_ID
import io.kotest.gradle.Constants.KOTLIN_MULTIPLATFORM_PLUGIN_ID
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

      var isMultiplatform = false
      project.plugins.withId(KOTLIN_MULTIPLATFORM_PLUGIN_ID) {
         isMultiplatform = true
         println("Detected Kotlin MPP")
         project.setupMppTests()
      }

      // if we detect the java plugin, this is probably a kotlin JVM project
      project.plugins.withId(KOTLIN_JVM_PLUGIN_ID) {
         if (!isMultiplatform) {
            project.javaTestSourceSet()?.let {
               project.createKotestTask("kotest", JavaPlugin.TEST_CLASSES_TASK_NAME, project.files(""))
            }
         }
      }
   }
}
