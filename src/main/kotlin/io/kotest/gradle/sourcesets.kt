package io.kotest.gradle

import io.kotest.gradle.Constants.KOTLIN_MULTIPLATFORM_PLUGIN_ID
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilationToRunnableFiles
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.androidJvm
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.common
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.js
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.jvm
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.native
import org.jetbrains.kotlin.gradle.plugin.KotlinTargetWithTests

fun Project.configureNativeCompilations() {
   afterEvaluate {
      plugins.withId(KOTLIN_MULTIPLATFORM_PLUGIN_ID) {
         val kotlinExt = extensions.getByType(KotlinMultiplatformExtension::class.java)

         kotlinExt.testableTargets
            .filter { it.platformType == native }
            .forEach { testTarget ->
               testTarget.compilations.configureEach {
                  it.kotlinOptions.freeCompilerArgs += listOf("-entry")
               }
            }
      }
   }
}

fun Project.setupMppTests() {
   afterEvaluate {
      plugins.withId(KOTLIN_MULTIPLATFORM_PLUGIN_ID) {
         println("Found multiplatform plugin")

         val kotlinExt = extensions.getByType(KotlinMultiplatformExtension::class.java)

         kotlinExt.testableTargets.forEach { testTarget ->
            createMppKotestTask(testTarget, testTarget.compiledFiles())
         }
      }
   }
}

private fun KotlinTargetWithTests<*, *>.compiledFiles(): FileCollection {
   val deps = compilations.map {
      when (it) {
         is KotlinCompilationToRunnableFiles -> it.runtimeDependencyFiles + it.compileDependencyFiles
         else -> it.compileDependencyFiles
      }
   }
   val outputs = compilations.map { it.output.allOutputs }
   return (deps + outputs).reduce { a, b -> a.plus(b) }
}

private fun Project.createMppKotestTask(target: KotlinTargetWithTests<*, *>, files: FileCollection?) {
   val taskToDependOn = when (target.platformType) {
      js, jvm, androidJvm -> "${target.targetName}TestClasses"
      native -> "${target.targetName}TestKlibrary"
      common -> error("Not sure what to depend on for common targets")
   }

   createKotestTask("${target.targetName}Kotest", target.platformType, taskToDependOn, files)
}

internal fun Project.createKotestTask(
   taskName: String,
   platformType: KotlinPlatformType,
   dependsOn: String,
   files: FileCollection?
) {
   if (tasks.none { it.name == taskName }) {
      println("Creating task $taskName")

      tasks.maybeCreate(taskName, KotestTask::class.java).apply {
         description = "Executes Kotest for the given target"
         group = "verification"
         this.files.set(files)
         this.platformType.set(platformType)
         dependsOn(dependsOn)

         this@createKotestTask.tasks.getByName("check").dependsOn(this)
      }
   }
}
