package io.kotlintest.gradle

import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.tasks.testing.Test
import org.gradle.process.internal.DefaultExecActionFactory
import org.gradle.process.internal.JavaExecAction
import java.lang.Exception

class KotlinTestAction : Action<Test> {
  override fun execute(t: Test) {

    fun args(): List<String> {
      return listOf("--writer", "io.kotlintest.runner.console.MochaConsoleWriter")
    }

    fun exec(): JavaExecAction {
      val fileResolver = (t.project as ProjectInternal).services.get(FileResolver::class.java)
      val exec = DefaultExecActionFactory(fileResolver).newJavaExecAction()
      t.copyTo(exec)
      exec.main = "io.kotlintest.runner.console.LauncherKt"
      exec.classpath = t.classpath
      exec.jvmArgs = t.allJvmArgs
      exec.args = args()
      // this must be true so we can handle the failure ourselves by throwing GradleException
      // otherwise we get a nasty stack trace from gradle
      exec.isIgnoreExitValue = true
      return exec
    }

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