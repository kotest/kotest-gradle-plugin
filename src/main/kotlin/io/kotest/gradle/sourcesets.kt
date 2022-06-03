package io.kotest.gradle

import io.kotest.gradle.Constants.KOTLIN_MULTIPLATFORM_PLUGIN_ID
import org.gradle.api.Named
import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.logging.LogLevel
import org.gradle.api.plugins.internal.DefaultJavaPluginConvention
import org.gradle.api.tasks.SourceSet
import org.gradle.internal.extensibility.DefaultConvention
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilationToRunnableFiles
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.KotlinTargetWithTests

private val MPP_LOG_LEVEL = LogLevel.INFO

fun Project.mppTestTargets(): Map<KotlinTargetWithTests<*, *>, FileCollection> {

   project.afterEvaluate {
      project.plugins.withId(KOTLIN_MULTIPLATFORM_PLUGIN_ID) { plugin ->
         println("Found multiplatform plugin")

         val kotlinExt = project.extensions.getByName("kotlin")

         @Suppress("UNCHECKED_CAST")

         val sourceSets =
            kotlinExt.javaClass.getMethod("getSourceSets").invoke(kotlinExt) as NamedDomainObjectCollection<out Named>
         sourceSets.forEach {
            println(it.name)
         }
      }
   }

   logger.log(MPP_LOG_LEVEL, "exts=" + project.extensions)
   val ext = project.extensions as DefaultConvention
   logger.log(MPP_LOG_LEVEL, "map=" + ext.asMap)

   return when (val kotlin = project.extensions.getByName("kotlin")) {
      is KotlinMultiplatformExtension -> {
         logger.log(MPP_LOG_LEVEL, "Detected mpp kotlin $kotlin")
         logger.log(MPP_LOG_LEVEL, "kotlin.sourceSets ${kotlin.sourceSets.map { it.name }}")
         logger.log(MPP_LOG_LEVEL, "kotlin.targets ${kotlin.targets.map { it.targetName + " " + it.platformType }}")
         logger.log(
            MPP_LOG_LEVEL,
            "kotlin.testableTargets ${kotlin.testableTargets.map { it.targetName + " " + it.platformType }}"
         )
         kotlin.testableTargets.filter {
            // kotest plugin only supports JVM
            when (it.platformType) {
               KotlinPlatformType.jvm, KotlinPlatformType.androidJvm -> true
               else -> false
            }
         }.associateWith { target ->
            logger.log(MPP_LOG_LEVEL, "Detected mpp target $target")
            val deps = target.compilations.map {
               when (it) {
                  is KotlinCompilationToRunnableFiles -> it.runtimeDependencyFiles + it.compileDependencyFiles
                  else -> it.compileDependencyFiles
               }
            }
            val outputs = target.compilations.map { it.output.allOutputs }
            (deps + outputs).reduce { a, b -> a.plus(b) }
         }
      }
      is KotlinProjectExtension -> {
         logger.log(MPP_LOG_LEVEL, "kotlin KotlinProjectExtension $kotlin")
         emptyMap()
      }
      else -> emptyMap()
   }
}

fun Project.javaTestSourceSet(): SourceSet? {
   return when (val java = convention.plugins["java"]) {
      is DefaultJavaPluginConvention -> {
         java.sourceSets.findByName("test")
      }
      else -> null
   }
}
