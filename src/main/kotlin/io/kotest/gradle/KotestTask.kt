package io.kotest.gradle

import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.Task
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.file.FileTree
import org.gradle.api.internal.file.FileCollectionFactory
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.util.PatternSet
import org.gradle.internal.Factory
import org.gradle.internal.concurrent.ExecutorFactory
import org.gradle.process.internal.DefaultExecActionFactory
import org.gradle.process.internal.JavaExecAction
import org.gradle.process.internal.JavaForkOptionsFactory
import org.gradle.util.Path
import javax.inject.Inject

// gradle seems to require the class be open
open class KotestTask @Inject constructor(
    private val forkOptionsFactory: JavaForkOptionsFactory,
//    private val javaModuleDetector: JavaModuleDetector,
//    private val objectFactory: ObjectFactory,
    private val patternSetFactory: Factory<PatternSet>,
//    private val identity: TaskIdentity<KotestAbstractTask>,
    private val fileOps: FileSystemOperations,
    private val fileResolver: FileResolver,
    private val fileCollectionFactory: FileCollectionFactory,
    private val executorFactory: ExecutorFactory,
) : KotestAbstractTask(forkOptionsFactory) {

   private val testClassesDirs: FileCollection = project.objects.fileCollection()
   private val patternSet: PatternSet = patternSetFactory.create()!!
   private val binaryResultsDirectory = project.objects.directoryProperty()

   private fun args() = if (isIntellij()) {
      listOf("--writer", "mocha")
   } else {
      listOf("--writer", "teamcity")
   }

   private fun isIntellij(): Boolean = try {
      Class.forName("com.intellij.rt.execution.CommandLineWrapper")
      true
   } catch (t: Throwable) {
      false
   }

   private fun candidateClassFiles(): FileTree {
      return testClassesDirs.asFileTree.matching(patternSet)
   }

   private fun exec(): JavaExecAction {

      val exec = DefaultExecActionFactory.of(fileResolver, fileCollectionFactory, executorFactory).newJavaExecAction()
      copyTo(exec)

      val testResultsDir = project.buildDir.resolve("test-results")
      println(testResultsDir)

      val javaConvention = project.convention.getPlugin(JavaPluginConvention::class.java)
      val test = javaConvention.sourceSets.findByName("test") ?: error("Cannot find test sourceset")

      exec.main = "io.kotest.framework.launcher.LauncherKt"
      exec.classpath = test.runtimeClasspath
      exec.jvmArgs = allJvmArgs
      exec.args = args()
      // this must be true so we can handle the failure ourselves by throwing GradleException
      // otherwise we get a nasty stack trace from gradle
      exec.isIgnoreExitValue = true

      println("exec $exec")
      return exec
   }

   @TaskAction
   fun executeTests() {
      println("Kotest for the win intellij=" + isIntellij())

      // delete the output folder then recreate it
      val binaryResultsDir = binaryResultsDirectory.asFile.orNull
      println("Binary results dir $binaryResultsDir")
      //  fileOps.delete { it.delete(binaryResultsDir) }
      // binaryResultsDir?.mkdirs()
      println("qeqweqwewqeqew win !!!!")

      val result = try {
         exec().execute()
      } catch (e: Exception) {
         println(e)
         e.printStackTrace()
         throw GradleException("Test process failed", e)
      }

      if (result.exitValue != 0) {
         println("Launcher return value ${result.exitValue}")
         throw GradleException("There were test failures")
      }

//      addTestListener(testReportDataCollector);
//      addTestOutputListener(testReportDataCollector);

//      ProgressLogger parentProgressLogger = getProgressLoggerFactory ().newOperation(AbstractTestTask.class);
//      parentProgressLogger.setDescription("Test Execution");
//      parentProgressLogger.started();

//      TestExecuter<TestExecutionSpec> testExecuter = Cast . uncheckedNonnullCast (createTestExecuter());
//      TestListenerInternal resultProcessorDelegate = getTestListenerInternalBroadcaster ().getSource();
//      if (failFast) {
//         resultProcessorDelegate =  FailFastTestListenerInternal (testExecuter, resultProcessorDelegate);
//      }


   }
}