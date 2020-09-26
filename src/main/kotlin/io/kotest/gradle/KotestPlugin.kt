package io.kotest.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.JavaPlugin

open class KotestPlugin : Plugin<Project> {

   override fun apply(project: Project) {

      project.plugins.withId("org.jetbrains.kotlin.multiplatform") {
         project.mppTestTargets().forEach { (target, files) ->
            applyPlugin(
                project,
                target.targetName + "Kotest",
                target.targetName + "TestClasses",
                files
            )
         }
      }

      project.plugins.withType(JavaPlugin::class.java) {
         project.javaTestSourceSet()?.let {
            applyPlugin(project, "kotest", JavaPlugin.TEST_CLASSES_TASK_NAME, it.runtimeClasspath)
         }
      }
   }

   private fun applyPlugin(project: Project, taskName: String, dependentTask: String?, classpath: FileCollection) {
      if (project.tasks.none { it.name == taskName }) {
         val task = project.tasks.maybeCreate(taskName, KotestTask::class.java)
         task.classpath = classpath
         task.description = "Run Kotest"
         task.group = "verification"
         if (dependentTask != null && project.tasks.any { it.name == dependentTask })
            task.dependsOn(dependentTask)
      }
   }
}