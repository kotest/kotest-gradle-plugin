package io.kotest.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.internal.DefaultJavaPluginConvention
import org.gradle.api.tasks.SourceSet


open class KotestPlugin : Plugin<Project> {

   override fun apply(project: Project) {
      println("Project: ${project.name}")
      project.plugins.withType(JavaPlugin::class.java) {
         when (val java = project.convention.plugins["java"]) {
            is DefaultJavaPluginConvention -> {
               // detect the test sourceset and add a kotest task that will
               // execute against that test sourceset
               val testSourceSet = java.sourceSets.findByName("test")
               if (testSourceSet != null) {
                  applyPlugin(project, testSourceSet)
               }
            }
            else -> Unit
         }
      }
   }

   private fun applyPlugin(project: Project, sourceset: SourceSet) {
      if (project.tasks.none { task -> task.name == "kotest" }) {
         val task = project.tasks.create("kotest", KotestTask::class.java)
         task.group = "verification"
         task.description = "Run Kotest against ${sourceset.name} sourceset"

         val tasks = project.tasks.map { it.name }
         val potentialDependsOn = arrayOf("classes", "testClasses").filter { tasks.contains(it) }
         task.dependsOn(*potentialDependsOn.toTypedArray())
      }

   }
}