package io.kotlintest.gradle

import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.tasks.testing.Test
import org.gradle.process.internal.DefaultExecActionFactory
import org.gradle.process.internal.JavaExecAction

class KotlinTestAction : Action<Test> {
  override fun execute(t: Test) {

    t.project.components

    fun args(): List<String> {
      return listOf("--writer", "io.kotlintest.runner.console.DefaultConsoleWriter")
    }

    fun exec(): JavaExecAction {
      val fileResolver = (t.project as ProjectInternal).services.get(FileResolver::class.java)
      val exec = DefaultExecActionFactory(fileResolver).newJavaExecAction()
      t.copyTo(exec)
      exec.main = "io.kotlintest.runner.console.LauncherKt"
      exec.classpath = t.classpath
      exec.jvmArgs = t.allJvmArgs
      exec.args = args()
      exec.isIgnoreExitValue = true
      return exec
    }

    val result = exec().execute()
    if (result.exitValue != 0) {
      throw GradleException("There were test failures")
    }
  }
}