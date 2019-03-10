package io.kotlintest.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction

class KotlinTestPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    project.tasks.create("kotlintest", KotlinTestTask::class.java)
  }
}

class KotlinTestTask : DefaultTask() {
  @TaskAction
  fun runTests() {
    println("hello from sammy!")
  }
}