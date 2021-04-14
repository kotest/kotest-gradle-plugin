package io.kotest.gradle

import jetbrains.buildServer.messages.serviceMessages.ServiceMessagesParser
import org.gradle.api.GradleException
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.file.FileCollectionFactory
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.internal.tasks.testing.DefaultTestSuiteDescriptor
import org.gradle.api.internal.tasks.testing.results.DefaultTestResult
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.TestListener
import org.gradle.api.tasks.testing.TestOutputListener
import org.gradle.api.tasks.testing.TestResult
import org.gradle.internal.concurrent.ExecutorFactory
import org.gradle.process.internal.DefaultExecActionFactory
import org.gradle.process.internal.JavaExecAction
import java.io.PipedInputStream
import java.io.PipedOutputStream
import javax.inject.Inject
import kotlin.concurrent.thread

// gradle seems to require the class be open
open class Kotest @Inject constructor(
    private val fileResolver: FileResolver,
    private val fileCollectionFactory: FileCollectionFactory,
    private val executorFactory: ExecutorFactory
) : Test() {

   companion object {
      private const val IntellijTestListenerClassName = "IJTestEventLogger"
      private const val ReporterArg = "--reporter"
      private const val TermArg = "--termcolor"
      private const val TagsArg = "--tags"
      private const val TeamCityReporter = "teamcity"
      private const val TaycanReporter = "io.kotest.engine.reporter.TaycanConsoleReporter"
      private const val PlainColours = "ansi16"
      private const val TrueColours = "ansi256"
   }

   private val listeners = mutableListOf<TestListener>()
   private val outputListeners = mutableListOf<TestOutputListener>()

   private var tags: String? = null

   // gradle will call this if --tags was specified on the command line
   @Option(option = "tags", description = "Set tag expression to include or exclude tests")
   fun setTags(tags: String) {
      this.tags = tags
   }

   // intellij will call this to register its listeners for the test event run window
   override fun addTestListener(listener: TestListener) {
      listeners.add(listener)
   }

   override fun addTestOutputListener(listener: TestOutputListener) {
      outputListeners.add(listener)
   }

   /**
    * Returns args to be used for the tag expression.
    *
    * If --tags was passed as a command line arg, then that takes precedence over the value
    * set in the gradle build.
    *
    * Returns empty list if no tag expression was specified.
    */
   private fun tagArgs(): List<String> {
      tags?.let { return listOf(TagsArg, it) }
      project.kotest()?.tags?.orNull?.let { return listOf(TagsArg, it) }
      return emptyList()
   }

   // -- reporter was added in 4.2.1
   private fun args() = when {
      isIntellij() -> listOf(ReporterArg, TeamCityReporter, TermArg, PlainColours) + tagArgs()
      else -> listOf(ReporterArg, TaycanReporter, TermArg, TrueColours) + tagArgs()
   }

   private fun exec(classpath: FileCollection): JavaExecAction {
      val exec =
         DefaultExecActionFactory.of(fileResolver, fileCollectionFactory, executorFactory, null).newJavaExecAction()
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

   /**
    * Returns true if we are running inside intellij.
    * We detect this by looking to see if intellij added it's own test listener to this task, which it
    * does for any task tat extends Test.
    * The intellij test listener is an instance of IJTestEventLogger.
    */
   private fun isIntellij(): Boolean {
      return listeners.map { it::class.java.name }.any { it.contains(IntellijTestListenerClassName) }
   }

   private fun rerouteTeamCityListener(exec: JavaExecAction) {
      val input = PipedInputStream()
      exec.standardOutput = PipedOutputStream(input)
      thread {
         val root = DefaultTestSuiteDescriptor("root", "root")
         listeners.forEach {
            it.beforeSuite(root)
         }
         input.bufferedReader().useLines { lines ->
            val parser = ServiceMessagesParser()
            val callback = KotestServiceMessageParserCallback(root, listeners, outputListeners)
            lines.forEach { parser.parse(it, callback) }
         }
         listeners.forEach {
            it.afterSuite(root, DefaultTestResult(TestResult.ResultType.SUCCESS, 0, 0, 0, 0, 0, emptyList()))
         }
      }
   }

   @TaskAction
   override fun executeTests() {
      //val testResultsDir = project.buildDir.resolve("test-results")
      val sourceset = project.javaTestSourceSet() ?: return
      val result = try {
         val exec = exec(sourceset.runtimeClasspath)
         if (isIntellij()) rerouteTeamCityListener(exec)
         exec.execute()
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
