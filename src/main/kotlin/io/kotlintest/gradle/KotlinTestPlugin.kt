package io.kotlintest.gradle

import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.testing.Test

open class KotlinTestPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    val task = project.tasks.create("kotlintest", Test::class.java)
    task.actions.add(KotlinTestAction() as Action<in Task>)
  }
}


//open class KotlinTestPlugin : Plugin<Project> {
//  override fun apply(project: Project) {
//
//    val kotlintestJvm = project.tasks.create("kotlintest-jvm")
//    kotlintestJvm.actions.add(KotlinTestAction() as Action<in Task>)
//
//    val kotlintestJs = project.tasks.create("kotlintest-js")
//    val action: Action<in Task> = KotlinTestJsAction() as Action<in Task>
//    kotlintestJs.actions.add(action)
//  }
//}
//
//class KotlinTestAction : Action<Test> {
//  override fun execute(t: Test) {
//
//    t.project.components
//
//    fun args(): List<String> {
//      return listOf("--writer", "io.kotlintest.runner.console.DefaultConsoleWriter")
//    }
//
//    fun exec(): JavaExecAction {
//      val fileResolver = (t.project as ProjectInternal).services.get(FileResolver::class.java)
//      val exec = DefaultExecActionFactory(fileResolver).newJavaExecAction()
//      t.copyTo(exec)
//      exec.main = "io.kotlintest.runner.console.LauncherKt"
//      exec.classpath = t.classpath
//      exec.jvmArgs = t.allJvmArgs
//      exec.args = args()
//      exec.isIgnoreExitValue = true
//      return exec
//    }
//
//    val result = exec().execute()
//    if (result.exitValue != 0) {
//      throw GradleException("There were test failures")
//    }
//  }
//}