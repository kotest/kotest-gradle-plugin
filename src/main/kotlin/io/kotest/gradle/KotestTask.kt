package io.kotest.gradle

import groovy.lang.Closure
import io.kotest.gradle.helpers.kotest
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.file.FileCollectionFactory
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.testing.TestListener
import org.gradle.internal.concurrent.ExecutorFactory
import org.gradle.internal.jvm.Jvm
import org.gradle.jvm.toolchain.JavaLauncher
import org.gradle.process.JavaForkOptions
import org.gradle.process.ProcessForkOptions
import org.gradle.process.internal.DefaultExecActionFactory
import org.gradle.process.internal.JavaExecAction
import org.gradle.process.internal.JavaForkOptionsFactory
import javax.inject.Inject

// gradle seems to require the class be open
open class KotestTask @Inject constructor(
   private val fileResolver: FileResolver,
   private val fileCollectionFactory: FileCollectionFactory,
   private val executorFactory: ExecutorFactory,
   javaForkOptionsFactory: JavaForkOptionsFactory,
   objectFactory: ObjectFactory,
) : DefaultTask() {

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

   private var tags: String? = null
   private val listeners = mutableListOf<TestListener>()
   private val forkOptions = javaForkOptionsFactory.newDecoratedJavaForkOptions()
   private val javaLauncher = objectFactory.property(JavaLauncher::class.java)

   override fun configure(closure: Closure<*>): Task {
      return super.configure(closure)
   }

   private fun exec(classpath: FileCollection): JavaExecAction {
      val exec =
         DefaultExecActionFactory.of(fileResolver, fileCollectionFactory, executorFactory, null).newJavaExecAction()
      copyTo(exec)

      exec.mainClass.set("io.kotest.engine.launcher.MainKt")
      exec.classpath = classpath
      exec.jvmArgs = getAllJvmArgs()
      exec.args = args()
      // this must be true so we can handle the failure ourselves by throwing GradleException
      // otherwise we get a nasty stack trace from gradle
      exec.isIgnoreExitValue = true

      return exec
   }

   // -- reporter was added in 4.2.1
   private fun args() = when {
      isIntellij() -> listOf(ReporterArg, TeamCityReporter, TermArg, PlainColours) + tagArgs()
      else -> listOf(ReporterArg, TaycanReporter, TermArg, TrueColours) + tagArgs()
   }

   private fun copyTo(target: JavaForkOptions): KotestTask {
      forkOptions.copyTo(target)
      copyToolchainAsExecutable(target)
      return this
   }

   private fun copyToolchainAsExecutable(target: ProcessForkOptions) {
      target.executable = getEffectiveExecutable()
   }

   private fun getEffectiveExecutable(): String {
      if (javaLauncher.isPresent) {
         // The below line is OK because it will only be exercised in the Gradle daemon and not in the worker running tests.
         return javaLauncher.get().executablePath.toString()
      }
      return getExecutable() ?: Jvm.current().javaExecutable.absolutePath
   }

   private fun getExecutable(): String? {
      return forkOptions.executable
   }

   private fun getAllJvmArgs(): List<String> {
      return forkOptions.allJvmArgs
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

   /**
    * Returns true if we are running inside intellij.
    *
    * We detect this by looking to see if intellij added its own test listener to this task, which it
    * does for any task that extends Test.
    *
    * The intellij test listener is an instance of IJTestEventLogger.
    */
   private fun isIntellij(): Boolean {
      return listeners.map { it::class.java.name }.any { it.contains(IntellijTestListenerClassName) }
   }

//   private fun rerouteTeamCityListener(exec: JavaExecAction) {
//      val input = PipedInputStream()
//      exec.standardOutput = PipedOutputStream(input)
//      thread {
//         val root = DefaultTestSuiteDescriptor("root", "root")
//         listeners.forEach {
//            it.beforeSuite(root)
//         }
//         input.bufferedReader().useLines { lines ->
//            val parser = ServiceMessagesParser()
//            val callback = KotestServiceMessageParserCallback(root, listeners, outputListeners)
//            lines.forEach { parser.parse(it, callback) }
//         }
//         listeners.forEach {
//            it.afterSuite(root, DefaultTestResult(TestResult.ResultType.SUCCESS, 0, 0, 0, 0, 0, emptyList()))
//         }
//      }
//   }
//
//   @TaskAction
//   override fun executeTests() {
//      //val testResultsDir = project.buildDir.resolve("test-results")
//      val sourceset = project.javaTestSourceSet() ?: return
//      val result = try {
//         val exec = exec(sourceset.runtimeClasspath)
//         if (isIntellij()) rerouteTeamCityListener(exec)
//         exec.execute()
//      } catch (e: Exception) {
//         println(e)
//         e.printStackTrace()
//         throw GradleException("Test process failed", e)
//      }
//
//      if (result.exitValue != 0) {
//         throw GradleException("There were test failures")
//      }
//   }
}
