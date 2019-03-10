package io.kotlintest.gradle

import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.testing.Test

open class KotlinTestPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    val task = project.tasks.create("kotlintest", Test::class.java)
    task.actions.add(KotlinTestAction() as Action<in Task>)
  }
}