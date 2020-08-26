package io.kotest.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

open class KotestPlugin : Plugin<Project> {
   override fun apply(project: Project) {
      project.allprojects.forEach {
         if (it.tasks.none { task -> task.name == "kotest" }) {
            val task = it.tasks.create("kotest", KotestTask::class.java)
            task.group = "verification"
            task.description = "Runs Kotest tests"
            task.dependsOn("classes", "testClasses")
         }
      }
   }
}