package io.kotest.gradle

import org.gradle.api.GradleException
import org.gradle.api.internal.file.FileCollectionFactory
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.concurrent.ExecutorFactory
import org.gradle.process.internal.DefaultExecActionFactory
import org.gradle.process.internal.JavaExecAction
import org.gradle.process.internal.JavaForkOptionsFactory
import javax.inject.Inject

// gradle seems to require the class be open
open class KotestTask @Inject constructor(
    forkOptionsFactory: JavaForkOptionsFactory,
//    private val fileOps: FileSystemOperations,
    private val fileResolver: FileResolver,
    private val fileCollectionFactory: FileCollectionFactory,
    private val executorFactory: ExecutorFactory,
) : KotestAbstractTask(forkOptionsFactory) {

   private fun args() = if (isIntellij()) {
      listOf("--writer", "teamcity")
   } else {
      listOf("--writer", "mocha")
   }

   private fun isIntellij(): Boolean = try {
      Class.forName("com.intellij.rt.execution.CommandLineWrapper")
      true
   } catch (t: Throwable) {
      false
   }

   private fun exec(): JavaExecAction {
      val exec = DefaultExecActionFactory.of(fileResolver, fileCollectionFactory, executorFactory).newJavaExecAction()
      copyTo(exec)

      val javaConvention = project.convention.getPlugin(JavaPluginConvention::class.java)
      val testResultsDir = javaConvention.testResultsDir
      println("testResultsDir=$testResultsDir")

      val jvm = project.plugins.findPlugin("org.jetbrains.kotlin.jvm") != null
      val testSourceSetName = if (jvm) "test" else "jvmTest"
      val test = javaConvention.sourceSets.findByName(testSourceSetName)
          ?: error("Cannot find test sourceset '$testSourceSetName'")

      exec.main = "io.kotest.framework.launcher.LauncherKt"
      exec.classpath = test.runtimeClasspath
      exec.jvmArgs = allJvmArgs
      exec.args = args()
      // this must be true so we can handle the failure ourselves by throwing GradleException
      // otherwise we get a nasty stack trace from gradle
      exec.isIgnoreExitValue = true

      return exec
   }

   @TaskAction
   fun executeTests() {

      val testResultsDir = project.buildDir.resolve("test-results")

      val result = try {
         exec().execute()
      } catch (e: Exception) {
         println(e)
         e.printStackTrace()
         throw GradleException("Test process failed", e)
      }

      if (result.exitValue != 0) {
         throw GradleException("There were test failures")
      }
   }
}