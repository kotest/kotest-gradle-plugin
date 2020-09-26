package io.kotest.gradle

import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.internal.DefaultJavaPluginConvention
import org.gradle.api.tasks.SourceSet
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilationToRunnableFiles
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.KotlinTargetWithTests

fun Project.mppTestTargets(): Map<KotlinTargetWithTests<*, *>, FileCollection> {
   return when (val kotlin = project.extensions.getByName("kotlin")) {
      is KotlinMultiplatformExtension -> {
         kotlin.testableTargets.filter {
            // kotest plugin only supports JVM
            when (it.platformType) {
               KotlinPlatformType.jvm, KotlinPlatformType.androidJvm -> true
               else -> false
            }
         }.map { target ->
            val deps = target.compilations.map {
               when (it) {
                  is KotlinCompilationToRunnableFiles -> it.runtimeDependencyFiles + it.compileDependencyFiles
                  else -> it.compileDependencyFiles
               }
            }
            val outputs = target.compilations.map { it.output.allOutputs }
            val classpath = (deps + outputs).reduce { a, b -> a.plus(b) }
            Pair(target, classpath)
         }.toMap()
      }
      else -> emptyMap()
   }
}

fun Project.javaTestSourceSet(): SourceSet? {
   return when (val java = convention.plugins["java"]) {
      is DefaultJavaPluginConvention -> java.sourceSets.findByName("test")
      else -> null
   }
}