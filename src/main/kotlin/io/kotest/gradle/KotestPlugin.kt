package io.kotest.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

open class KotestPlugin : Plugin<Project> {
   override fun apply(project: Project) {
      val task = project.tasks.create("kotest", KotestTask::class.java)
      task.group = "verification"
      task.description = "Runs Kotest tests"
      task.dependsOn("classes", "testClasses")
   }
}