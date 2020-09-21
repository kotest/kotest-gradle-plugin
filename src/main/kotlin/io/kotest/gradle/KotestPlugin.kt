package io.kotest.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

open class KotestPlugin : Plugin<Project> {
   override fun apply(project: Project) {
      project.subprojects.forEach { subproject ->
         if (subproject.tasks.none { task -> task.name == "kotest" }) {
            val task = subproject.tasks.create("kotest", KotestTask::class.java)
            task.group = "verification"
            task.description = "Runs Kotest tests"

            val tasks = subproject.tasks.map { it.name }
            val potentialDependsOn = arrayOf("classes", "testClasses").filter { tasks.contains(it) }.toTypedArray()
            task.dependsOn(*potentialDependsOn)
         }
      }
   }
}