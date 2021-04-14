package io.kotest.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin

open class KotestPlugin : Plugin<Project> {

   override fun apply(project: Project) {

      val extension = project.extensions.create(
         "kotest",
         KotestExtension::class.java,
         project
      )

//      project.plugins.withId("org.jetbrains.kotlin.multiplatform") {
//         project.mppTestTargets().forEach { (target, files) ->
//            applyPlugin(
//                project,
//                target.targetName + "Kotest",
//                target.targetName + "TestClasses",
//                files
//            )
//         }
//      }

//
////            project.tasks.create("kotest", KotestTestTask::class.java).apply {
////               description = "Run tests using Kotest"
////               group = "verification"
////               testLogging.exceptionFormat = TestExceptionFormat.SHORT
////               testLogging.events = TestLogEvent.values().toSet()
////            }
////            }
//         }
//      }

      // if we detect the java plugin, this is probably a kotlin JVM project
      project.plugins.withType(JavaPlugin::class.java) {
         project.javaTestSourceSet()?.let {
            applyPlugin(project, "kotest", JavaPlugin.TEST_CLASSES_TASK_NAME)
         }
      }
   }

   private fun applyPlugin(project: Project, taskName: String, dependentTask: String?) {
      if (project.tasks.none { it.name == taskName }) {
         val task = project.tasks.maybeCreate(taskName, Kotest::class.java)
         task.description = "Run Kotest"
         task.group = "verification"
         if (dependentTask != null && project.tasks.any { it.name == dependentTask })
            task.dependsOn(dependentTask)
         project.subprojects.forEach { applyPlugin(it, taskName, dependentTask) }
      }
   }
}

internal fun Project.kotest(): KotestExtension? {
   return when (val ext = extensions.findByName("kotest")) {
      is KotestExtension -> ext
      null -> null
      else -> throw IllegalStateException("kotest is not of the correct type")
   }
}