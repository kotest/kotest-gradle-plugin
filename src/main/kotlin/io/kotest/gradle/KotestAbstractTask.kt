package io.kotest.gradle

import org.gradle.api.Action
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.ConventionTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.process.CommandLineArgumentProvider
import org.gradle.process.JavaDebugOptions
import org.gradle.process.JavaForkOptions
import org.gradle.process.ProcessForkOptions
import org.gradle.process.internal.JavaForkOptionsFactory
import java.io.File

abstract class KotestAbstractTask(forkOptionsFactory: JavaForkOptionsFactory) : ConventionTask(), JavaForkOptions {

   private val forkOptions = forkOptionsFactory.newDecoratedJavaForkOptions().apply {
      enableAssertions = true
   }

   @Input
   override fun getExecutable(): String = forkOptions.executable

   override fun setExecutable(executable: String?) {
      forkOptions.executable = executable
   }

   override fun setExecutable(executable: Any?) {
      forkOptions.setExecutable(executable)
   }

   override fun executable(executable: Any?): ProcessForkOptions {
      forkOptions.executable(executable)
      return this
   }

   @InputDirectory
   override fun getWorkingDir(): File = forkOptions.workingDir

   override fun setWorkingDir(dir: File?) {
      forkOptions.workingDir = dir
   }

   override fun setWorkingDir(dir: Any?) = forkOptions.setWorkingDir(dir)

   override fun workingDir(dir: Any?): ProcessForkOptions {
      forkOptions.workingDir(dir)
      return this
   }

   @Input
   override fun getEnvironment(): MutableMap<String, Any> = forkOptions.environment

   override fun setEnvironment(environmentVariables: MutableMap<String, *>?) {
      forkOptions.environment = environmentVariables
   }

   override fun environment(environmentVariables: MutableMap<String, *>?): ProcessForkOptions {
      forkOptions.environment(environmentVariables)
      return this
   }

   override fun environment(name: String?, value: Any?): ProcessForkOptions {
      forkOptions.environment(name, value)
      return this
   }

   override fun copyTo(target: JavaForkOptions?): JavaForkOptions {
      forkOptions.copyTo(target)
      return this
   }

   override fun copyTo(target: ProcessForkOptions?): ProcessForkOptions {
      forkOptions.copyTo(target)
      return this
   }

   override fun getSystemProperties(): MutableMap<String, Any> = forkOptions.systemProperties

   override fun setSystemProperties(properties: MutableMap<String, *>?) {
      forkOptions.systemProperties = properties
   }

   override fun systemProperties(properties: MutableMap<String, *>?): JavaForkOptions {
      forkOptions.systemProperties(properties)
      return this
   }

   override fun systemProperty(name: String?, value: Any?): JavaForkOptions {
      forkOptions.systemProperty(name, value)
      return this
   }

   override fun getDefaultCharacterEncoding(): String? = forkOptions.defaultCharacterEncoding

   override fun setDefaultCharacterEncoding(defaultCharacterEncoding: String?) {
      forkOptions.defaultCharacterEncoding = defaultCharacterEncoding
   }

   override fun getMinHeapSize(): String? = forkOptions.minHeapSize

   override fun setMinHeapSize(heapSize: String?) {
      forkOptions.minHeapSize = heapSize
   }

   override fun getMaxHeapSize(): String? = forkOptions.maxHeapSize

   override fun setMaxHeapSize(heapSize: String?) {
      forkOptions.maxHeapSize = heapSize
   }

   override fun getJvmArgs(): MutableList<String> = forkOptions.jvmArgs ?: mutableListOf()

   override fun setJvmArgs(arguments: MutableList<String>?) {
      forkOptions.jvmArgs = arguments
   }

   override fun setJvmArgs(arguments: MutableIterable<*>?) {
      forkOptions.setJvmArgs(arguments)
   }

   override fun jvmArgs(arguments: MutableIterable<*>?): JavaForkOptions {
      forkOptions.setJvmArgs(arguments)
      return this
   }

   override fun jvmArgs(vararg arguments: Any?): JavaForkOptions {
      forkOptions.setJvmArgs(arguments.toList())
      return this
   }

   override fun getJvmArgumentProviders(): MutableList<CommandLineArgumentProvider> = forkOptions.jvmArgumentProviders

   override fun getBootstrapClasspath(): FileCollection = forkOptions.bootstrapClasspath

   override fun setBootstrapClasspath(classpath: FileCollection?) {
      forkOptions.bootstrapClasspath = classpath
   }

   override fun bootstrapClasspath(vararg classpath: Any?): JavaForkOptions {
      forkOptions.bootstrapClasspath(classpath)
      return this
   }

   override fun getEnableAssertions(): Boolean = forkOptions.enableAssertions

   override fun setEnableAssertions(enabled: Boolean) {
      forkOptions.enableAssertions = enabled
   }

   override fun getDebug(): Boolean = forkOptions.debug
   override fun setDebug(enabled: Boolean) {
      forkOptions.debug = enabled
   }

   override fun getDebugOptions(): JavaDebugOptions = forkOptions.debugOptions

   override fun debugOptions(action: Action<JavaDebugOptions>?) {
      forkOptions.debugOptions(action)
   }

   override fun getAllJvmArgs(): MutableList<String> = forkOptions.allJvmArgs

   override fun setAllJvmArgs(arguments: MutableList<String>?) {
      forkOptions.allJvmArgs = arguments
   }

   override fun setAllJvmArgs(arguments: MutableIterable<*>?) {
      forkOptions.setAllJvmArgs(arguments)
   }

}