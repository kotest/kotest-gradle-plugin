package io.kotest.gradle

import org.gradle.api.GradleException
import org.gradle.api.internal.file.FileCollectionFactory
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet
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

   private fun args() = listOf("--termcolor", "true") + writerArg()

   // -- reporter was added in 4.2.1
   private fun writerArg() = if (isIntellij()) listOf("--reporter", "teamcity") else listOf("--reporter",
       "io.kotest.engine.reporter.TaycanConsoleReporter")

   private fun exec(sourceset: SourceSet): JavaExecAction {
      val exec = DefaultExecActionFactory.of(fileResolver, fileCollectionFactory, executorFactory).newJavaExecAction()
      copyTo(exec)

      exec.main = "io.kotest.engine.launcher.MainKt"
      exec.classpath = sourceset.runtimeClasspath
      exec.jvmArgs = allJvmArgs
      exec.args = args()
      // this must be true so we can handle the failure ourselves by throwing GradleException
      // otherwise we get a nasty stack trace from gradle
      exec.isIgnoreExitValue = true

      return exec
   }

   private fun testSourceSet(): SourceSet? {
      val javaConvention = project.convention.getPlugin(JavaPluginConvention::class.java) ?: return null
      val jvm = project.plugins.findPlugin("org.jetbrains.kotlin.jvm") != null
      return javaConvention.sourceSets.find { it.name == "test" || it.name == "jvmTest" }
   }

   @TaskAction
   fun executeTests() {

      val sourceSet = testSourceSet() ?: return

      //val testResultsDir = project.buildDir.resolve("test-results")

      val result = try {
         exec(sourceSet).execute()
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