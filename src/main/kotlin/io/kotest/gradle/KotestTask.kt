package io.kotest.gradle

import org.gradle.api.GradleException
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.file.FileCollectionFactory
import org.gradle.api.internal.file.FileResolver
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
    private val executorFactory: ExecutorFactory
) : KotestAbstractTask(forkOptionsFactory) {

   // this must be set after the task is created
   var classpath: FileCollection? = null

   private fun args() = listOf("--termcolor", "true") + writerArg()

   // -- reporter was added in 4.2.1
   private fun writerArg() = if (isIntellij()) listOf("--reporter", "teamcity") else listOf("--reporter",
       "io.kotest.engine.reporter.TaycanConsoleReporter")

   private fun exec(): JavaExecAction {
      val exec = DefaultExecActionFactory.of(fileResolver, fileCollectionFactory, executorFactory).newJavaExecAction()
      copyTo(exec)

      exec.main = "io.kotest.engine.launcher.MainKt"
      exec.classpath = classpath
      exec.jvmArgs = allJvmArgs
      exec.args = args()
      // this must be true so we can handle the failure ourselves by throwing GradleException
      // otherwise we get a nasty stack trace from gradle
      exec.isIgnoreExitValue = true

      return exec
   }

   @TaskAction
   fun executeTests() {

      if (classpath == null) return
      //val testResultsDir = project.buildDir.resolve("test-results")

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