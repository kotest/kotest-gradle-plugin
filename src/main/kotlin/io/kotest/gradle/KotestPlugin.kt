package io.kotest.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.JavaPlugin

open class KotestPlugin : Plugin<Project> {

   override fun apply(project: Project) {
      println("Applying kotest plugin to ${project.name}")

      if (project.extensions.findByName("kotest") == null) {
         project.extensions.create(
            "kotest",
            KotestExtension::class.java,
            project
         )
      }

      project.plugins.withId("org.jetbrains.kotlin.multiplatform") {
         println("Detected Kotlin MPP")
         project.mppTestTargets().forEach { (target, files) ->
            println("Found target $target")
            createKotestTask(
               project,
               target.targetName + "Kotest",
               target.targetName + "TestClasses",
               files
            )
         }
      }

//
// //            project.tasks.create("kotest", KotestTestTask::class.java).apply {
// //               description = "Run tests using Kotest"
// //               group = "verification"
// //               testLogging.exceptionFormat = TestExceptionFormat.SHORT
// //               testLogging.events = TestLogEvent.values().toSet()
// //            }
// //            }
//         }
//      }

      // if we detect the java plugin, this is probably a kotlin JVM project
      project.plugins.withType(JavaPlugin::class.java) {
         project.javaTestSourceSet()?.let {
            createKotestTask(project, "kotest", JavaPlugin.TEST_CLASSES_TASK_NAME, project.files(""))
         }
      }
   }

   private fun createKotestTask(project: Project, taskName: String, dependentTask: String?, files: FileCollection?) {
      if (project.tasks.none { it.name == taskName }) {
         println("Creating task $taskName")

         project.tasks.maybeCreate(taskName, KotestTask::class.java).apply {
            description = "Run Kotest"
            group = "verification"
            if (dependentTask != null && project.tasks.any { it.name == dependentTask }) {
               dependsOn(dependentTask)
            }
         }
      }
   }
}
